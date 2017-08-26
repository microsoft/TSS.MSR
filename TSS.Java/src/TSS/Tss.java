package tss;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import tss.Crypto.ECCKeyPair;
import tss.Crypto.RsaKeyPair;
import tss.tpm.TPM2B_DIGEST;
import tss.tpm.TPM2B_PUBLIC_KEY_RSA;
import tss.tpm.TPMS_ECC_PARMS;
import tss.tpm.TPMS_ECC_POINT;
import tss.tpm.TPMS_ID_OBJECT;
import tss.tpm.TPMS_RSA_PARMS;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPMT_SENSITIVE;
import tss.tpm.TPMT_SYM_DEF_OBJECT;
import tss.tpm.TPM_ALG_ID;
import tss.tpm.TPM_ECC_CURVE;

public class Tss
{
	public static class Key
	{
		public byte[] PrivatePart;
		public TPMT_PUBLIC PublicPart;
		
		public Key() {}
	}
	
	public static Tss.Key createKey(TPMT_PUBLIC pub)
	{
		Tss.Key tssKey = new Tss.Key();
		
		if(pub.GetUnionSelector_parameters()== TPM_ALG_ID.RSA.Value)
		{

			TPMS_RSA_PARMS parms = (TPMS_RSA_PARMS) pub.parameters;
			int keySize = parms.keyBits;
			int exponent = parms.exponent;
			RsaKeyPair newKey = Crypto.createRsaKey(keySize, exponent);

			byte[] pubKey = Crypto.bigIntToTpmInt(newKey.PublicKey, keySize);

			tssKey.PublicPart = new TPMT_PUBLIC(pub.nameAlg, pub.objectAttributes, pub.authPolicy, pub.parameters, 
												new TPM2B_PUBLIC_KEY_RSA(pubKey));
			tssKey.PrivatePart = Crypto.bigIntToTpmInt(newKey.PrivateKey, keySize/2);
		}
		else if(pub.GetUnionSelector_parameters()== TPM_ALG_ID.ECC.Value) 
		{
			TPMS_ECC_PARMS parms = (TPMS_ECC_PARMS) pub.parameters;
			TPM_ECC_CURVE curve = parms.curveID;
			int scheme = parms.GetUnionSelector_scheme();

			TPM_ALG_ID alg = TPM_ALG_ID.fromInt(scheme);
			ECCKeyPair p = Crypto.createECCKey(curve, alg);
			int keySize = Crypto.ecTpmKeyStrength(curve);

			tssKey.PublicPart = new TPMT_PUBLIC(
					pub.nameAlg, pub.objectAttributes, pub.authPolicy, pub.parameters, 
					new TPMS_ECC_POINT(Crypto.bigIntToTpmInt(p.PublicKey.getXCoord().toBigInteger(),keySize),
									   Crypto.bigIntToTpmInt(p.PublicKey.getYCoord().toBigInteger(), keySize)));
			tssKey.PrivatePart = Crypto.bigIntToTpmInt(p.PrivateKey, keySize);
		}
		else
			throw new TpmException("Unsupported alg");
		return tssKey;
	}

	/**
	 * A helper object that holds the parts of a TPM object activation
	 * @author pengland
	 *
	 */
	public static class ActivationCredential 
	{
		public ActivationCredential()
		{
			CredentialBlob = new TPMS_ID_OBJECT();
			return;
		}
		public TPMS_ID_OBJECT CredentialBlob;
		public byte[] Secret;
	}
	
