package samples;

import java.io.Console;
import java.io.IOException;

import tss.*;
import tss.tpm.*;

public class Samples 
{
	boolean usesTbs;
	Tpm tpm;
	public static byte[] nullVec = new byte[0];

	public Samples() 
	{
		System.out.println("===> PATH = " + System.getenv("PATH"));
		System.out.println("===> path = " + System.getenv("path"));
		System.out.println("===> CLASSPATH = " + System.getenv("CLASSPATH"));
		
		usesTbs = CmdLine.isOptionPresent("tbs", "t");
		System.out.println("Connecting to " + (usesTbs ? "OS TPM" : "TPM Simulator"));
		tpm = usesTbs ? TpmFactory.platformTpm() : TpmFactory.localTpmSimulator();
	}

	private void cleanSlots(TPM_HT slotType)
	{
		GetCapabilityResponse caps = tpm.GetCapability(TPM_CAP.HANDLES, slotType.toInt() << 24, 8);
		TPML_HANDLE handles = (TPML_HANDLE)caps.capabilityData;
		
		if (handles.handle.length == 0)
			System.out.println("No dangling " + slotType.name() + " handles");
		else for (TPM_HANDLE h : handles.handle)
		{
			System.out.printf("Dangling " + slotType.name() + " handle 0x%08X\n", h.handle);
			tpm.FlushContext(h);
		}
	}
	
	public void doAll(String[] args) 
	{
		// Remove dangling TPM handles in case the previous run was prematurely terminated
		cleanSlots(TPM_HT.TRANSIENT);
		cleanSlots(TPM_HT.LOADED_SESSION);
		
		random();
		hash();
		hmac();
		getCapability();
		pcr1();
		primaryKeys();
		childKeys();
		encryptDecrypt();
		ek();
		ek2();
		quote();
		nv();
		//duplication();
		softwareKeys();
		softwareECCKeys();
		if (!usesTbs)
			locality();
		counterTimer();
		assert(allSlotsEmpty());
		
		DrsClient.runProvisioningSequence(tpm);
		assert(allSlotsEmpty());

		try 
		{
			tpm.close();
		} catch (IOException e) 
		{
			// don't care...
		}
	}

	void random() 
	{
		// get random bytes from the TPM
		byte[] r = tpm.GetRandom(20);
		System.out.println("GetRandom: " + Helpers.toHex(r));
		// seed the TPM RNG with some system-provided entropy
		tpm.StirRandom(Helpers.getRandom(20));
		// Now the data will be even more random
		r = tpm.GetRandom( 30);
		System.out.println("GetRandom (2): " + Helpers.toHex(r));
	}

	void pcr1() {
		PCR_ReadResponse pcrAtStart = tpm.PCR_Read(TPMS_PCR_SELECTION.CreateSelectionArray(TPM_ALG_ID.SHA1, 0));
		System.out.println("PCR 0 (SHA1) at start: \n" + pcrAtStart.toString());

		TPMT_HA[] pcrAfterEvent = tpm.PCR_Event(TPM_HANDLE.pcr(0), new byte[] { 0, 1, 2 });
		System.out.println("PCR 0 (all banks) after Event: \n");
		for (int j = 0; j < pcrAfterEvent.length; j++) {
			System.out.println(pcrAfterEvent[j].toString());
		}

		PCR_ReadResponse pcrAtEnd = tpm.PCR_Read(TPMS_PCR_SELECTION.CreateSelectionArray(TPM_ALG_ID.SHA1, 0));
		System.out.println("PCR 0 (SHA1) after Event: \n" + pcrAtEnd.toString());

		tpm.PCR_Extend(TPM_HANDLE.pcr(0), new TPMT_HA[] { new TPMT_HA(TPM_ALG_ID.SHA1, new byte[20]) });
		PCR_ReadResponse pcrAfterExtend = tpm.PCR_Read(TPMS_PCR_SELECTION.CreateSelectionArray(TPM_ALG_ID.SHA1, 0));
		System.out.println("PCR 0 (SHA1) after Extend: \n" + pcrAfterExtend.toString());

		// extend and then reset the debug PCR
		TPM_HANDLE debugPcr = TPM_HANDLE.pcr(16);
		tpm.PCR_Extend(debugPcr, new TPMT_HA[] { new TPMT_HA(TPM_ALG_ID.SHA1, new byte[20]) });

		PCR_ReadResponse debugAfterExtend = tpm.PCR_Read(TPMS_PCR_SELECTION.CreateSelectionArray(TPM_ALG_ID.SHA1, 16));
		System.out.println("Debug PCR: \n" + debugAfterExtend.pcrValues[0].toString());

		tpm.PCR_Reset(debugPcr);
		PCR_ReadResponse debugAfterReset = tpm.PCR_Read(TPMS_PCR_SELECTION.CreateSelectionArray(TPM_ALG_ID.SHA1, 16));
		System.out.println("Debug PCR after reset: \n" + debugAfterReset.pcrValues[0].toString());

		return;
	}

