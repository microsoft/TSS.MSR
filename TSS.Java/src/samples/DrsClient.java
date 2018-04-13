package samples;

//import java.io.Console;
import java.nio.charset.Charset;
import java.util.Arrays;

import tss.*;
import tss.tpm.*;

/**
 * Example demonstrating client side implementation of the interface with Azure IoT Device Registration Service
 */

public class DrsClient
{
	static final TPM_HANDLE SRK_PersHandle = TPM_HANDLE.persistent(0x00000001);
	static final TPM_HANDLE EK_PersHandle = TPM_HANDLE.persistent(0x00010001);
	static final TPM_HANDLE ID_KEY_PersHandle = TPM_HANDLE.persistent(0x00000100);

	static final TPMT_SYM_DEF_OBJECT Aes128SymDef = new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, 128, TPM_ALG_ID.CFB);
	static final TPMT_SYM_DEF_OBJECT NullSymDef = new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, 128, TPM_ALG_ID.CFB);

	static final TPMT_PUBLIC EK_Template = new TPMT_PUBLIC(
			// TPMI_ALG_HASH	nameAlg
		    TPM_ALG_ID.SHA256,
		    // TPMA_OBJECT  objectAttributes
		    new TPMA_OBJECT(TPMA_OBJECT.restricted, TPMA_OBJECT.decrypt, TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent,
		       				TPMA_OBJECT.adminWithPolicy, TPMA_OBJECT.sensitiveDataOrigin),
		    // TPM2B_DIGEST authPolicy
		    javax.xml.bind.DatatypeConverter.parseHexBinary("837197674484b3f81a90cc8d46a5d724fd52d76e06520b64f2a1da1b331469aa"),
		    // TPMU_PUBLIC_PARMS    parameters
		    new TPMS_RSA_PARMS(Aes128SymDef, new TPMS_NULL_ASYM_SCHEME(), 2048, 0),
		    // TPMU_PUBLIC_ID       unique
		    new TPM2B_PUBLIC_KEY_RSA());
	
	static final TPMT_PUBLIC SRK_Template = new TPMT_PUBLIC(
			// TPMI_ALG_HASH	nameAlg
		    TPM_ALG_ID.SHA256,
		    // TPMA_OBJECT  objectAttributes
		    new TPMA_OBJECT(TPMA_OBJECT.restricted, TPMA_OBJECT.decrypt, TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent,
		       				TPMA_OBJECT.noDA, TPMA_OBJECT.userWithAuth, TPMA_OBJECT.sensitiveDataOrigin),
		    // TPM2B_DIGEST authPolicy
		    new byte[0],
		    // TPMU_PUBLIC_PARMS    parameters
		    new TPMS_RSA_PARMS(Aes128SymDef, new TPMS_NULL_ASYM_SCHEME(), 2048, 0),
		    // TPMU_PUBLIC_ID       unique
		    new TPM2B_PUBLIC_KEY_RSA());
	
	
	static void Print (String fmt, Object ...args)
	{
		System.out.printf(fmt + (fmt.endsWith("\n") ? "" : "\n"), args);
	}
	
	
	static void ClearPersistent(Tpm tpm, TPM_HANDLE hPers, String keyRole)
	{
		tpm._allowErrors().ReadPublic(hPers);
	    TPM_RC	rc = tpm._getLastResponseCode();
	    if (rc == TPM_RC.SUCCESS)
	    {
	        Print("Deleting persistent %s 0x%08X", keyRole, hPers.handle);
	        tpm.EvictControl(TPM_HANDLE.from(TPM_RH.OWNER), hPers, hPers);
	        Print("Successfully deleted persistent %s 0x%08X", keyRole, hPers.handle);
	    }
	    else if (rc == TPM_RC.HANDLE)
	    {
	        Print("%s 0x%08X does not exist", keyRole, hPers.handle);
	    }
	    else
	        Print("Unexpected failure <%s> of TPM2_ReadPublic for %s 0x%08X", rc, keyRole, hPers.handle);
	}

	static TPMT_PUBLIC
	CreatePersistentPrimary(Tpm tpm, TPM_HANDLE hPers, TPM_RH hierarchy, TPMT_PUBLIC inPub, String primaryRole)
	{
	    ReadPublicResponse rpResp = tpm._allowErrors().ReadPublic(hPers);
	    TPM_RC	rc = tpm._getLastResponseCode();
	    if (rc == TPM_RC.SUCCESS)
	    {
	    	// TODO: Check if the public area of the existing key matches the requested one
	    	Print(">> %s already exists\r\n", primaryRole);
	        return rpResp.outPublic;
	    }
	    if (rc != TPM_RC.HANDLE)
	    {
	        Print("Unexpected failure {%s} of TPM2_ReadPublic for %s 0x%08X", rc.name(), primaryRole, hPers);
	        return null; // TPM_RH_NULL
	    }

	    TPMS_SENSITIVE_CREATE sens = new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]);
		CreatePrimaryResponse cpResp = tpm.CreatePrimary(TPM_HANDLE.from(hierarchy), sens, inPub,
													  new byte[0], new TPMS_PCR_SELECTION[0]);
	    Print(">> Successfully created transient %s 0x%08X\r\n", primaryRole, cpResp.handle.handle);

	    tpm.EvictControl(TPM_HANDLE.from(TPM_RH.OWNER), cpResp.handle, hPers);
	    Print(">> Successfully persisted %s as 0x%08X\r\n", primaryRole, hPers.handle);

	    tpm.FlushContext(cpResp.handle);
	    return cpResp.outPublic;
	}
	

	// NOTE: For now only HMAC signing is supported.
	static byte[] SignData(Tpm tpm, TPMT_PUBLIC idKeyPub, byte[] tokenData)
	{
	    TPM_ALG_ID	idKeyHashAlg = ((TPMS_SCHEME_HMAC)((TPMS_KEYEDHASH_PARMS)idKeyPub.parameters).scheme).hashAlg;
	    int 		MaxInputBuffer = TpmHelpers.getTpmProperty(tpm, TPM_PT.INPUT_BUFFER);

	    if (tokenData.length <= MaxInputBuffer)
	    {
	    	return tpm.HMAC(ID_KEY_PersHandle, tokenData, idKeyHashAlg);
	    }
	    
        int curPos = 0;
        int bytesLeft = tokenData.length;

        TPM_HANDLE  hSeq = tpm.HMAC_Start(ID_KEY_PersHandle, new byte[0], idKeyHashAlg);

        do {
        	tpm.SequenceUpdate(hSeq, Arrays.copyOfRange(tokenData, curPos, curPos + MaxInputBuffer));

            bytesLeft -= MaxInputBuffer;
            curPos += MaxInputBuffer;
        } while (bytesLeft > MaxInputBuffer);

        return tpm.SequenceComplete(hSeq, Arrays.copyOfRange(tokenData, curPos, curPos + bytesLeft), TPM_HANDLE.from(TPM_RH.NULL)).result;
	}

	
	public static void runProvisioningSequence(Tpm tpm)
	{
			
		try
		{
			if (CmdLine.isOptionPresent("clear", "c"))
			{
				System.out.println("Clearing keys ...");
				ClearPersistent(tpm, EK_PersHandle, "EK");
				ClearPersistent(tpm, SRK_PersHandle, "SRK");
				ClearPersistent(tpm, ID_KEY_PersHandle, "ID");
				return;
			}
			
		    TPMT_PUBLIC	ekPub = null,
		    			srkPub = null;
			
		    //
		    // Make sure that device keys used in activation protocol exist
		    //
			ekPub = CreatePersistentPrimary(tpm, EK_PersHandle, TPM_RH.ENDORSEMENT, EK_Template, "EK");
			srkPub = CreatePersistentPrimary(tpm, SRK_PersHandle, TPM_RH.OWNER, SRK_Template, "SRK");
		
		    int blobBufCapacity = 4096;
		    byte[]  actBlobBuffer = new byte[blobBufCapacity];
		    
		    //
		    // Obtain activation blob from the server
		    //

		    int actBlobSize = 0;
			byte[]	ekBlobToSend = (new TPM2B_PUBLIC(ekPub)).toTpm(),
					srkBlobToSend = (new TPM2B_PUBLIC(srkPub)).toTpm();
			
			// Initial version of the DRS protocol expected only key bytes (without complete template data)  
			//byte[]  ekUnique = ((TPM2B_PUBLIC_KEY_RSA)ekPub.unique).buffer,
			//   		srkUnique = ((TPM2B_PUBLIC_KEY_RSA)srkPub.unique).buffer;
			
		    actBlobSize = DrsServer.GetActivationBlob2(tpm, ekBlobToSend, ekBlobToSend.length,
					    									   srkBlobToSend, srkBlobToSend.length,
					    									   actBlobBuffer, blobBufCapacity);
		    
		    if (actBlobSize <= 0)
		    	throw new Exception("Unexpected DRS failure");
		    
		    //
		    // Unmarshal components of the activation blob generated by DRS
		    //
		    InByteBuf actBlob = new InByteBuf(Arrays.copyOfRange(actBlobBuffer, 0, actBlobSize));
		    
		    TPM2B_ID_OBJECT         credBlob = TPM2B_ID_OBJECT.fromTpm(actBlob);
		    Print("credBlob end: %d", actBlob.curPos());
		    TPM2B_ENCRYPTED_SECRET  encSecret = TPM2B_ENCRYPTED_SECRET.fromTpm(actBlob);
		    Print("encSecret end: %d", actBlob.curPos());
		    TPM2B_PRIVATE           idKeyDupBlob = TPM2B_PRIVATE.fromTpm(actBlob);
		    Print("idKeyDupBlob end: %d", actBlob.curPos());
		    TPM2B_ENCRYPTED_SECRET  encWrapKey = TPM2B_ENCRYPTED_SECRET.fromTpm(actBlob);
		    Print("encWrapKey end: %d", actBlob.curPos());
		    TPM2B_PUBLIC    		idKeyPub = TPM2B_PUBLIC.fromTpm(actBlob);
		    Print("idKeyPub end: %d", actBlob.curPos());
		    TPM2B_DATA				encUriData = TPM2B_DATA.fromTpm(actBlob);
		    Print("encUriData end: %d", actBlob.curPos());

		    // Start a policy session to be used with ActivateCredential()
		    StartAuthSessionResponse sasResp = tpm.StartAuthSession(TPM_HANDLE.NULL, TPM_HANDLE.NULL,
		    											Helpers.getRandom(20), new byte[0], TPM_SE.POLICY,
		    											new TPMT_SYM_DEF(TPM_ALG_ID.NULL, 0, TPM_ALG_ID.NULL), TPM_ALG_ID.SHA256);
		    
			// Apply the policy necessary to authorize an EK on Windows
		    tpm.PolicySecret(TPM_HANDLE.from(TPM_RH.ENDORSEMENT), sasResp.handle,
		    				 new byte[0], new byte[0], new byte[0], 0);
		    
		    // Use ActivateCredential() to decrypt symmetric key that is used as an inner protector
		    // of the duplication blob of the new Device ID key generated by DRS.
		    byte[] innerWrapKey = tpm._withSessions(TPM_HANDLE.pwSession(new byte[0]), sasResp.handle)	
		    						 .ActivateCredential(SRK_PersHandle, EK_PersHandle, credBlob.credential, encSecret.secret);

		    // Initialize parameters of the symmetric key used by DRS 
		    // Note that the client uses the key size chosen by DRS, but other parameters are fixes (an AES key in CFB mode).
		    TPMT_SYM_DEF_OBJECT symDef = new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, innerWrapKey.length * 8, TPM_ALG_ID.CFB);
		    
		    //
		    // Import the new Device ID key issued by DRS into the device's TPM
		    //
		    TPM2B_PRIVATE idKeyPriv = tpm.Import(SRK_PersHandle, innerWrapKey, idKeyPub.publicArea, idKeyDupBlob, encWrapKey.secret, symDef);
		    
		    //
		    // Load and persist new Device ID key issued by DRS
		    //
	    
		    TPM_HANDLE hIdkey = tpm.Load(SRK_PersHandle, idKeyPriv, idKeyPub.publicArea);

		    ClearPersistent(tpm, ID_KEY_PersHandle, "ID Key");
		    
		    tpm.EvictControl(TPM_HANDLE.from(TPM_RH.OWNER), hIdkey, ID_KEY_PersHandle);
		    Print("Successfully created persistent %s 0x%08X\r\n", "ID Key", ID_KEY_PersHandle.handle);

		    tpm.FlushContext(hIdkey);
		    
		    //
		    // Decrypt URI data using TPM.
		    // A recommended alternative for the actual SDK code is to use the symmetric algorithm from a software crypto library
		    //
		    int maxUriDataSize = TpmHelpers.getTpmProperty(tpm, TPM_PT.INPUT_BUFFER);
		    if (encUriData.buffer.length > maxUriDataSize)
		        throw new Exception("Too long encrypted URI data string. Max supported length is " + Integer.toString(maxUriDataSize));
		
		    
		    // The template of the symmetric key used by the DRS
			TPMT_PUBLIC symTemplate = new TPMT_PUBLIC(
					// TPMI_ALG_HASH	nameAlg
				    TPM_ALG_ID.SHA256,
				    // TPMA_OBJECT  objectAttributes
				    new TPMA_OBJECT(TPMA_OBJECT.decrypt, TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.userWithAuth),
				    // TPM2B_DIGEST authPolicy
				    new byte[0],
				    // TPMU_PUBLIC_PARMS    parameters
				    new TPMS_SYMCIPHER_PARMS(symDef),
				    // TPMU_PUBLIC_ID       unique
				    new TPM2B_DIGEST_Symcipher());

			// URI data are encrypted with the same symmetric key used as the inner protector of the new Device ID key duplication blob.
		    TPMS_SENSITIVE_CREATE sensCreate = new TPMS_SENSITIVE_CREATE (new byte[0], innerWrapKey);
		    CreateResponse crResp = tpm.Create(SRK_PersHandle, sensCreate, symTemplate, new byte[0], new TPMS_PCR_SELECTION[0]);
			
		    TPM_HANDLE hSymKey = tpm.Load(SRK_PersHandle, crResp.outPrivate, crResp.outPublic);
		    
		    byte[] iv = new byte[innerWrapKey.length];
		    EncryptDecryptResponse edResp = tpm.EncryptDecrypt(hSymKey, (byte)1, TPM_ALG_ID.CFB, iv, encUriData.buffer);
			Print("Decrypted URI data size: %d", edResp.outData.length);
			Print("Decrypted URI [for native]: %s", new String(edResp.outData, Charset.forName("UTF-8")));
			Print("Decrypted URI [for java]: %s", new String(edResp.outData));
		    
		    tpm.FlushContext(hSymKey);
		    
		    //
		    // Generate token data, and sign it using the new Device ID key
		    // (Note that this sample simply generates a random buffer in lieu of a valid token)
		    //
		    
		    byte[] deviceIdData = Helpers.getRandom(2550);

		    byte[] signature = SignData(tpm, idKeyPub.publicArea, deviceIdData);

		    // Use DRS emulator library to make sure that the signature is correct
		    // Note that the actual SDK does not need a code like this.
		    int rc = DrsServer.VerifyIdSignature(tpm, deviceIdData, signature);
		    
		    if (rc != TPM_RC.SUCCESS.toInt())
		    	throw new Exception("Failed to verify a signature created by the new Device ID key");
		    
	    	Print("Successfully verified a signature created by the new Device ID key");
		}
		catch (TpmException te) {
			String	rcName = te.ResponseCode == null ? "<NONE>" : te.ResponseCode.name();
			String	msg = te.getMessage();
			Print("A TPM operations FAILED: error {%s}; message \"%s\"", rcName, msg);
		}
		catch (Exception e) {
			Print("An operation FAILED: Error message: \"%s\"", e.getMessage());
		}
		Print("RunProvisioningSequence finished!");
	}

	//static 
	
}
