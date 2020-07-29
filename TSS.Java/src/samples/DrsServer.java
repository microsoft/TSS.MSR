package samples;

import tss.*;
import tss.tpm.*;


public class DrsServer {

    static TPMT_PUBLIC IdKeyTemplate = null;
    static TPMS_SENSITIVE_CREATE IdKeySens = null;
    
    public static int VerifyIdSignature(Tpm tpm, byte[] data, byte[] sig)
    {
        byte[] hmac = Crypto.hmac(TPM_ALG_ID.SHA256, IdKeySens.data, data);
        return (Helpers.arraysAreEqual(sig, hmac) ? TPM_RC.SUCCESS : TPM_RC.SIGNATURE).toInt();
    }
    
    public static int GetActivationBlob2(Tpm tpm, byte[] ekPubBlob, int ekPubSize, byte[] srkPubBlob, int srkPubSize,
                                                  byte[] actBlobBuffer, int blobBufCapacity)
    {
        TPMT_PUBLIC ekPub = TPM2B_PUBLIC.fromBytes(ekPubBlob).publicArea; 
        TPMT_PUBLIC srkPub = TPM2B_PUBLIC.fromBytes(srkPubBlob).publicArea;
        
        // Start a policy session required for key duplication
        TPM_HANDLE sess = tpm.StartAuthSession(TPM_HANDLE.NULL, TPM_HANDLE.NULL,
                                               Helpers.RandomBytes(20), new byte[0], TPM_SE.POLICY,
                                               new TPMT_SYM_DEF(TPM_ALG_ID.NULL, 0, TPM_ALG_ID.NULL), TPM_ALG_ID.SHA256)
                        .handle;
        // Run the necessary policy command
        tpm.PolicyCommandCode(sess, TPM_CC.Duplicate);
        // Retrieve the policy digest computed by the TPM
        byte[] dupPolicyDigest = tpm.PolicyGetDigest(sess);
        
        IdKeyTemplate = new TPMT_PUBLIC(
                TPM_ALG_ID.SHA256,
                new TPMA_OBJECT(TPMA_OBJECT.sign, TPMA_OBJECT.userWithAuth, TPMA_OBJECT.noDA),
                dupPolicyDigest,
                new TPMS_KEYEDHASH_PARMS(new TPMS_SCHEME_HMAC(TPM_ALG_ID.SHA256)),
                new TPM2B_DIGEST_KEYEDHASH());
    
        byte[] keyBytes = Helpers.RandomBytes(32);
        IdKeySens = new TPMS_SENSITIVE_CREATE(new byte[0], keyBytes);
        CreatePrimaryResponse idKey = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER), IdKeySens, IdKeyTemplate,
                                                           new byte[0], new TPMS_PCR_SELECTION[0]);
        
        TPM_HANDLE srkPubHandle = tpm.LoadExternal(null, srkPub, TPM_HANDLE.from(TPM_RH.OWNER));
        
        TPMT_SYM_DEF_OBJECT symWrapperDef = new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, 128, TPM_ALG_ID.CFB);
        DuplicateResponse dupResp = tpm._withSession(sess)
                                       .Duplicate(idKey.handle, srkPubHandle, new byte[0], symWrapperDef);

        tpm.FlushContext(srkPubHandle);

        TPM_HANDLE ekPubHandle = tpm.LoadExternal(null, ekPub, TPM_HANDLE.from(TPM_RH.ENDORSEMENT));

        MakeCredentialResponse cred = tpm.MakeCredential(ekPubHandle, dupResp.encryptionKeyOut, srkPub.getName());

    
        // Delete the key and session handles
        tpm.FlushContext(ekPubHandle);
        tpm.FlushContext(idKey.handle);
        tpm.FlushContext(sess);
        
        
        final TPMT_PUBLIC symWrapperTemplate = new TPMT_PUBLIC(
                TPM_ALG_ID.SHA256,
                new TPMA_OBJECT(TPMA_OBJECT.decrypt, TPMA_OBJECT.encrypt, TPMA_OBJECT.userWithAuth),
                new byte[0],
                new TPMS_SYMCIPHER_PARMS(symWrapperDef),
                new TPM2B_DIGEST());

        //
        // Encrypt URI data to be passed to the client device
        //
        TPMS_SENSITIVE_CREATE sens = new TPMS_SENSITIVE_CREATE(new byte[0], dupResp.encryptionKeyOut);
        TPM_HANDLE symWrapperHandle = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER), sens, symWrapperTemplate,
                                                          new byte[0], new TPMS_PCR_SELECTION[0])
                                    .handle;
        
        byte[] uriData =  "http://my.test.url/TestDeviceID=F4ED90771DAA7C0B3230FF675DF8A61104AE7C8BB0093FD6A".getBytes();    // Charset.forName("UTF-8")
        byte[] iv = new byte[dupResp.encryptionKeyOut.length];
        byte[] encryptedUri = tpm.EncryptDecrypt(symWrapperHandle, (byte)0, TPM_ALG_ID.CFB, iv, uriData).outData;
        
    
        // Delete the key and session handles
        tpm.FlushContext(symWrapperHandle);


        //
        // Build activation blob for the client device
        //
        
        TpmBuffer actBlob = new TpmBuffer();
        
        byte[] credBlob = cred.credentialBlob.toBytes();
        actBlob.writeShort(credBlob.length);
        actBlob.writeByteBuf(credBlob);

        actBlob.writeShort(cred.secret.length);
        actBlob.writeByteBuf(cred.secret);
        
        dupResp.duplicate.toTpm(actBlob);
        
        actBlob.writeShort(dupResp.outSymSeed.length);
        actBlob.writeByteBuf(dupResp.outSymSeed);
        
        byte[] idKeyPub = idKey.outPublic.toBytes();
        actBlob.writeShort(idKeyPub.length);
        actBlob.writeByteBuf(idKeyPub);
        
        actBlob.writeShort(encryptedUri.length);
        actBlob.writeByteBuf(encryptedUri);
        
        System.arraycopy(actBlob.buffer(), 0, actBlobBuffer, 0, actBlob.curPos());
        return actBlob.curPos();
    } // GetActivationBlob2()

}