	/**
	 * Create an TPM activation bundle.  An activation bundle is the secret value encrypted to the 
	 * named ek public key with an associated authorization (the name of the key that the secret 
	 * is associated.)  
	 * 
	 * @param ek The Endorsement Public key that should be used to encrypt the secret
	 * @param nameOfKeyToBeActivated The name of the key that the TPM should check residency
	 * @param secret The secret to encrypt
	 * @return The credential blobs
	 */
	public static ActivationCredential createActivationCredential(
			TPMT_PUBLIC ek, 
			byte[] nameOfKeyToBeActivated,
			byte[] secret) 
	{
		
		if (!(ek.parameters instanceof TPMS_RSA_PARMS))
			throw new RuntimeException("Not supported");

		Tss.ActivationCredential act = new Tss.ActivationCredential();

		TPMS_RSA_PARMS ekParms = (TPMS_RSA_PARMS) ek.parameters;
		TPM2B_PUBLIC_KEY_RSA ekPubKey = (TPM2B_PUBLIC_KEY_RSA) (ek.unique);
		TPM_ALG_ID schemeAlg = ek.nameAlg;
		TPM_ALG_ID nameAlg = ek.nameAlg;

		TPMT_SYM_DEF_OBJECT symDef = ekParms.symmetric;
		if (symDef.algorithm != TPM_ALG_ID.AES)
			throw new RuntimeException("Symmetric alg not supported");
		if (symDef.mode != TPM_ALG_ID.CFB)
			throw new RuntimeException("Symmetric alg mode not supported");
		int symmKeySize = symDef.keyBits;

		byte[] seed = Helpers.getRandom(Crypto.digestSize(nameAlg));
		byte[] encSeed = Crypto.oaepEncrypt(ekParms, ekPubKey, seed, schemeAlg, "IDENTITY");
		act.Secret = encSeed;

		byte[] lengthPrependedSecret = (new TPM2B_DIGEST(secret)).toTpm();
		byte[] symKey = Crypto.KDFa(nameAlg, seed, "STORAGE", nameOfKeyToBeActivated, new byte[0], symmKeySize);

		CFBBlockCipher encryptCipher = new CFBBlockCipher(new AESEngine(), symmKeySize);
		KeyParameter key = new KeyParameter(symKey);
		encryptCipher.init(true, new ParametersWithIV(key, new byte[1]));
		byte[] encIdentity = new byte[lengthPrependedSecret.length];
		int numEncrypted = encryptCipher.processBytes(lengthPrependedSecret, 0,lengthPrependedSecret.length, encIdentity, 0);
		
		if (numEncrypted != lengthPrependedSecret.length)throw new RuntimeException("");
		act.CredentialBlob.encIdentity = encIdentity;
		
		int hmacKeyBits = Crypto.digestSize(nameAlg);
		byte[] hmacKey = Crypto.KDFa(nameAlg, seed, "INTEGRITY", new byte[0], new byte[0], hmacKeyBits * 8);

		byte[] toHash = Helpers.concatenate(act.CredentialBlob.encIdentity, nameOfKeyToBeActivated);
		
		byte[] outerHmac = Crypto.hmac(nameAlg, hmacKey, toHash);
		act.CredentialBlob.integrityHMAC = outerHmac;
		return act;

	}

	/**
	 * Encapsulates data necessary to import an object (usually a key) into a TPM.
	 * @author pengland
	 */
	public static class DuplicationBlob 
	{    
		public DuplicationBlob()
		{
		}
		
		/**
		 * The optional symmetric encryption key used as the inner wrapper for
		 * duplicate. If symmetricAlg is TPM_ALG_NULL, then this parameter shall be the
		 * Empty Buffer
		 */
		public byte[] EncryptionKey;
		
		/**
		 * The symmetrically encrypted duplicate object that may contain an inner
		 * symmetric wrapper
		 */
		public byte[] DuplicateObject;
		
		/**
		 *  Symmetric key used to encrypt duplicate inSymSeed is   
		 *  encrypted / encoded using the algorithms of newParent
		 */
		public byte[] EncryptedSeed;
		
		/**
		 * Set to random key used for inner-wrapper (if an inner-wrapper is requested).       
		 */
		public byte[] InnerWrapperKey;
	};
	