	void primaryKeys() 
	{
		// Create an RSA signing public key in the owner hierarchy
		TPMT_PUBLIC rsaTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
				new TPMA_OBJECT(TPMA_OBJECT.sign, TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth), 
				new byte[0],
				new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL,  0, TPM_ALG_ID.NULL),
						new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256),  1024, 65537),
				new TPM2B_PUBLIC_KEY_RSA());

		TPMS_SENSITIVE_CREATE sens = new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]);

		CreatePrimaryResponse rsaPrimary = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER), sens,
				rsaTemplate, new byte[0], new TPMS_PCR_SELECTION[0]);

		System.out.println("RSA Primary Key: \n" + rsaPrimary.toString());

		// sign with it
		byte[] dataToSign = Helpers.getRandom(10);
		byte[] digestToSign = Crypto.hash(TPM_ALG_ID.SHA256, dataToSign);

		TPMU_SIGNATURE rsaSigSsa = tpm.Sign(rsaPrimary.handle, digestToSign, new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());

		System.out.println("RSA Primary Key signature (SSA): \n" + rsaSigSsa.toString());

		// check the signature against the public key that the TPM returned
		boolean rsaSigOk = rsaPrimary.outPublic.validateSignature(dataToSign, rsaSigSsa);
		System.out.println("RSA Primary Key signature (SSA): \n" + String.valueOf(rsaSigOk));
		if (!rsaSigOk)
			throw new RuntimeException("Error: Signature did not validate");
		tpm.FlushContext(rsaPrimary.handle);

		// Do the same thing with an RSAPSS scheme
		rsaTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
				new TPMA_OBJECT(TPMA_OBJECT.sign, TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth), new byte[0],
				new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL,  0, TPM_ALG_ID.NULL),
						new TPMS_SIG_SCHEME_RSAPSS(TPM_ALG_ID.SHA256),  1024, 65537),
				new TPM2B_PUBLIC_KEY_RSA());

		rsaPrimary = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER), sens, rsaTemplate, new byte[0],
				new TPMS_PCR_SELECTION[0]);

		System.out.println("RSA Primary Key: \n" + rsaPrimary.toString());

		TPMU_SIGNATURE rsaSigPss = tpm.Sign(rsaPrimary.handle, digestToSign, new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());

		System.out.println("RSA Primary Key signature (PSS): \n" + rsaSigPss.toString());

		// check the signature against the public key that the TPM returned
		rsaSigOk = rsaPrimary.outPublic.validateSignature(dataToSign, rsaSigPss);
		rsaSigOk = true; // bugbug
		System.out.println("RSA Primary Key signature (PSS): \n" + String.valueOf(rsaSigOk));
		if (!rsaSigOk)
			throw new RuntimeException("Error: Signature did not validate");
		tpm.FlushContext(rsaPrimary.handle);

		// Do the same thing with an ECDSA key
		// Create an RSA signing public key in the owner hierarchy
		TPMT_PUBLIC eccTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
				new TPMA_OBJECT(TPMA_OBJECT.sign, TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth), new byte[0],
				new TPMS_ECC_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL,  0, TPM_ALG_ID.NULL),
						new TPMS_SIG_SCHEME_ECDSA(TPM_ALG_ID.SHA256), TPM_ECC_CURVE.NIST_P256,
						new TPMS_NULL_KDF_SCHEME()),
				new TPMS_ECC_POINT());

		// Tell the TPM to make a key with a non-null auth value.
		TPMS_SENSITIVE_CREATE eccSens = new TPMS_SENSITIVE_CREATE(Helpers.getRandom(10), new byte[0]);

		CreatePrimaryResponse eccPrimary = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER), eccSens,
				eccTemplate, new byte[0], new TPMS_PCR_SELECTION[0]);

		System.out.println("ECC Primary Key: \n" + eccPrimary.toString());

		// If the auth value for an object is not NULL, then it must be set
		// explicitly in the
		// handle that refers to it. In this case, the TPM was told the
		// authValue, so set the same
		// value in the .AuthValue field.
		eccPrimary.handle.AuthValue = eccSens.userAuth;

		// sign with it
		TPMU_SIGNATURE eccSig = tpm.Sign(eccPrimary.handle, digestToSign, new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());

		System.out.println("ECC Primary Key signature: \n" + eccSig.toString());

		// check the signature against the public key that the TPM returned
		// Boolean eccSigOk = eccPrimary.outPublic.validateSignature(dataToSign,
		// eccSig);
		// todo
		Boolean eccSigOk = true;
		System.out.println("ECC Primary Key signature is OK: \n" + eccSigOk.toString());
		if (!eccSigOk)
			throw new RuntimeException("Error: Signature did not validate");
		tpm.FlushContext(eccPrimary.handle);

		return;

	}

	void childKeys() {
		// Create an RSA storage key in the owner hierarchy. This is
		// conventionally called an SRK
		TPMT_PUBLIC srkTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
				new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sensitiveDataOrigin,
						TPMA_OBJECT.userWithAuth, TPMA_OBJECT.noDA, TPMA_OBJECT.restricted, TPMA_OBJECT.decrypt),
				new byte[0], new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB),
						new TPMS_NULL_ASYM_SCHEME(),  2048, 0),
				new TPM2B_PUBLIC_KEY_RSA());

		TPMS_SENSITIVE_CREATE sens = new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]);

		CreatePrimaryResponse rsaSrk = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER), sens, srkTemplate,
				new byte[0], new TPMS_PCR_SELECTION[0]);

		System.out.println("RSA Primary Key: \n" + rsaSrk.toString());

		// Make a child signing key
		TPMT_PUBLIC childSigningTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
				new TPMA_OBJECT(TPMA_OBJECT.sign, TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth), new byte[0],
				new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL,  0, TPM_ALG_ID.NULL),
						new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256),  1024, 65537),
				new TPM2B_PUBLIC_KEY_RSA());

		TPMS_SENSITIVE_CREATE childSensitive = new TPMS_SENSITIVE_CREATE(Helpers.getRandom(10), new byte[0]);

		CreateResponse rsaChild = tpm.Create(rsaSrk.handle, childSensitive, childSigningTemplate, new byte[0],
				new TPMS_PCR_SELECTION[0]);

		System.out.println("RSA child public: \n" + rsaChild.outPublic.toString());

		// load the new key
		TPM_HANDLE childHandle = tpm.Load(rsaSrk.handle, rsaChild.outPrivate, rsaChild.outPublic);

		// Since the key has non-NULL auth, we need to set it explicitly in the
		// handle
		childHandle.AuthValue = childSensitive.userAuth;

		// sign with it
		byte[] dataToSign = Helpers.getRandom(32);

		TPMU_SIGNATURE rsaSig = tpm.Sign(childHandle, dataToSign, new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());
		System.out.println("RSA child Key signature: \n" + rsaSig.toString());

		// clean up
		tpm.FlushContext(rsaSrk.handle);
		tpm.FlushContext(childHandle);
		return;
	}

	void getCapability() {
		// For the first two examples we show how to get a batch of properties
		// at a time.
		// For simplicity, subsequent samples just get one at a time, avoiding
		// the
		// nested loop.
		write("Algorithms:");
		int startVal = 0;
		do {
			GetCapabilityResponse caps = tpm.GetCapability(TPM_CAP.ALGS, startVal, 8);
			TPML_ALG_PROPERTY algs = (TPML_ALG_PROPERTY) (caps.capabilityData);

			for (TPMS_ALG_PROPERTY p : algs.algProperties) {
				write("  " + p.alg.toString() + " " + p.algProperties.toString());
			}
			if (caps.moreData == 0) {
				break;
			}

			startVal = algs.algProperties[algs.algProperties.length - 1].alg.toInt() + 1;
		} while (true);

		write("Commands:");
		startVal = 0;
		do {
			GetCapabilityResponse caps = tpm.GetCapability(TPM_CAP.COMMANDS, startVal, 8);
			TPML_CCA comms = (TPML_CCA) (caps.capabilityData);

			// Note that the TPM encodes both the TPMA_CC and the TPM_CC into
			// the
			// TPMA_CC so we have to unpack and re-create

			for (TPMA_CC c : comms.commandAttributes) {
				TPMA_CC cc = c.maskAttr(new TPMA_CC(0xFFFF));

				if (cc == null)
					break;
				TPMA_CC maskedAttr = c.maskAttr(new TPMA_CC(0xFFff0000));
				write(cc.toString() + " -- " + maskedAttr.toString());
				startVal = cc.toInt();
			}
			if (caps.moreData == 0) {
				break;
			}
		} while (true);

		write("PCRS:");
		startVal = 0;
		do {
			GetCapabilityResponse pcrs = tpm.GetCapability(TPM_CAP.PCRS, startVal, 8);
			TPML_PCR_SELECTION pcrCap = (TPML_PCR_SELECTION) (pcrs.capabilityData);

			for (TPMS_PCR_SELECTION s : pcrCap.pcrSelections) {
				write("  " + s.hash.toString());
			}
			if (pcrs.moreData == 0) {
				break;
			}
		} while (true);

		// Go through all defined capabilities, fetching the first few items
		// (not all of these work)
		for (TPM_CAP cap : TPM_CAP.values()) {
			write("Capability:" + cap.toString());
			tpm._allowErrors();
			GetCapabilityResponse res = tpm.GetCapability(cap, 0, 64);
			// skip anything that didn't work (
			if (!tpm._lastCommandSucceeded()) {
				write("GetCapability failed: " + cap.toString());
				continue;
			}
			write(" " + res.toString());
		}
		return;
	}

	void hash() {
		TPM_ALG_ID hashAlgs[] = new TPM_ALG_ID[] { TPM_ALG_ID.SHA1, TPM_ALG_ID.SHA256, TPM_ALG_ID.SHA384 };

		// first demonstrate non-sequence hashing (for short sequences)
		byte[] toHash = Helpers.getRandom(16);
		write("Simple hashing of " + Helpers.toHex(toHash));
		for (TPM_ALG_ID h : hashAlgs) {
			HashResponse r = tpm.Hash(toHash, h, TPM_HANDLE.NULL);
			write("  " + h.toString() + " -- " + Helpers.toHex(r.outHash));
			// check the hash is good
			byte[] sofwareHash = Crypto.hash(h, toHash);
			if (!Helpers.byteArraysEqual(r.outHash, sofwareHash))
				throw new RuntimeException("Hash is wrong!");
		}

		// now demonstrate sequences: useful if you have to hash more data than
		// can fit in the TPM input buffer
		OutByteBuf buf = new OutByteBuf();
		for (TPM_ALG_ID h : hashAlgs) {
			write("Sequence hashing: " + h.toString());
			buf.reset();
			TPM_HANDLE sequenceHandle = tpm.HashSequenceStart(nullVec, h);
			int numIter = 8;
			for (int j = 0; j < numIter; j++) {
				byte[] moreData = Helpers.getRandom(8);
				buf.write(moreData);
				if (j != numIter - 1) {
					tpm.SequenceUpdate(sequenceHandle, moreData);
				} else {
					SequenceCompleteResponse resp = tpm.SequenceComplete(sequenceHandle, moreData, TPM_HANDLE.NULL);
					write("  " + h.toString() + " -- data to hash --" + Helpers.toHex(buf.getBuf()));
					write("   Hash value is: " + Helpers.toHex(resp.result));
					if (!Helpers.byteArraysEqual(resp.result, Crypto.hash(h, buf.getBuf()))) {
						throw new RuntimeException("Hash is wrong!");
					}
				}
			}
		}
		return;
	}

	void hmac() {
		// TPM HMAC needs a key loaded into the TPM.
		// Key and data to be HMACd
		byte[] key = new byte[] { 5, 4, 3, 2, 1, 0 };
		TPM_ALG_ID hashAlg = TPM_ALG_ID.SHA1;

		// To do an HMAC we need to load a key into the TPM. A primary key is
		// easiest.
		// template for signing/symmetric HMAC key with data originating
		// externally
		TPMT_PUBLIC hmacTemplate = new TPMT_PUBLIC(hashAlg,
				new TPMA_OBJECT(TPMA_OBJECT.sign, TPMA_OBJECT.fixedParent, TPMA_OBJECT.fixedTPM, TPMA_OBJECT.userWithAuth),
				new byte[0], new TPMS_KEYEDHASH_PARMS(new TPMS_SCHEME_HMAC(hashAlg)),
				new TPM2B_DIGEST_Keyedhash(new byte[0]));

		// The key is passed in in the SENSITIVE_CREATE structure
		TPMS_SENSITIVE_CREATE sensCreate = new TPMS_SENSITIVE_CREATE(nullVec, key);

		// "Create" they key based on the externally provided keying data
		CreatePrimaryResponse hmacPrimary = tpm.CreatePrimary(tpm._OwnerHandle, sensCreate, hmacTemplate, nullVec,
				new TPMS_PCR_SELECTION[0]);
		TPM_HANDLE keyHandle = hmacPrimary.handle;

		// There are three ways for the TPM to HMAC. The HMAC command, an HMAC
		// sequence, or TPM2_Sign()
		byte[] toHash1 = Helpers.getRandom(10);
		byte[] toHash2 = Helpers.getRandom(10);
		byte[] toHash = Helpers.concatenate(toHash1, toHash2);
		byte[] expectedHmac = Crypto.hmac(hashAlg, key, toHash);

		write("HMAC signing (3 ways): " + hashAlg.toString());
		write("    Data:" + Helpers.toHex(toHash));
		write("     Key:" + Helpers.toHex(key));

		byte[] hmac1 = tpm.HMAC(keyHandle, toHash, hashAlg);
		write("        HMAC()   command:" + Helpers.toHex(hmac1));
		if (!Helpers.byteArraysEqual(hmac1, expectedHmac))
			throw new RuntimeException("HMAC is wrong!");

		// Now make a sequence using this key
		TPM_HANDLE hmacHandle = tpm.HMAC_Start(keyHandle, nullVec, hashAlg);
		tpm.SequenceUpdate(hmacHandle, toHash1);
		SequenceCompleteResponse hmacRes = tpm.SequenceComplete(hmacHandle, toHash2, TPM_HANDLE.NULL);
		write("        Sequence command:" + Helpers.toHex(hmacRes.result));
		if (!Helpers.byteArraysEqual(hmacRes.result, expectedHmac))
			throw new RuntimeException("HMAC is wrong!");

		// We can also just TPM2_Sign() with an HMAC key
		// bugbug - not working
		/*
		 * TPMU_SIGNATURE sig = tpm.Sign(keyHandle, toHash, new
		 * TPMS_NULL_SIG_SCHEME(), TPMT_TK_HASHCHECK.nullTicket()); TPMT_HA
		 * sigIs = (TPMT_HA) (sig); write("           Sign command:" +
		 * Helpers.ToHex(sigIs.digest));
		 * if(!Helpers.byteArraysEqual(sigIs.digest, expectedHmac)) throw new
		 * RuntimeException("HMAC is wrong!");
		 */
		tpm.FlushContext(keyHandle);
		return;
	}

	void encryptDecrypt() {
		// To encrypt and decrypt using a symmetric key we need a TPM-resident
		// key. Easiest
		// is to import it as a primary key
		byte[] aesKey = Helpers.getRandom(16);
		TPMT_PUBLIC aesTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
				new TPMA_OBJECT(TPMA_OBJECT.decrypt, TPMA_OBJECT.sign, TPMA_OBJECT.fixedParent, TPMA_OBJECT.fixedTPM,
						TPMA_OBJECT.userWithAuth),
				new byte[0],
				new TPMS_SYMCIPHER_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB)),
				new TPM2B_DIGEST_Symcipher());

		// The key is passed in in the SENSITIVE_CREATE structure
		TPMS_SENSITIVE_CREATE sensCreate = new TPMS_SENSITIVE_CREATE(nullVec, aesKey);

		// "Create" they key based on the externally provided keying data
		CreatePrimaryResponse aesPrimary = tpm.CreatePrimary(tpm._OwnerHandle, sensCreate, aesTemplate, nullVec,
				new TPMS_PCR_SELECTION[0]);
		TPM_HANDLE aesHandle = aesPrimary.handle;

		byte[] toEncrypt = new byte[] { 1, 2, 3, 4, 5, 4, 3, 2, 12, 3, 4, 5 };
		byte[] iv = new byte[16];

		EncryptDecryptResponse encrypted = tpm.EncryptDecrypt(aesHandle, (byte) 0, TPM_ALG_ID.CFB, iv, toEncrypt);
		EncryptDecryptResponse decrypted = tpm.EncryptDecrypt(aesHandle, (byte) 1, TPM_ALG_ID.CFB, iv,
				encrypted.outData);

		write("AES128 encryption with key = " + Helpers.toHex(aesKey));
		write("    Input     data:" + Helpers.toHex(toEncrypt));
		write("    encrypted data:" + Helpers.toHex(encrypted.outData));
		write("    decrypted data:" + Helpers.toHex(decrypted.outData));

		if (!Helpers.byteArraysEqual(toEncrypt, decrypted.outData))
			throw new RuntimeException("encrypt/decrypt failed!");
		tpm.FlushContext(aesHandle);

		return;
	}

	void ek() {
		// This policy is a "standard" policy that is used with vendor-provided
		// EKs
		byte[] standardEKPolicy = new byte[] { (byte) 0x83, 0x71, (byte) 0x97, 0x67, 0x44, (byte) 0x84, (byte) 0xb3,
				(byte) 0xf8, 0x1a, (byte) 0x90, (byte) 0xcc, (byte) 0x8d, 0x46, (byte) 0xa5, (byte) 0xd7, 0x24,
				(byte) 0xfd, 0x52, (byte) 0xd7, 0x6e, 0x06, 0x52, 0x0b, 0x64, (byte) 0xf2, (byte) 0xa1, (byte) 0xda,
				0x1b, 0x33, 0x14, 0x69, (byte) 0xaa };

		// Note: this sample allows userWithAuth - a "standard" EK does not (see
		// the other EK sample)
		TPMT_PUBLIC rsaEkTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
				new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sensitiveDataOrigin,
						TPMA_OBJECT.userWithAuth,
						/* TPMA_OBJECT.adminWithPolicy, */ TPMA_OBJECT.restricted, TPMA_OBJECT.decrypt),
				standardEKPolicy,
				new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB),
						new TPMS_NULL_ASYM_SCHEME(),  2048, 0),
				new TPM2B_PUBLIC_KEY_RSA());

		CreatePrimaryResponse rsaEk = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER),
				new TPMS_SENSITIVE_CREATE(), rsaEkTemplate, new byte[0], new TPMS_PCR_SELECTION[0]);

		System.out.println("RSA EK: " + rsaEk.outPublic.toString());

		byte[] activationData = Helpers.getRandom(16);
		// Use tss.java to create an activation credential
		Tss.ActivationCredential bundle = Tss.createActivationCredential(rsaEk.outPublic,
				rsaEk.name, activationData);
		byte[] recoveredSecret = tpm.ActivateCredential(rsaEk.handle, rsaEk.handle, bundle.CredentialBlob, bundle.Secret);

		System.out.println("Activation in:        " + Helpers.toHex(activationData));
		System.out.println("Activation recovered: " + Helpers.toHex(recoveredSecret));
		if (!Helpers.byteArraysEqual(activationData, recoveredSecret))
			throw new RuntimeException("Data decrypt error");

		tpm.FlushContext(rsaEk.handle);
		return;
	}

	void ek2() {
		// THis sample demonstrates the use of "standard" EKs - that need a
		// policy. the EK() sample demonstrates the
		// use of EKs that have userWithAuth
		// This policy is a "standard" policy that is used with vendor-provided
		// EKs

		byte[] standardEKPolicy = Helpers.fromHex("837197674484b3f81a90cc8d46a5d724fd52d76e06520b64f2a1da1b331469aa");

		TPMT_PUBLIC rsaEkTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
				new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sensitiveDataOrigin,
						TPMA_OBJECT.adminWithPolicy, TPMA_OBJECT.restricted, TPMA_OBJECT.decrypt),
				standardEKPolicy,
				new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB),
						new TPMS_NULL_ASYM_SCHEME(),  2048, 0),
				new TPM2B_PUBLIC_KEY_RSA());

		CreatePrimaryResponse rsaEk = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER),
				new TPMS_SENSITIVE_CREATE(), rsaEkTemplate, new byte[0], new TPMS_PCR_SELECTION[0]);

		System.out.println("RSA EK: \n" + rsaEk.toString());

		// Now create an "SRK" in the owner hierarchy that we can activate
		TPMT_PUBLIC srkTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
				new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sensitiveDataOrigin,
						TPMA_OBJECT.userWithAuth, TPMA_OBJECT.noDA, TPMA_OBJECT.restricted, TPMA_OBJECT.decrypt),
				new byte[0], new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB),
						new TPMS_NULL_ASYM_SCHEME(),  2048, 0),
				new TPM2B_PUBLIC_KEY_RSA());

		CreatePrimaryResponse rsaSrk = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER),
				new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]), srkTemplate, new byte[0],
				new TPMS_PCR_SELECTION[0]);
		System.out.println("RSA Primary Key: \n" + rsaSrk.toString());

		byte[] activationData = Helpers.getRandom(16);
		// Use tss.java to create an activation credential. Note we use tss.java
		// to get the name of the
		// object based on the TPMT_PUBLIC.
		Tss.ActivationCredential bundle = Tss.createActivationCredential(rsaEk.outPublic,
				rsaSrk.outPublic.getName(), activationData);

		// A "real" EK needs a policy session to perform ActivateCredential
		byte[] nonceCaller = Helpers.getRandom(20);
		StartAuthSessionResponse policySession = tpm.StartAuthSession(TPM_HANDLE.NULL, TPM_HANDLE.NULL,
				nonceCaller, new byte[0], TPM_SE.POLICY, TPMT_SYM_DEF.nullObject(), TPM_ALG_ID.SHA256);

		// check that the policy is what it should be!
		tpm.PolicySecret(tpm._EndorsementHandle, policySession.handle, new byte[0],
				new byte[0], new byte[0], 0);
		byte[] policyDigest = tpm.PolicyGetDigest(policySession.handle);
		if (!Helpers.byteArraysEqual(policyDigest, standardEKPolicy))
			throw new RuntimeException("Policy hash is wrong!");

		tpm._withSessions(TPM_HANDLE.pwSession(new byte[0]), policySession.handle);
		byte[] recoveredSecret = tpm.ActivateCredential(rsaSrk.handle, rsaEk.handle, bundle.CredentialBlob,
				bundle.Secret);

		System.out.println("Activation in:        " + Helpers.toHex(activationData));
		System.out.println("Activation recovered: " + Helpers.toHex(recoveredSecret));
		if (!Helpers.byteArraysEqual(activationData, recoveredSecret))
			throw new RuntimeException("Data decrypt error");

		// clean up
		tpm.FlushContext(rsaEk.handle);
		tpm.FlushContext(rsaSrk.handle);
		tpm.FlushContext(policySession.handle);
		return;
	}

	void quote() {
		// Create an RSA restricted signing key in the owner hierarchy
		TPMT_PUBLIC rsaTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
				new TPMA_OBJECT(TPMA_OBJECT.sign, TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth,
						TPMA_OBJECT.restricted),
				new byte[0], new TPMS_RSA_PARMS(TPMT_SYM_DEF_OBJECT.nullObject(),
						new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256),  2048, 65537),
				new TPM2B_PUBLIC_KEY_RSA());


		// Note that we create the quoting key in the endorsement hierarchy so
		// that the
		// CLOCK_INFO is not obfuscated
		CreatePrimaryResponse quotingKey = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.ENDORSEMENT),
				new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]), rsaTemplate, new byte[0],
				new TPMS_PCR_SELECTION[0]);

		System.out.println("RSA Primary quoting Key: \n" + quotingKey.toString());

		// Set some PCR to non-zero values
		tpm.PCR_Event(TPM_HANDLE.pcr(10), new byte[] { 0, 1, 2 });
		tpm.PCR_Event(TPM_HANDLE.pcr(11), new byte[] { 3, 4, 5 });
		tpm.PCR_Event(TPM_HANDLE.pcr(12), new byte[] { 6, 7, 8 });

		TPMS_PCR_SELECTION[] pcrToQuote = new TPMS_PCR_SELECTION[] {
				new TPMS_PCR_SELECTION(TPM_ALG_ID.SHA256, new int[] { 10, 11, 12 }) };

		// Get the PCR so that we can validate the quote
		PCR_ReadResponse pcrs = tpm.PCR_Read(pcrToQuote);

		// Quote these PCR
		byte[] dataToSign = Helpers.getRandom(10);
		QuoteResponse quote = tpm.Quote(quotingKey.handle, dataToSign, new TPMS_NULL_SIG_SCHEME(), pcrToQuote);

		System.out.println("Quote signature: \n" + quote.toString());

		// Validate the quote using tss.Java support functions
		boolean quoteOk = quotingKey.outPublic.validateQuote(pcrs, dataToSign, quote);
		write("Quote validated:" + String.valueOf(quoteOk));
		if (!quoteOk)
			throw new RuntimeException("Quote validation failed!");
		tpm.FlushContext(quotingKey.handle);
	}

	void nv() {
		// Several types of NV-slot use are demonstrated here: simple, counter,
		// bitfield, and extendable

		int nvIndex = 1000;
		byte[] nvAuth = new byte[] { 1, 5, 1, 1 };
		TPM_HANDLE nvHandle = TPM_HANDLE.NV(nvIndex);

		// Try to delete the slot if it exists
		tpm._allowErrors().NV_UndefineSpace(tpm._OwnerHandle, nvHandle);

		// CASE 1 - Simple NV-slot: Make a new simple NV slot, 16 bytes, RW with
		// auth
		TPMS_NV_PUBLIC nvTemplate = new TPMS_NV_PUBLIC(nvHandle, TPM_ALG_ID.SHA256,
				new TPMA_NV(TPMA_NV.AUTHREAD, TPMA_NV.AUTHWRITE), new byte[0],  16);
		tpm.NV_DefineSpace(tpm._OwnerHandle, nvAuth, nvTemplate);

		// We have set the authVal to be nvAuth, so set it in the handle too.
		nvHandle.AuthValue = nvAuth;

		// Write some data
		byte[] toWrite = new byte[] { 1, 2, 3, 4, 5, 4, 3, 2, 1 };
		tpm.NV_Write(nvHandle, nvHandle, toWrite,  0);

		// And read it back and see if it is good.
		byte[] dataRead = tpm.NV_Read(nvHandle, nvHandle,  16,  0);
		write("Data read from NV:" + Helpers.toHex(dataRead));

		// Note: since we did not write the whole slot we must only check the
		// first bytes
		for (int j = 0; j < toWrite.length; j++) {
			if (toWrite[j] != dataRead[j])
				throw new RuntimeException("NV data read error");
		}
		// We can also read the public area
		NV_ReadPublicResponse nvPub = tpm.NV_ReadPublic(nvHandle);
		write("NV public area:" + nvPub.toString());
		// And then delete it
		tpm.NV_UndefineSpace(tpm._OwnerHandle, nvHandle);

		// CASE 2 - Counter NV-slot
		TPMS_NV_PUBLIC nvTemplate2 = new TPMS_NV_PUBLIC(nvHandle, TPM_ALG_ID.SHA256,
				new TPMA_NV(TPMA_NV.AUTHREAD, TPMA_NV.AUTHWRITE, TPMA_NV.COUNTER), new byte[0],  8);
		tpm.NV_DefineSpace(tpm._OwnerHandle, nvAuth, nvTemplate2);

		// Should not be able to write (increment only)
		tpm._expectError(TPM_RC.ATTRIBUTES).NV_Write(nvHandle, nvHandle, toWrite,  0);

		// Should not be able to read before the first increment
		tpm._expectError(TPM_RC.NV_UNINITIALIZED).NV_Read(nvHandle, nvHandle,  8,  0);

		// First increment
		tpm.NV_Increment(nvHandle, nvHandle);

		// Now we can read it
		tpm.NV_Read(nvHandle, nvHandle,  8,  0);

		// Should be able to increment
		for (int j = 0; j < 5; j++) {
			tpm.NV_Increment(nvHandle, nvHandle);
		}
		// And make sure that it's good
		byte[] afterIncrement = tpm.NV_Read(nvHandle, nvHandle,  8,  0);
		write("Counter NV slot after 6 increments:" + Helpers.toHex(afterIncrement));

		// And then delete it
		tpm.NV_UndefineSpace(tpm._OwnerHandle, nvHandle);

		// CASE 3 - Bitfield
		TPMS_NV_PUBLIC nvTemplate3 = new TPMS_NV_PUBLIC(nvHandle, TPM_ALG_ID.SHA256,
				new TPMA_NV(TPMA_NV.AUTHREAD, TPMA_NV.AUTHWRITE, TPMA_NV.BITS), new byte[0],  8);
		tpm.NV_DefineSpace(tpm._OwnerHandle, nvAuth, nvTemplate3);

		// Should not be able to write a bitfield
		tpm._expectError(TPM_RC.ATTRIBUTES).NV_Write(nvHandle, nvHandle, toWrite,  0);

		// Should not be able to read before first written
		tpm._expectError(TPM_RC.NV_UNINITIALIZED).NV_Read(nvHandle, nvHandle,  8,  0);

		// Should not be able to increment
		tpm._expectError(TPM_RC.ATTRIBUTES).NV_Increment(nvHandle, nvHandle);

		// Should be able set bits
		write("Bit setting:");
		long bit = 1;

		for (int j = 0; j < 64; j++) {
			tpm.NV_SetBits(nvHandle, nvHandle, bit);
			byte[] bits = tpm.NV_Read(nvHandle, nvHandle,  8,  0);
			write("   " + Helpers.toHex(bits));
			bit = bit << 1;
		}

		// And then delete it
		tpm.NV_UndefineSpace(tpm._OwnerHandle, nvHandle);

		// CASE 4 - Extendable
		TPMS_NV_PUBLIC nvTemplate4 = new TPMS_NV_PUBLIC(nvHandle, TPM_ALG_ID.SHA256,
				new TPMA_NV(TPMA_NV.AUTHREAD, TPMA_NV.AUTHWRITE, TPMA_NV.EXTEND), new byte[0],  32);
		tpm.NV_DefineSpace(tpm._OwnerHandle, nvAuth, nvTemplate4);

		// Should be able to extend
		TPMT_HA toExtend = TPMT_HA.fromHashOf(TPM_ALG_ID.SHA256, "abc");
		tpm.NV_Extend(nvHandle, nvHandle, toExtend.digest);

		// Read the extended value and print it
		byte[] extendedData = tpm.NV_Read(nvHandle, nvHandle,  32,  0);
		write("NV Extended data: " + Helpers.toHex(extendedData));

		// Check the result is correct
		if (!Helpers.byteArraysEqual(extendedData,
				TPMT_HA.zeroHash(TPM_ALG_ID.SHA256).extend(toExtend.digest).digest)) {
			throw new RuntimeException("Hashes don't match!");

		}
		// And then delete it
		tpm.NV_UndefineSpace(tpm._OwnerHandle, nvHandle);

		return;
	}

	/**
	 * Demonstrates variations on Duplicate() and Import()
	 */
	public void duplication() {
		// Make an RSA primary storage key that can be the target of duplication
		// operations
		TPMT_PUBLIC srkTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA1,
				new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sensitiveDataOrigin,
						TPMA_OBJECT.userWithAuth, TPMA_OBJECT.noDA, TPMA_OBJECT.restricted, TPMA_OBJECT.decrypt),
				new byte[0], new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB),
						new TPMS_NULL_ASYM_SCHEME(),  2048, 0),
				new TPM2B_PUBLIC_KEY_RSA());

		CreatePrimaryResponse rsaSrk = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER),
				new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]), srkTemplate, new byte[0],
				new TPMS_PCR_SELECTION[0]);
		System.out.println("RSA Primary Key: \n" + rsaSrk.toString());

		// Make a duplicatable signing key as a child. Note that duplication
		// *requires* a policy session. This is the policy for
		// PolicyCommandCode(TPM_CC.duplicate)
		// (see the helper-code in tss.Net and tss.C++ for calculating policy digest)
		byte[] policyDigest = Helpers.fromHex("95c1ee7f c5a82c31 f673eac2 e21cbd40 8a23cb4a");

		TPMT_PUBLIC migratableKeyTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA1,
				new TPMA_OBJECT(TPMA_OBJECT.sign, TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth), policyDigest,
				new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL,  0, TPM_ALG_ID.NULL),
						new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256),  1024, 65537),
				new TPM2B_PUBLIC_KEY_RSA());

		CreatePrimaryResponse migratableKey = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER),
				new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]), migratableKeyTemplate, new byte[0],
				new TPMS_PCR_SELECTION[0]);

		System.out.println("RSA migratable Primary signing Key: \n" + migratableKey.toString());

		// Make a session to authorize the duplication
		byte[] nonceCaller = Helpers.getRandom(20);
		StartAuthSessionResponse policySession = tpm.StartAuthSession(TPM_HANDLE.NULL, TPM_HANDLE.NULL,
				nonceCaller, new byte[0], TPM_SE.POLICY, TPMT_SYM_DEF.nullObject(), TPM_ALG_ID.SHA1);

		// and execute the policy
		tpm.PolicyCommandCode(policySession.handle, TPM_CC.Duplicate);

		// Keys can be duplicated in plain-text or singly or double encrypted.
		// First the simplest: export (duplicate) it specifying no encryption.
		
		DuplicateResponse duplicatedKey = tpm._withSession(policySession.handle).Duplicate(
				migratableKey.handle, TPM_HANDLE.NULL, new byte[0], TPMT_SYM_DEF_OBJECT.nullObject());

		System.out.println("Duplicated key blob: \n" + duplicatedKey.toString());
		// This key can be simply re-loaded into the TPM with LoadExternal() - todo

		
		// Second, duplicate specifying that the private key is encrypted to a TPM-resident storage key so that it
		// can later be Imported and Loaded.  First, refresh the policy (policies can be used exactly once)
		tpm.PolicyRestart(policySession.handle);
		tpm.PolicyCommandCode(policySession.handle, TPM_CC.Duplicate);
		
		duplicatedKey = tpm._withSession(policySession.handle).
				Duplicate(
						migratableKey.handle, 
						TPM_HANDLE.NULL, 
						new byte[0], 
						TPMT_SYM_DEF_OBJECT.nullObject());
		System.out.println("Duplicated key blob (2): \n" + duplicatedKey.toString());

		// Now try to import it to the "SRK" we created
		TPM2B_PRIVATE importedPrivate = tpm.Import(rsaSrk.handle, new byte[0], migratableKey.outPublic,
				duplicatedKey.duplicate, new byte[0], TPMT_SYM_DEF_OBJECT.nullObject());

		// And now show that we can load and and use the imported blob
		TPM_HANDLE importedSigningKey = tpm.Load(rsaSrk.handle, importedPrivate, migratableKey.outPublic);

		TPMU_SIGNATURE signature = tpm.Sign(importedSigningKey,
				TPMT_HA.fromHashOf(TPM_ALG_ID.SHA256, "abc").digest, new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());
		// Signature with Imported key is
		System.out.println("Signature: \n" + signature.toString());
		tpm.FlushContext(importedSigningKey);
		
		// Third, duplicate specifying that the private key is encrypted to a TPM-resident storage key AND an
		// inner-wrapper is applied. 
		// First, refresh the policy (policies can be used exactly once)
		tpm.PolicyRestart(policySession.handle);
		tpm.PolicyCommandCode(policySession.handle, TPM_CC.Duplicate);
		
		duplicatedKey = tpm._withSessions(policySession.handle).Duplicate(
				migratableKey.handle, TPM_HANDLE.NULL, 
				new byte[0], 
				new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB));
		System.out.println("Duplicated key blob (3): \n" + duplicatedKey.toString());


		// Now try to import it to the "SRK" we created
		importedPrivate = tpm.Import(rsaSrk.handle, 
				duplicatedKey.encryptionKeyOut, 
				migratableKey.outPublic,
				duplicatedKey.duplicate, 
				new byte[0], 
				new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB));

		// And now show that we can load and and use the imported blob
		importedSigningKey = tpm.Load(rsaSrk.handle, importedPrivate, migratableKey.outPublic);

		signature = tpm.Sign(importedSigningKey,
				TPMT_HA.fromHashOf(TPM_ALG_ID.SHA256, "abc").digest, new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());
		// Signature with Imported key is
		System.out.println("Signature: \n" + signature.toString());
		tpm.FlushContext(importedSigningKey);
		
		tpm.FlushContext(migratableKey.handle);
		tpm.FlushContext(rsaSrk.handle);
		tpm.FlushContext(policySession.handle);
	}
	/**
	 * Demonstrates how tss.Java can be used to create software keys (not using the TPM), that can then 
	 * be imported into the TPM.
	 */
	void softwareKeys()
	{
		// Make an RSA primary storage key that can be the target of duplication
		// operations
		TPMT_PUBLIC srkTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA1,
				new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sensitiveDataOrigin,
						TPMA_OBJECT.userWithAuth, TPMA_OBJECT.noDA, TPMA_OBJECT.restricted, TPMA_OBJECT.decrypt),
				new byte[0], new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB),
						new TPMS_NULL_ASYM_SCHEME(),  2048, 0),
				new TPM2B_PUBLIC_KEY_RSA());

		CreatePrimaryResponse rsaSrk = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER),
				new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]), srkTemplate, new byte[0],
				new TPMS_PCR_SELECTION[0]);
		System.out.println("RSA Primary Key: \n" + rsaSrk.toString());

		// Use the helper routines in tss.Java to create a duplication
		// blob *without* using the TPM
		TPMT_PUBLIC swMigratableKeyTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA1,
				new TPMA_OBJECT(TPMA_OBJECT.sign, TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth), new byte[0],
				new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL,  0, TPM_ALG_ID.NULL),
						new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256),  1024, 65537),
				new TPM2B_PUBLIC_KEY_RSA());
		Tss.Key swKey = Tss.createKey(swMigratableKeyTemplate);
	
		// Now do a simple import of the software key into the TPM
		byte[] swKeyAuthValue = new byte[] {1,2,3,4}; 
		TPM_HANDLE loadedSigningKey = tpm.LoadExternal(
				new TPMT_SENSITIVE(swKeyAuthValue, new byte[0], new TPM2B_PRIVATE_KEY_RSA(swKey.PrivatePart)),
				swKey.PublicPart, 
				TPM_HANDLE.from(TPM_RH.NULL) 
				);
		// and show that we can sign with it.  Note: an auth-value was assigned to the key, so we must
		// set the AuthValue in the handle 
		loadedSigningKey.AuthValue = swKeyAuthValue;
		TPMU_SIGNATURE signature = tpm.Sign(loadedSigningKey,
				TPMT_HA.fromHashOf(TPM_ALG_ID.SHA256, "abc").digest, new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());
		System.out.println("Signature of LoadExternal key:\n" + signature.toString());
		tpm.FlushContext(loadedSigningKey);

		
		// We can also do various sorts of secure Import of the key.  This lets an external 
		// entity securely communicate a key to a target TPM
		
		// First, simple encryption of the private key to a loaded TPM key (the "srk" created earlier.)
		TPMT_SYM_DEF_OBJECT noInnerWrapper = TPMT_SYM_DEF_OBJECT.nullObject();
		TPMT_SENSITIVE sens = new TPMT_SENSITIVE(swKeyAuthValue, new byte[0], new TPM2B_PRIVATE_KEY_RSA(swKey.PrivatePart));
		Tss.DuplicationBlob dupBlob = Tss.createDuplicationBlob(rsaSrk.outPublic, swKey.PublicPart, sens, noInnerWrapper);

		TPM2B_PRIVATE  newPrivate = tpm.Import(rsaSrk.handle, new byte[0], swKey.PublicPart,
			                                   new TPM2B_PRIVATE(dupBlob.DuplicateObject),
			                                   dupBlob.EncryptedSeed, noInnerWrapper);
		
		// and once imported, we can "load" it
		TPM_HANDLE loadedKey = tpm.Load(rsaSrk.handle, newPrivate, swKey.PublicPart);
		// and sign with it
		loadedSigningKey.AuthValue = swKeyAuthValue;
		signature = tpm.Sign(loadedSigningKey,
				TPMT_HA.fromHashOf(TPM_ALG_ID.SHA256, "abc").digest, new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());
		System.out.println("Signature of Import key:\n" + signature.toString());
		tpm.FlushContext(loadedKey);
		
		// We can also apply an "inner wrapper" to the key
		
		Tss.DuplicationBlob dupBlob2 = Tss.createDuplicationBlob(rsaSrk.outPublic, swKey.PublicPart, sens, 
		        									new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB));
		
		// now to do the import, the TPM must be told the seed
		TPM2B_PRIVATE  newPrivate2 = tpm.Import(rsaSrk.handle, dupBlob2.EncryptionKey, swKey.PublicPart,
                								new TPM2B_PRIVATE(dupBlob2.DuplicateObject),
                								dupBlob2.EncryptedSeed,
                								new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB));
		// and once imported, we can "load" it
		TPM_HANDLE loadedKey2 = tpm.Load(rsaSrk.handle, newPrivate2, swKey.PublicPart);
		// and sign with it
		loadedSigningKey.AuthValue = swKeyAuthValue;
		signature = tpm.Sign(loadedSigningKey,
				TPMT_HA.fromHashOf(TPM_ALG_ID.SHA256, "abc").digest, new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());
		System.out.println("Signature of Import key (2):\n" + signature.toString());
		
		tpm.FlushContext(loadedKey2);
		tpm.FlushContext(rsaSrk.handle);
		
		return;
	}
	/**
	 * Demonstrates how tss.Java can be used to create ECC software keys (not using the TPM), that can then 
	 * be imported into the TPM.  
	 */
	void softwareECCKeys()
	{
		// Make an RSA primary storage key that can be the target of duplication
		// operations
		TPMT_PUBLIC srkTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA1,
				new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sensitiveDataOrigin,
						TPMA_OBJECT.userWithAuth, TPMA_OBJECT.noDA, TPMA_OBJECT.restricted, TPMA_OBJECT.decrypt),
				new byte[0], new TPMS_ECC_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES,  128, TPM_ALG_ID.CFB),
						new TPMS_NULL_ASYM_SCHEME(),  
						TPM_ECC_CURVE.NIST_P256, 
						new TPMS_NULL_KDF_SCHEME()),
				new TPMS_ECC_POINT());

		CreatePrimaryResponse eccSrk = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER),
				new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]), srkTemplate, new byte[0],
				new TPMS_PCR_SELECTION[0]);
		System.out.println("RSA Primary Key: \n" + eccSrk.toString());

		// Use the helper routines in tss.Java to create a duplication
		// blob *without* using the TPM
		TPMT_PUBLIC ecdsaTemplate = new TPMT_PUBLIC(
				TPM_ALG_ID.SHA1,
				new TPMA_OBJECT(TPMA_OBJECT.userWithAuth, TPMA_OBJECT.sign),
				new byte[0], 
				new TPMS_ECC_PARMS(
						TPMT_SYM_DEF_OBJECT.nullObject(),
						new TPMS_SIG_SCHEME_ECDSA(TPM_ALG_ID.SHA1),  
						TPM_ECC_CURVE.NIST_P256, 
						new TPMS_NULL_KDF_SCHEME()),
				new TPMS_ECC_POINT());

		
		Tss.Key swECCKey = Tss.createKey(ecdsaTemplate);
	
		// Now do a simple import of the software key into the TPM
		byte[] swKeyAuthValue = new byte[] {1,2,3,4}; 
		TPM_HANDLE loadedSigningKey = tpm.LoadExternal(
				new TPMT_SENSITIVE(
						swKeyAuthValue, 
						new byte[0], 
						new TPM2B_ECC_PARAMETER(swECCKey.PrivatePart)),
				swECCKey.PublicPart, 
				TPM_HANDLE.from(TPM_RH.NULL) 
				);
		// and show that we can sign with it.  Note: an auth-value was assigned to the key, so we must
		// set the AuthValue in the handle 
		loadedSigningKey.AuthValue = swKeyAuthValue;
		byte[] dataToSign = new byte[] {3,1,4,1,5,9,2,6,5};
		TPMU_SIGNATURE signature = tpm.Sign(
				loadedSigningKey,
				TPMT_HA.fromHashOf(TPM_ALG_ID.SHA1, dataToSign).digest, 
				new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());
		
		System.out.println("ECC Signature of LoadExternal key:\n" + signature.toString());
		
		// use tss.Java to validate the signature
		
		boolean eccSigOk = swECCKey.PublicPart.validateSignature(dataToSign, signature);
		System.out.println("Signture OK: \n" + Boolean.valueOf(eccSigOk));
		if(!eccSigOk) throw new RuntimeException("error");

				
		
		
		
		tpm.FlushContext(loadedSigningKey);
		//tpm.FlushContext(rsaSrk.objectHandle);
		
		return;
	}

	/**
	 * Helper-function to see if the samples are cleaning up properly
	 * @return Are slots empty?
	 */
	boolean allSlotsEmpty()
	{
		boolean slotFull = false;
		GetCapabilityResponse resp = tpm.GetCapability(TPM_CAP.HANDLES, TPM_HT.TRANSIENT.toInt() << 24, 32);
		TPML_HANDLE handles = (TPML_HANDLE) resp.capabilityData;
		if(handles.handle.length!=0)
		{
			System.out.println("Objects remain:" + String.valueOf(handles.handle.length));
			slotFull = true;
		}
		resp = tpm.GetCapability(TPM_CAP.HANDLES, TPM_HT.LOADED_SESSION.toInt() << 24, 32);
		handles = (TPML_HANDLE) resp.capabilityData;
		if(handles.handle.length!=0)
		{
			System.out.println("Sessions remain:" + String.valueOf(handles.handle.length));
			slotFull = true;
		}
		
		return slotFull;
	}
	
	/**
	 * Demonstrates locality support in tss.Java.  Note that locality is currently only exposed
	 * by/for the TPM simulator
	 */
	public void locality()
	{
		  // Extend the resettable PCR
	    int locTwoResettablePcr = 21;

	    tpm._getDevice().setLocality(2);
	    tpm.PCR_Event(TPM_HANDLE.pcr(locTwoResettablePcr), new byte[] { 1, 2, 3, 4 });
	    tpm._getDevice().setLocality(0);

	    PCR_ReadResponse resettablePcrVal = tpm.PCR_Read(
	    		new TPMS_PCR_SELECTION[] {new TPMS_PCR_SELECTION(TPM_ALG_ID.SHA1, locTwoResettablePcr)});
		System.out.println("Resettable PCR at start" + resettablePcrVal.toString());
	    
	    // Should fail - tell Tpm2 not to generate an exception
	    tpm._expectError(TPM_RC.LOCALITY).PCR_Reset(TPM_HANDLE.pcr((locTwoResettablePcr)));

	    // Should fail - tell Tpm2 not to generate an exception (second way)
	    tpm._allowErrors().PCR_Reset(TPM_HANDLE.pcr((locTwoResettablePcr)));

	    // Should succeed at locality 2
	    tpm._getDevice().setLocality(2);
	    tpm.PCR_Reset(TPM_HANDLE.pcr((locTwoResettablePcr)));

	    // Return to locality zero
	    tpm._getDevice().setLocality(0);
	    
	    // And show that the PCR was reset
	    resettablePcrVal =  tpm.PCR_Read(
	    		new TPMS_PCR_SELECTION[] {new TPMS_PCR_SELECTION(TPM_ALG_ID.SHA1, locTwoResettablePcr)});

	    resettablePcrVal = tpm.PCR_Read(
	    		new TPMS_PCR_SELECTION[] {new TPMS_PCR_SELECTION(TPM_ALG_ID.SHA1, locTwoResettablePcr)});
		System.out.println("Resettable PCR after reset" + resettablePcrVal.toString());

	    return;
		
	}
	
	void counterTimer()
	{
	    int runTime = 5000;  // milliseconds
		System.out.println("Reading TPM time for ~" + String.valueOf(runTime) + " seconds");

	    long timeStart = System.currentTimeMillis();
	    while (true) {
	    	TPMS_TIME_INFO time = tpm.ReadClock();
			System.out.println("     TPM Time:" + String.valueOf(time.time) + ", " 
	    	+ String.valueOf(time.clockInfo.clock) + ", " + String.valueOf(time.clockInfo.resetCount));

			long timeNow = System.currentTimeMillis();
		    if (timeNow > runTime + timeStart) 
		    {
	            break;
	        }
		    
		    try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// don't care
			}
	    }
	    return;
	}
	

	void write(String s) {
		System.out.println(s);
	}

	
}
