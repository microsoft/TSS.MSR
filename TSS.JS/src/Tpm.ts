/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

import * as tt from "./TpmTypes.js";
import { TPM_HANDLE, TPM_CC, TPM_RC, TPM_ALG_ID, TPMT_PUBLIC, TPM2B_PRIVATE } from "./TpmTypes.js";
import { toTpm, fromTpm, toTpm2B, fromTpm2B, createFromTpm, sizedToTpm, arrayToTpm, TpmMarshaller } from "./TpmMarshaller.js";
import { TpmBase } from "./TpmBase.js";


export class Tpm extends TpmBase
{
    constructor(useSimulator: boolean = false,
                host: string = '127.0.0.1', port: number = 2321)
    {
        super(useSimulator, host, port);
    }

    public connect(continuation: () => void)
    {
        super.connect(continuation);
    }

    /**
     * This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
     * 
     * @param activateHandle handle of the object associated with certificate in credentialBlob Auth Index: 1 Auth Role: ADMIN 
     * @param keyHandle loaded key used to decrypt the TPMS_SENSITIVE in credentialBlob Auth Index: 2 Auth Role: USER 
     * @param credentialBlob the credential 
     * @param secret keyHandle algorithm-dependent encrypted seed that protects credentialBlob 
     * @return the decrypted certificate information the data should be no larger than the size of the digest of the nameAlg associated with keyHandle
     */
    ActivateCredential(activateHandle: TPM_HANDLE, keyHandle: TPM_HANDLE, credentialBlob: tt.TPMS_ID_OBJECT, secret: Buffer,
                       continuation: (Buffer) => void)
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.ActivateCredential, [activateHandle, keyHandle]);
        curPos = sizedToTpm(credentialBlob, cmdBuf, 2, curPos);
        curPos = toTpm2B(secret, cmdBuf, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.ActivateCredential, respBuf);
            let res = fromTpm2B(respBuf, pos)[0];
            if (res.length + 2 != paramSize)
                throw(new Error('ActivateCredential(): Invalid param size in response buffer: ' + paramSize + ' vs. actual ' + (res.length + 2)));
            setImmediate(continuation, res);
        });
    }

    /**
     * This command is used to create a Primary Object under one of the Primary Seeds or a Temporary Object under TPM_RH_NULL. The command uses a TPM2B_PUBLIC as a template for the object to be created. The size of the unique field shall not be checked for consistency with the other object parameters. The command will create and load a Primary Object. The sensitive area is not returned.
     * 
     * @param primaryHandle TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL Auth Index: 1 Auth Role: USER 
     * @param inSensitive the sensitive data, see TPM 2.0 Part 1 Sensitive Values 
     * @param inPublic the public template 
     * @param outsideInfo data that will be included in the creation data for this object to provide permanent, verifiable linkage between this object and some object owner data 
     * @param creationPCR PCR that will be used in creation data 
     * @return TPM2_CreatePrimary_RESPONSE{(ul)(li)(code)handle(/code) - handle of type TPM_HT_TRANSIENT for created Primary Object(/li)(li)(code)outPublic(/code) - the public portion of the created object(/li)(li)(code)creationData(/code) - contains a TPMT_CREATION_DATA(/li)(li)(code)creationHash(/code) - digest of creationData using nameAlg of outPublic(/li)(li)(code)creationTicket(/code) - ticket used by TPM2_CertifyCreation() to validate that the creation data was produced by the TPM(/li)(li)(code)name(/code) - the name of the created object(/li)(/ul)}
     */
    CreatePrimary(primaryHandle: TPM_HANDLE, inSensitive: tt.TPMS_SENSITIVE_CREATE, inPublic: TPMT_PUBLIC,
                  outsideInfo: Buffer, creationPCR: tt.TPMS_PCR_SELECTION[],
                  continuation: (CreatePrimaryResponse) => void): void
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.CreatePrimary, [primaryHandle]);
        curPos = sizedToTpm(inSensitive, cmdBuf, 2, curPos);
        curPos = sizedToTpm(inPublic, cmdBuf, 2, curPos);
        curPos = toTpm2B(outsideInfo, cmdBuf, curPos);
        curPos = arrayToTpm(creationPCR, cmdBuf, 4, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.CreatePrimary, respBuf);
            let res: tt.CreatePrimaryResponse = null;
            if (this.getLastResponseCode() == TPM_RC.SUCCESS)
                res = createFromTpm(tt.CreatePrimaryResponse, respBuf, pos)[0];
            setImmediate(continuation, res);
        });
    }

    /**
     * This command allows certain Transient Objects to be made persistent or a persistent object to be evicted.
     * 
     * @param auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER 
     * @param objectHandle the handle of a loaded object Auth Index: None 
     * @param persistentHandle if objectHandle is a transient object handle, then this is the persistent handle for the object if objectHandle is a persistent object handle, then it shall be the same value as persistentHandle
     */
    EvictControl(auth: TPM_HANDLE, objectHandle: TPM_HANDLE, persistentHandle: TPM_HANDLE,
                 continuation: () => void): void
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.EvictControl, [auth, objectHandle]);
        curPos = persistentHandle.toTpm(cmdBuf, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let paramSize = super.processResponse(TPM_CC.EvictControl, respBuf)[0];
            if (paramSize != 0)
                throw(new Error('EvictControl(): Non-empty response parameters area: ' + paramSize + ' bytes'));
            setImmediate(continuation);
        });
    }

    /**
     * This command causes all context associated with a loaded object, sequence object, or session to be removed from TPM memory.
     * 
     * @param flushHandle the handle of the item to flush NOTE This is a use of a handle as a parameter.
     */
    FlushContext(handle: TPM_HANDLE,
                 continuation: () => void): void
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.FlushContext, [handle]);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let paramSize = super.processResponse(TPM_CC.FlushContext, respBuf)[0];
            if (paramSize != 0)
                throw(new Error('FlushContext(): Non-empty response parameters area: ' + paramSize + ' bytes'));
            setImmediate(continuation);
        });
    }

    /**
     * This command returns various information regarding the TPM and its current state.
     * 
     * @param capability group selection; determines the format of the response 
     * @param property further definition of information 
     * @param propertyCount number of properties of the indicated type to return 
     * @return TPM2_GetCapability_RESPONSE{(ul)(li)(code)moreData(/code) - flag to indicate if there are more values of this type(/li)(li)(code)capabilityData(/code) - the capability data(/li)(/ul)}
     */
    GetCapability(capability: tt.TPM_CAP, property: number, propertyCount: number,
                  continuation: (GetCapabilityResponse) => void)
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.GetCapability, null);
        curPos = toTpm(capability, cmdBuf, 4, curPos);
        curPos = toTpm(property, cmdBuf, 4, curPos);
        curPos = toTpm(propertyCount, cmdBuf, 4, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.GetCapability, respBuf);
            let res: tt.GetCapabilityResponse = null;
            if (this.getLastResponseCode() == TPM_RC.SUCCESS)
                res = createFromTpm(tt.GetCapabilityResponse, respBuf, pos)[0];
            setImmediate(continuation, res);
        });
    }

    /**
     * This command returns the next bytesRequested octets from the random number generator (RNG).
     * 
     * @param bytesRequested number of octets to return 
     * @return the random octets
     */
    GetRandom(numBytes: number,
              continuation: (Buffer) => void): void
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.GetRandom, null);
        curPos = toTpm(numBytes, cmdBuf, 2, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.GetRandom, respBuf);
            let res: Buffer = fromTpm2B(respBuf, pos)[0];
            if (res.length + 2 != paramSize)
                throw(new Error('GetRandom(): Invalid param size in response buffer: ' + paramSize + ' vs. actual ' + (res.length + 2)));
            setImmediate(continuation, res);
        });
    }

    /**
     * This command performs an HMAC on the supplied data using the indicated hash algorithm.
     * 
     * @param handle handle for the symmetric signing key providing the HMAC key Auth Index: 1 Auth Role: USER 
     * @param buffer HMAC data 
     * @param hashAlg algorithm to use for HMAC
     */
    HMAC(handle: TPM_HANDLE, buffer: Buffer, hashAlg: TPM_ALG_ID,
         continuation: (Buffer) => void)
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.HMAC, [handle]);
        curPos = toTpm2B(buffer, cmdBuf, curPos);
        curPos = toTpm(hashAlg, cmdBuf, 2, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.HMAC, respBuf);
            let res: Buffer = fromTpm2B(respBuf, pos)[0];
            if (res.length + 2 != paramSize)
                throw(new Error('HMAC(): Invalid param size in response buffer: ' + paramSize + ' vs. actual ' + (res.length + 2)));
            setImmediate(continuation, res);
        });
    }

    /**
     * This command starts an HMAC sequence. The TPM will create and initialize an HMAC sequence structure, assign a handle to the sequence, and set the authValue of the sequence object to the value in auth.
     * 
     * @param handle handle of an HMAC key Auth Index: 1 Auth Role: USER 
     * @param auth authorization value for subsequent use of the sequence 
     * @param hashAlg the hash algorithm to use for the HMAC 
     * @return a handle to reference the sequence
     */
    HMAC_Start(handle: TPM_HANDLE, auth: Buffer, hashAlg: TPM_ALG_ID,
               continuation: (TPM_HANDLE) => void)
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.HMAC_Start, [handle]);
        curPos = toTpm2B(auth, cmdBuf, curPos);
        curPos = toTpm(hashAlg, cmdBuf, 2, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.HMAC_Start, respBuf);
            let res: TPM_HANDLE = createFromTpm(TPM_HANDLE, respBuf, pos)[0];
            setImmediate(continuation, res);
        });
    }

    /**
     * This command is used to add data to a hash or HMAC sequence. The amount of data in buffer may be any size up to the limits of the TPM.
     * 
     * @param sequenceHandle handle for the sequence object Auth Index: 1 Auth Role: USER 
     * @param buffer data to be added to hash
     */
    SequenceUpdate(sequenceHandle: TPM_HANDLE, buffer: Buffer,
                   continuation: () => void)
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.SequenceUpdate, [sequenceHandle]);
        curPos = toTpm2B(buffer, cmdBuf, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let paramSize = super.processResponse(TPM_CC.SequenceUpdate, respBuf)[0];
            if (paramSize != 0)
                throw(new Error('SequenceUpdate(): Non-empty response parameters area: ' + paramSize + ' bytes'));
            setImmediate(continuation);
        });
    }

    /**
    * This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
    * 
    * @param sequenceHandle authorization for the sequence Auth Index: 1 Auth Role: USER 
    * @param buffer data to be added to the hash/HMAC 
    * @param hierarchy hierarchy of the ticket for a hash 
    * @return TPM2_SequenceComplete_RESPONSE{(ul)(li)(code)result(/code) - the returned HMAC or digest in a sized buffer(/li)(li)(code)validation(/code) - ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE This is a NULL Ticket when the sequence is HMAC.(/li)(/ul)}
    */
    SequenceComplete(sequenceHandle: TPM_HANDLE, buffer: Buffer , hierarchy: TPM_HANDLE,
                     continuation: (SequenceCompleteResponse) => void)
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.SequenceComplete, [sequenceHandle]);
        curPos = toTpm2B(buffer, cmdBuf, curPos);
        curPos = hierarchy.toTpm(cmdBuf, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.SequenceComplete, respBuf);
            let res: tt.SequenceCompleteResponse = null;
            if (this.getLastResponseCode() == TPM_RC.SUCCESS)
                res = createFromTpm(tt.SequenceCompleteResponse, respBuf, pos)[0];
            setImmediate(continuation, res);
        });
    }

    /**
     * This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
     * 
     * @param parentHandle the handle of the new parent for the object Auth Index: 1 Auth Role: USER 
     * @param encryptionKey the optional symmetric encryption key used as the inner wrapper for duplicate If symmetricAlg is TPM_ALG_NULL, then this parameter shall be the Empty Buffer. 
     * @param objectPublic the public area of the object to be imported This is provided so that the integrity value for duplicate and the object attributes can be checked. NOTE Even if the integrity value of the object is not checked on input, the object Name is required to create the integrity value for the imported object. 
     * @param duplicate the symmetrically encrypted duplicate object that may contain an inner symmetric wrapper 
     * @param inSymSeed the seed for the symmetric key and HMAC key inSymSeed is encrypted/encoded using the algorithms of newParent. 
     * @param symmetricAlg definition for the symmetric algorithm to use for the inner wrapper If this algorithm is TPM_ALG_NULL, no inner wrapper is present and encryptionKey shall be the Empty Buffer. 
     * @return the sensitive area encrypted with the symmetric key of parentHandle
     */
    Import(parentHandle: TPM_HANDLE, encryptionKey: Buffer, objectPublic: TPMT_PUBLIC, duplicate: TPM2B_PRIVATE, inSymSeed: Buffer, symmetricAlg: tt.TPMT_SYM_DEF_OBJECT,
           continuation: (TPM2B_PRIVATE) => void)
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.Import, [parentHandle]);
        curPos = toTpm2B(encryptionKey, cmdBuf, curPos);
        curPos = objectPublic.toTpm2B(cmdBuf, curPos);
        curPos = duplicate.toTpm(cmdBuf, curPos);
        curPos = toTpm2B(inSymSeed, cmdBuf, curPos);
        curPos = symmetricAlg.toTpm(cmdBuf, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.Import, respBuf);
            let res: TPM2B_PRIVATE = createFromTpm(TPM2B_PRIVATE, respBuf, pos)[0];
            if (res.buffer.length + 2 != paramSize)
                throw(new Error('Import(): Invalid param size in response buffer: ' + paramSize + ' vs. actual ' + (res.buffer.length + 2)));
            setImmediate(continuation, res);
        });
    }

    /**
     * This command is used to load objects into the TPM. This command is used when both a TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be loaded, the TPM2_LoadExternal command is used.
     * 
     * @param parentHandle TPM handle of parent key; shall not be a reserved handle Auth Index: 1 Auth Role: USER 
     * @param inPrivate the private portion of the object 
     * @param inPublic the public portion of the object 
     * @return handle of type TPM_HT_TRANSIENT for the loaded object
     */
    Load(parentHandle: TPM_HANDLE, inPrivate: TPM2B_PRIVATE, inPublic: TPMT_PUBLIC,
         continuation: (TPM_HANDLE) => void)
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.Load, [parentHandle]);
        curPos = inPrivate.toTpm(cmdBuf, curPos);
        curPos = inPublic.toTpm2B(cmdBuf, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.Load, respBuf);
            let res: TPM_HANDLE = createFromTpm(TPM_HANDLE, respBuf, pos)[0];
            setImmediate(continuation, res);
        });
    }

    /**
     * This command includes a secret-based authorization to a policy. The caller proves knowledge of the secret value using an authorization session using the authValue associated with authHandle. A password session, an HMAC session, or a policy session containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
     * 
     * @param authHandle handle for an entity providing the authorization Auth Index: 1 Auth Role: USER 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param nonceTPM the policy nonce for the session This can be the Empty Buffer. 
     * @param cpHashA digest of the command parameters to which this authorization is limited This not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer. 
     * @param policyRef a reference to a policy relating to the authorization may be the Empty Buffer Size is limited to be no larger than the nonce size supported on the TPM. 
     * @param expiration time when authorization will expire, measured in seconds from the time that nonceTPM was generated If expiration is non-negative, a NULL Ticket is returned. See 23.2.5. 
     * @return TPM2_PolicySecret_RESPONSE{(ul)(li)(code)timeout(/code) - implementation-specific time value used to indicate to the TPM when the ticket expires(/li)(li)(code)policyTicket(/code) - produced if the command succeeds and expiration in the command was non-zero ( See 23.2.5). This ticket will use the TPMT_ST_AUTH_SECRET structure tag(/li)(/ul)}
     */
    PolicySecret(authHandle: TPM_HANDLE, policySession: TPM_HANDLE, nonceTPM: Buffer, cpHashA: Buffer, policyRef: Buffer, expiration: number,
                 continuation: (PolicySecretResponse) => void)
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.PolicySecret, [authHandle, policySession]);
        curPos = toTpm2B(nonceTPM, cmdBuf, curPos);
        curPos = toTpm2B(cpHashA, cmdBuf, curPos);
        curPos = toTpm2B(policyRef, cmdBuf, curPos);
        curPos = toTpm(expiration, cmdBuf, 4, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.PolicySecret, respBuf);
            let res: tt.PolicySecretResponse = null;
            if (this.getLastResponseCode() == TPM_RC.SUCCESS)
                res = createFromTpm(tt.PolicySecretResponse, respBuf, pos)[0];
            setImmediate(continuation, res);
        });
    }

    /**
     * This command is used to read the public area and Name of an NV Index. The public area of an Index is not privacy-sensitive and no authorization is required to read this data.
     * 
     * @param nvIndex the NV Index Auth Index: None 
     * @return TPM2_NV_ReadPublic_RESPONSE{(ul)(li)(code)nvPublic(/code) - the public area of the NV Index(/li)(li)(code)nvName(/code) - the Name of the nvIndex(/li)(/ul)}
     */
    ReadPublic(h: TPM_HANDLE,
               continuation: (ReadPublicResponse) => void): void
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.ReadPublic, [h]);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.ReadPublic, respBuf);
            let res: tt.ReadPublicResponse = null;
            if (this.getLastResponseCode() == TPM_RC.SUCCESS)
                res = createFromTpm(tt.ReadPublicResponse, respBuf, pos)[0];
            setImmediate(continuation, res);
        });
    }
    
    /**
     * This command is used to start an authorization session using alternative methods of establishing the session key (sessionKey). The session key is then used to derive values used for authorization and for encrypting parameters.
     * 
     * @param tpmKey handle of a loaded decrypt key used to encrypt salt may be TPM_RH_NULL Auth Index: None 
     * @param bind entity providing the authValue may be TPM_RH_NULL Auth Index: None 
     * @param nonceCaller initial nonceCaller, sets nonceTPM size for the session shall be at least 16 octets 
     * @param encryptedSalt value encrypted according to the type of tpmKey If tpmKey is TPM_RH_NULL, this shall be the Empty Buffer. 
     * @param sessionType indicates the type of the session; simple HMAC or policy (including a trial policy) 
     * @param symmetric the algorithm and key size for parameter encryption may select TPM_ALG_NULL 
     * @param authHash hash algorithm to use for the session Shall be a hash algorithm supported by the TPM and not TPM_ALG_NULL 
     * @return TPM2_StartAuthSession_RESPONSE{(ul)(li)(code)handle(/code) - handle for the newly created session(/li)(li)(code)nonceTPM(/code) - the initial nonce from the TPM, used in the computation of the sessionKey(/li)(/ul)}
     */
    StartAuthSession(tpmKey: TPM_HANDLE, bind: TPM_HANDLE, nonceCaller: Buffer, encryptedSalt: Buffer,
                     sessionType: tt.TPM_SE, symmetric: tt.TPMT_SYM_DEF, authHash: TPM_ALG_ID,
                     continuation: (StartAuthSessionResponse) => void): void
    {
        let [cmdBuf, curPos] = super.prepareCmdBuf(TPM_CC.StartAuthSession, [tpmKey, bind]);
        curPos = toTpm2B(nonceCaller, cmdBuf, curPos);
        curPos = toTpm2B(encryptedSalt, cmdBuf, curPos);
        curPos = toTpm(sessionType, cmdBuf, 1, curPos);
        curPos = symmetric.toTpm(cmdBuf, curPos);
        curPos = toTpm(authHash, cmdBuf, 2, curPos);
        super.dispatchCommand(cmdBuf.slice(0, curPos), (respBuf: Buffer): void =>
        {
            let [paramSize, pos] = super.processResponse(TPM_CC.StartAuthSession, respBuf);
            let res: tt.StartAuthSessionResponse = null;
            if (this.getLastResponseCode() == TPM_RC.SUCCESS)
                res = createFromTpm(tt.StartAuthSessionResponse, respBuf, pos)[0];
            setImmediate(continuation, res);
        });
    }
}; // class Tpm