	public static Tss.DuplicationBlob createDuplicationBlob(
			TPMT_PUBLIC targetParent,
			TPMT_PUBLIC _publicPart,
			TPMT_SENSITIVE _sensitivePart,
			TPMT_SYM_DEF_OBJECT innerWrapper)
	{
		if(!(targetParent.parameters instanceof TPMS_RSA_PARMS))
		{
			throw new TpmException("Only import of keys to RSA storage parents supported");
		}
		TPM_ALG_ID nameAlg = targetParent.nameAlg;

		Tss.DuplicationBlob blob = new Tss.DuplicationBlob();
		byte[] encryptedSensitive = null;;
		byte[] innerWrapperKey = null;;
		byte[] nullVec = new byte[0];
		//int innerWrapperKeyBits=innerWrapper.keyBits;

		if (innerWrapper.algorithm == TPM_ALG_ID.NULL) 
		{
			encryptedSensitive = Helpers.byteArrayToLenPrependedByteArray(_sensitivePart.toTpm());
			blob.EncryptionKey = nullVec;
		} else 
		{
			if (innerWrapper.algorithm != TPM_ALG_ID.AES &&
					innerWrapper.mode != TPM_ALG_ID.CFB) 
			{
				throw new TpmException("innerWrapper KeyDef is not supported for import");
			}

			byte[] sens = Helpers.byteArrayToLenPrependedByteArray(_sensitivePart.toTpm());
			byte[]  toHash = Helpers.concatenate(sens, _publicPart.getName());

			byte[] innerIntegrity = Helpers.byteArrayToLenPrependedByteArray(Crypto.hash(nameAlg, toHash));
			byte[] innerData = Helpers.concatenate(innerIntegrity, sens);

			int aesKeyLen = innerWrapper.keyBits/8;
			innerWrapperKey = Helpers.getRandom(aesKeyLen);
			encryptedSensitive = Crypto.cfbEncrypt(true,TPM_ALG_ID.AES,innerWrapperKey,nullVec,innerData);
			blob.EncryptionKey = innerWrapperKey;
		}

		TPMS_RSA_PARMS newParentParms = (TPMS_RSA_PARMS)(targetParent.parameters);
		TPMT_SYM_DEF_OBJECT newParentSymDef = newParentParms.symmetric;

		if (newParentSymDef.algorithm != TPM_ALG_ID.AES &&
				newParentSymDef.mode != TPM_ALG_ID.CFB) 
		{
			throw new TpmException("new parent symmetric key is not supported for import");
		}

		int newParentSymmKeyLen = newParentSymDef.keyBits;
		// Otherwise we know we are AES128
		byte[] seed = Helpers.getRandom(newParentSymmKeyLen/8);
		byte[] encryptedSeed = targetParent.encrypt(seed, "DUPLICATE");

		byte[] symmKey = Crypto.KDFa(targetParent.nameAlg,seed,"STORAGE",_publicPart.getName(),nullVec,newParentSymmKeyLen);

		byte[] dupSensitive = Crypto.cfbEncrypt(true,TPM_ALG_ID.AES,symmKey,nullVec,encryptedSensitive);

		int npNameNumBits = Crypto.digestSize(nameAlg) * 8;
		byte[] hmacKey = Crypto.KDFa(nameAlg, seed, "INTEGRITY", nullVec, nullVec, npNameNumBits);
		byte[] outerDataToHmac = Helpers.concatenate(dupSensitive, _publicPart.getName());
		byte[] outerHmacBytes = Crypto.hmac(nameAlg, hmacKey, outerDataToHmac);
		byte[] outerHmac = Helpers.byteArrayToLenPrependedByteArray(outerHmacBytes);
		byte[] DuplicationBlob = Helpers.concatenate(outerHmac, dupSensitive);

		blob.DuplicateObject = DuplicationBlob;
		blob.EncryptedSeed = encryptedSeed;
		blob.InnerWrapperKey = innerWrapperKey;

		return blob;
	}

	
	
}
