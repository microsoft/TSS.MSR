package tss;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

import org.bouncycastle.asn1.eac.ECDSAPublicKey;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECCurve;

import tss.tpm.*;

/**
 * Interfaces to crypto functions (mostly using Bouncy Castle)
 * @author pengland
 *
 */
public class Crypto {

	static 
	{
		Security.addProvider(new BouncyCastleProvider());
	}
	
/**
 *  Return the size in bytes of a hash algorithm based on the TPM algId
 *  
 * @param alg The algorithm
 * @return the size in bytes
 */
	public static int digestSize(TPM_ALG_ID alg) {
		switch (alg.asEnum()) {
		case SHA1:
			return 20;
		case SHA256:
			return 32;
		case SHA384:
			return 48;
		case SHA512:
			return 64;
		default:
			throw new RuntimeException("Unknown algorithm ID (not a hash?)");
		}
	}
/**
 * Hash data
 * 
 * @param alg The hash algorithm
 * @param data The data to hash
 * @return The digest value
 */
	public static byte[] hash(TPM_ALG_ID alg, byte[] data) {
		Digest d = getDigest(alg);
		byte[] res = new byte[d.getDigestSize()];
		d.update(data, 0, data.length);
		d.doFinal(res, 0);
		return res;
	}

	/**
	 * hmac data
	 * 
	 * @param alg The hash algorithm
	 * @param key The HMAC key
	 * @param data The data to hash
	 * @return The digest value
	 */public static byte[] hmac(TPM_ALG_ID alg, byte[] key, byte[] data) {
		HMac h = new HMac(getDigest(alg));

		byte[] result = new byte[h.getMacSize()];
		KeyParameter kp = new KeyParameter(key);
		h.init(kp);
		h.update(data, 0, data.length);
		h.doFinal(result, 0);
		return result;
	}

	/**
	 * Validate a TPM signature. Note that this function hashes
	 * dataThatWasSigned before verifying the signature.
	 * 
	 * @param _pubKey The TPM public key
	 * @param _dataThatWasSigned The data that was hashed then signed
	 * @param _signature The signature returned by the TPM
	 * @return Whether the signature was valid
	 */
	public static boolean validateSignature(TPMT_PUBLIC _pubKey, byte[] _dataThatWasSigned, TPMU_SIGNATURE _signature) {

		if (_pubKey.parameters instanceof TPMS_RSA_PARMS) {
			TPMS_RSA_PARMS rsaParms = (TPMS_RSA_PARMS) _pubKey.parameters;
			TPM2B_PUBLIC_KEY_RSA rsaPubKey = (TPM2B_PUBLIC_KEY_RSA) (_pubKey.unique);
			int exponent = rsaParms.exponent;
			BigInteger exp = BigInteger.valueOf(exponent);
			BigInteger pub = new BigInteger(1, rsaPubKey.buffer);
			RSAKeyParameters pubKey = new RSAKeyParameters(false, pub, exp);

			if (rsaParms.scheme instanceof TPMS_SIG_SCHEME_RSAPSS) {
				TPMS_SIGNATURE_RSAPSS theRsaSig = (TPMS_SIGNATURE_RSAPSS) _signature;

				TPMS_SIG_SCHEME_RSAPSS scheme = (TPMS_SIG_SCHEME_RSAPSS) rsaParms.scheme;
				TPM_ALG_ID hashAlg = scheme.hashAlg;

				// todo - not working
				// bugbug - salt size
				AsymmetricBlockCipher rsaEngine = new RSABlindedEngine();
				rsaEngine.init(false, pubKey);
/*
				PSSSigner signerX = new PSSSigner(rsaEngine, getDigest(hashAlg), 48);

				signerX.init(false, pubKey);
				signerX.update(_dataThatWasSigned, 0, _dataThatWasSigned.length);
				boolean sigOkX = signerX.verifySignature(theRsaSig.sig);
*/
				RSADigestSigner signer = new RSADigestSigner(getDigest(theRsaSig.hash));
				signer.init(false, pubKey);
				signer.update(_dataThatWasSigned, 0, _dataThatWasSigned.length);
				boolean sigOk = signer.verifySignature(theRsaSig.sig);

				return sigOk;
			}
			if (rsaParms.scheme instanceof TPMS_SIG_SCHEME_RSASSA) {

				TPMS_SIGNATURE_RSASSA theRsaSig = (TPMS_SIGNATURE_RSASSA) _signature;
				TPMS_SIG_SCHEME_RSASSA scheme = (TPMS_SIG_SCHEME_RSASSA) rsaParms.scheme;
				TPM_ALG_ID hashAlg = scheme.hashAlg;

				RSADigestSigner signer = new RSADigestSigner(getDigest(hashAlg));
				signer.init(false, pubKey);
				signer.update(_dataThatWasSigned, 0, _dataThatWasSigned.length);
				Boolean sigOk = signer.verifySignature(theRsaSig.sig);
				return sigOk;
			}
		}
		if (_pubKey.parameters instanceof TPMS_ECC_PARMS) 
		{
			// TODO: not yet working...
			TPMS_ECC_PARMS eccParms = (TPMS_ECC_PARMS) _pubKey.parameters;
			//TPMS_ECC_POINT eccPubKey = (TPMS_ECC_POINT) (_pubKey.unique);
			if (eccParms.scheme instanceof TPMS_SIG_SCHEME_ECDSA) 
			{
				/*
				TPMS_SIG_SCHEME_ECDSA scheme = (TPMS_SIG_SCHEME_ECDSA) eccParms.scheme;

				ECDSAPublicKey pubKey = new ECDSAPublicKey(null, null, null, null, _dataThatWasSigned, null, _dataThatWasSigned, 0);
				
						String name = "secp256r1";

				
			       // === NOT PART OF THE CODE, JUST GETTING TEST VECTOR ===
		        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "BC");
		        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec(name);
		        kpg.initialize(ecGenParameterSpec);
		        ECPublicKey key = (ECPublicKey) kpg.generateKeyPair().getPublic();
		        byte[] x = key.getW().getAffineX().toByteArray();
		        byte[] y = key.getW().getAffineY().toByteArray();

		        // === here the magic happens ===
		        KeyFactory eckf = KeyFactory.getInstance("EC");
		        ECPoint point = new ECPoint(new BigInteger(1, x), new BigInteger(1, y));
		        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(name);
		        ECParameterSpec spec = new ECNamedCurveSpec(name, parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH(), parameterSpec.getSeed());
		        ECPublicKey ecPublicKey = (ECPublicKey) eckf.generatePublic(new ECPublicKeySpec(point, spec));
		        
				
				
				AsymmetricKeyParameter pubKey = new EC
				AsymmetricCipherKeyPair kp = new AsymmetricCipherKeyPair(null, null)
				
				ECDSASigner verifier = new ECDSASigner();
				verifier.init(false, param);
				
		        ParametersWithRandom param = new ParametersWithRandom(priKey, k);

		        CipherParameters parms = new CipherParameters()
				verifier.init(false, param);
				ISigner signer = SignerUtilities.GetSigner("SHA-256withECDSA"); 

				ECDSADigestSigner 
				
			*/	
			return true;
			}
		

			throw new RuntimeException("Not implemented");
		}
		
		
		
		throw new RuntimeException("Not implemented");
	};


	/**
	 * Validate a TPM quote against a set of PCR and a nonce.
	 * 
	 * @param pubKey The public key to use to validate the Quote
	 * @param expectedPcrs PCR values expected
	 * @param nonce The nonce
	 * @param quote The TPM generated quote
	 * @return Whether the quote was valid
	 * 
	 */
	public static boolean validateQuote(TPMT_PUBLIC pubKey, PCR_ReadResponse expectedPcrs, byte[] nonce, QuoteResponse quote)
		{
		    TPMS_ATTEST attest = quote.quoted;
		    if (attest.magic != TPM_GENERATED.VALUE) 
		    {
		        return false;
		    }
		    if (!Helpers.byteArraysEqual(attest.extraData, nonce)) 
		    {
		        return false;
		    }
		    TPMS_QUOTE_INFO quoteInfo = (TPMS_QUOTE_INFO) attest.attested;
		    if(quoteInfo.pcrSelect.length!= expectedPcrs.pcrSelectionOut.length)
		    {
		    	return false;
		    }
	    	// todo - the TPM spec doesn't define a unique encoding for the bitmap
		    byte[] quoteSelect = OutByteBuf.arrayToByteBuf(quoteInfo.pcrSelect);
		    byte[] expectedPcrSelect = OutByteBuf.arrayToByteBuf(expectedPcrs.pcrSelectionOut);
		    if(!Helpers.byteArraysEqual(quoteSelect, expectedPcrSelect))
		    {
		    	return false;
		    }
		    // Calculate the PCR-value hash and check the quote is the same
		    OutByteBuf pcrBuf = new OutByteBuf();
		    for (int j = 0; j < expectedPcrs.pcrValues.length; j++) 
		    {
		    	pcrBuf.write(expectedPcrs.pcrValues[j].buffer);
		    }

			TPM_ALG_ID hashAlg = Crypto.getSigningHashAlg(pubKey);
		    byte[] pcrHash = Crypto.hash(hashAlg,  pcrBuf.getBuf());
			if(!Helpers.byteArraysEqual(pcrHash, quoteInfo.pcrDigest ))
			{
				return false;
			}
		    // And finally check the signature
		    byte[] signedBlob = quote.quoted.toTpm();
		    Boolean quoteOk = Crypto.validateSignature(pubKey,  signedBlob,  quote.signature);
		    return quoteOk;
		}


	
	
	public static ECPublicKey decodeKey(byte[] encoded) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException{
	    // todo - not working
		
		X9ECParameters params = ECNamedCurveTable.getByName("secp256k1");
	    KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
	    ECCurve curve = params.getCurve();
	    java.security.spec.EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, params.getSeed());
	    java.security.spec.ECPoint point = ECPointUtil.decodePoint(ellipticCurve, encoded);
	    //java.security.spec.ECParameterSpec params2 =EC5Util.convertSpec(ellipticCurve, params);
	    //java.security.spec.ECPublicKeySpec keySpec = new java.security.spec.ECPublicKeySpec(point,params2);
	    //return (ECPublicKey) fact.generatePublic(keySpec);
	    return null;
	}
	
	
	/**
	 * Gets a Bouncy Castle Digest object that matches the TPM_ALG_ID hash algId 
	 * 
	 * @param alg The TPM hash algId
	 * @return A new Bouncy Castle hash object
	 */
	public static Digest getDigest(TPM_ALG_ID alg) {
		switch (alg.asEnum()) {
		case SHA1:
			return new SHA1Digest();
		case SHA256:
			return new SHA256Digest();
		case SHA384:
			return new SHA384Digest();
		case SHA512:
			return new SHA512Digest();
		default:
			throw new RuntimeException("No such digest");
		}

	}

	/**
	 * Perform the TPM key derivation procedure KDFa
	 * 
	 * @param hmacHash The underlying hash algorithm
	 * @param hmacKey The HMAC key to use for key derivation
	 * @param label The label value (note: the label is the zero-terminated UTC-encoded string)
	 * @param contextU The first context value
	 * @param contextV The second context value
	 * @param numBitsRequired The number of bits to return (must be a whole number of bytes)
	 * @return The KDFa-derived key
	 */
	public static byte[] KDFa(TPM_ALG_ID hmacHash, byte[] hmacKey, String label, byte[] contextU, byte[] contextV,
			int numBitsRequired) {
		int bitsPerLoop = digestSize(hmacHash) * 8;
		long numLoops = (numBitsRequired + bitsPerLoop - 1) / bitsPerLoop;
		byte[] kdfStream = new byte[(int) (numLoops * bitsPerLoop / 8)];
		for (int j = 0; j < numLoops; j++) {
			byte[] toHmac = Helpers.concatenate(
					new byte[][] { 
						Helpers.hostToNet(j + 1), 
						stringToLabel(label),
						contextU, 
						contextV, 
						Helpers.hostToNet(numBitsRequired) });
			byte[] fragment = hmac(hmacHash, hmacKey, toHmac);
			System.arraycopy(fragment, 0, kdfStream, j * bitsPerLoop / 8, fragment.length);
		}
		return Helpers.shiftRight(kdfStream, (int) (bitsPerLoop * numLoops - numBitsRequired));
	}

	/**
	 * RSA encrypt using the OAEP encoding
	 * @param parms The encryption parameters to use
	 * @param key The public key to use
	 * @param data The data to encrypt
	 * @param hashAlg The hash algorithm to use in the OAEP encoding
	 * @param encodingLabel The label to use (the label will be the zero-terminated UTC-encoded string)
	 * @return data encrypted with key
	 */
	public static byte[] oaepEncrypt(TPMS_RSA_PARMS parms, TPM2B_PUBLIC_KEY_RSA key, byte[] data, TPM_ALG_ID hashAlg,
			String encodingLabel) {
		byte[] encodingParms = stringToLabel(encodingLabel);
		int exponent = parms.exponent;
		if (exponent == 0)
			exponent = 65537;

		BigInteger exp = BigInteger.valueOf(exponent);
		BigInteger pub = new BigInteger(1, key.buffer);
		RSAKeyParameters pubKey = new RSAKeyParameters(false, pub, exp);

		try 
		{
			AsymmetricBlockCipher cipher = new OAEPEncoding(new RSAEngine(), getDigest(hashAlg), encodingParms);
			cipher.init(true, new ParametersWithRandom(pubKey));
			byte[] outX = cipher.processBlock(data, 0, data.length);
			return outX;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Encoding failed");
		}

	}

	public static byte[] asymEncrypt(TPMT_PUBLIC _pub, byte[] data, String encodingParms)
	{
		return oaepEncrypt((TPMS_RSA_PARMS) _pub.parameters, (TPM2B_PUBLIC_KEY_RSA ) _pub.unique, data, _pub.nameAlg, encodingParms);
	}
	
	/**
	 * Encode the string s as a TPM label (used in OAEP and other encodings.)  The label is the 
	 * zero-terminated UTF-8-encoded string
	 * 
	 * @param s The label
	 * @return The encoded label
	 */
	static byte[] stringToLabel(String s) {
		return Helpers.concatenate(Charset.forName("UTF-8").encode(s).array(), new byte[] { 0 });
	}


	
	/**
	 * Extract the signing hash algorithm from various supported schemes
	 * @param pub The public area to examine
	 * @return The hash algId
	 */
	public static TPM_ALG_ID getSigningHashAlg(TPMT_PUBLIC pub)
	{
	    if(pub.parameters instanceof TPMS_RSA_PARMS)
	    {
	    	TPMS_RSA_PARMS rsaParms = (TPMS_RSA_PARMS) pub.parameters;
	    	if(rsaParms.scheme instanceof TPMS_SIG_SCHEME_RSASSA) return ((TPMS_SIG_SCHEME_RSASSA)rsaParms.scheme).hashAlg;
	    	if(rsaParms.scheme instanceof TPMS_SIG_SCHEME_RSAPSS) return ((TPMS_SIG_SCHEME_RSAPSS)rsaParms.scheme).hashAlg;
	    	throw new RuntimeException("Unsupported scheme");
	    }
	    throw new RuntimeException("Unsupported algorithm");
	}
	

	public static byte[] cfbEncrypt(
			boolean _encrypt, 
			TPM_ALG_ID _algId,
			byte[] _key,
			byte[] _iv,
			byte[] _x)
	{
		if(_algId!=TPM_ALG_ID.AES)
		{
			throw new TpmException("Only AES is supported");
		}
		int symKeySize = _key.length*8;
		byte[] iv = (_iv==null)? new byte[0]: _iv;
		CFBBlockCipher encryptCipher = new CFBBlockCipher(new AESEngine(), symKeySize);
		KeyParameter key = new KeyParameter(_key);
		encryptCipher.init(_encrypt, new ParametersWithIV(key, iv));
		byte[] encData = new byte[_x.length];
		int numEncrypted = encryptCipher.processBytes(_x, 0,_x.length, encData, 0);
		if(numEncrypted!= _x.length)
		{
			throw new RuntimeException("Error!");
		}
		return encData;
	}

	public static RsaKeyPair createRsaKey(int keySize, int exponent)
	{
		try
		{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	        keyGen.initialize(keySize);
	        KeyPair key = keyGen.generateKeyPair();
	        
	        RSAPrivateCrtKey priv = (RSAPrivateCrtKey) key.getPrivate();
	        RSAPublicKey pub = (RSAPublicKey) key.getPublic();
	        RsaKeyPair newKey = new RsaKeyPair();
	        newKey.PublicKey = pub.getModulus();
	        newKey.PrivateKey = priv.getPrimeP();
	        return newKey;
		}
		catch(Exception e)
		{
			throw new TpmException("Bad alg:", e);
		}
	}
	public static ECCKeyPair createECCKey(TPM_ECC_CURVE curveId, TPM_ALG_ID alg)
	{
		try
		{
			ECCKeyPair newKey = new ECCKeyPair();

/*
			X9ECParameters ecSpec = ECNamedCurveTable.getByName(ecTpmToBc(curveId));
			KeyPairGenerator gen = KeyPairGenerator.getInstance("ECDSA", "BC");
            //SecureRandom random = new SecureRandom(new byte[16]);
//			gen.initialize(ecSpec, random);
			KeyPair pairgg = gen.generateKeyPair();
			
			BCECPrivateKey priv = (BCECPrivateKey)pairgg.getPrivate();
			BCECPublicKey pub = (BCECPublicKey)pairgg.getPublic();

			newKey.PublicKey = pub.getQ();
			newKey.PrivateKey = priv.getD();
	*/
			ECGenParameterSpec     ecGenSpec = new ECGenParameterSpec(ecTpmToBc(curveId));
			KeyPairGenerator    g = KeyPairGenerator.getInstance(ecTpmToBc(alg), "BC");
			g.initialize(ecGenSpec, new SecureRandom());
			KeyPair pairX = g.generateKeyPair();

			BCECPrivateKey priv = (BCECPrivateKey)pairX.getPrivate();
			BCECPublicKey pub = (BCECPublicKey)pairX.getPublic();
			
			newKey.PublicKey = pub.getQ();
			newKey.PrivateKey = priv.getD();
			
			
			
			return newKey;
			
			/*
			int keyStrength = ecTpmKeyStrength(curveId);
            SecureRandom random = new SecureRandom(new byte[16]);
            KeyGenerationParameters keyGenerationParameters = new KeyGenerationParameters(random, keyStrength);
            ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
            keyPairGenerator.init(keyGenerationParameters);
            AsymmetricCipherKeyPair caKey = keyPairGenerator.generateKeyPair();
			
			
			ECGenParameterSpec     ecGenSpec = new ECGenParameterSpec("prime256v1");

			KeyPairGenerator    g = KeyPairGenerator.getInstance("ECDSA", "BC");

			g.initialize(ecGenSpec, new SecureRandom());

			KeyPair pair = g.generateKeyPair();
			return null;
	        
	    
	        RSAPrivateCrtKey priv = (RSAPrivateCrtKey) key.getPrivate();
	        RSAPublicKey pub = (RSAPublicKey) key.getPublic();
	        RsaKeyPair newKey = new RsaKeyPair();
	        newKey.PublicKey = pub.getModulus();
	        newKey.PrivateKey = priv.getPrimeP();
	        */
	    }
		catch(Exception e)
		{
			throw new TpmException("Bad alg:", e);
		}
	}
	
	static String ecTpmToBc(TPM_ECC_CURVE curve)
	{
		switch(curve.asEnum())
		{
			case NIST_P256: return "P-256";
			default: 
			throw new TpmException("Unsupported alg");
		}
	}
	
	static String ecTpmToBc(TPM_ALG_ID id)
	{
		switch(id.asEnum())
		{
			case ECDSA: return "ECDSA";
			case ECDH: return "ECDH";
			default: 
			throw new TpmException("Unsupported alg");
		}
	}
	
	static int ecTpmKeyStrength(TPM_ECC_CURVE curve)
	{
		switch(curve.asEnum())
		{
			case NIST_P256: return 256;
			default: 
			throw new TpmException("Unsupported alg");
		}
	}
	
	public static class RsaKeyPair
	{
		public RsaKeyPair() {}
		
		public BigInteger PublicKey;
		public BigInteger PrivateKey;
	}
	
	public static class ECCKeyPair
	{
		public ECCKeyPair() {}
		
		public org.bouncycastle.math.ec.ECPoint PublicKey;
		public BigInteger PrivateKey;
	}
	
	/**
	 * Converts a signed BC BigInteger into an unsigned fixed-length TPM integer 
	 * 
	 * @param x A BC BigInteger
	 * @param keySize Target key size in bits
	 * @return TPM-formatted integer size keySize/8
	 */
	static byte[] bigIntToTpmInt(BigInteger x, int keySize)
	{
		int numBytes = keySize/8;
		byte[] key = x.toByteArray();
		byte[] ret = new byte[numBytes];

		// offset may be positive (the BigInt has a leading zero sign-byte) or negative (the BigInt does not use all the bytes)
		int offset = key.length - numBytes;
		// todo remove - sanity check 
		if((offset>5) || (offset< -5))throw new RuntimeException("help");
		
		for(int j=0;j<numBytes;j++)
		{
			if(j+offset<0)continue;
			ret[j] = key[j+offset]; 
		}
		return ret;
	}
	
}
