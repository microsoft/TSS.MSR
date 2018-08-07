/**
 * This file contains a sample demonstrating TPM related parts of the device side of the protocol used by the Azure IoT DRS
 */


//import * as tss from "tss.js";
//import {Tpm, TPM_HANDLE, TPM_ALG_ID, TPM_RC, TPM_HT, TPM_PT, TPMA_OBJECT, TPMT_PUBLIC, TPM2B_PRIVATE, TpmBuffer} from "tss.js";
import * as tss from "../lib/TpmTypes.js";
import {TPM_HANDLE, TPM_ALG_ID, TPM_RC, TPM_HT, TPM_PT, TPMA_OBJECT, TPMT_PUBLIC, TPM2B_PRIVATE} from "../lib/TpmTypes.js";
import {Owner, Endorsement, Session, NullPwSession, NullSymDef} from "../lib/Tss.js";
import {Tpm} from "../lib/Tpm.js";
import {TpmBuffer} from "../lib/TpmMarshaller.js";
import {TpmError} from "../lib/TpmBase.js";
import {Crypto} from "../lib/Crypt.js";

import * as crypto from 'crypto';


const TEST_MODE: boolean = false;

const Aes128SymDef = new tss.TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, 128, TPM_ALG_ID.CFB);

const EK_PersHandle: TPM_HANDLE = new TPM_HANDLE(0x81010001);
const SRK_PersHandle: TPM_HANDLE = new TPM_HANDLE(0x81000001);
const ID_KEY_PersHandle: TPM_HANDLE = new TPM_HANDLE(0x81000100);

// Template of the Endorsement Key (EK)
const EkTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
        TPMA_OBJECT.restricted | TPMA_OBJECT.decrypt | TPMA_OBJECT.fixedTPM | TPMA_OBJECT.fixedParent
		    | TPMA_OBJECT.adminWithPolicy | TPMA_OBJECT.sensitiveDataOrigin,
        new Buffer('837197674484b3f81a90cc8d46a5d724fd52d76e06520b64f2a1da1b331469aa', 'hex'),
        new tss.TPMS_RSA_PARMS(Aes128SymDef, new tss.TPMS_NULL_ASYM_SCHEME(), 2048, 0),
        new tss.TPM2B_PUBLIC_KEY_RSA());

// Template of the Storage Root Key (SRK)
const SrkTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
        TPMA_OBJECT.restricted | TPMA_OBJECT.decrypt | TPMA_OBJECT.fixedTPM | TPMA_OBJECT.fixedParent
		    | TPMA_OBJECT.noDA | TPMA_OBJECT.userWithAuth | TPMA_OBJECT.sensitiveDataOrigin,
        null,
        new tss.TPMS_RSA_PARMS(Aes128SymDef, new tss.TPMS_NULL_ASYM_SCHEME(), 2048, 0),
        new tss.TPM2B_PUBLIC_KEY_RSA());


/**
 * Data structure with the information about persistent keys used in the protocol (EK and SRK).
 */
class PersKeyInfo {
    public constructor(
        /**
         * Human readable name for debugging/logging purposes only
         */
        public name: string,

        /**
         * The TPM hierarachy, to which the key belongs
         * @note All the keys are persisted in the owner hierarchy disregarding their mother one
         */
        public hierarchy: TPM_HANDLE,

        /**
         * Handle value where the persistent key is expected to be found
         */
        public handle: TPM_HANDLE,

        /**
         * Template to be used for key cretaion if the persistent key with the given handle does not exist
         */
        public template: TPMT_PUBLIC,

        /**
         * Public part of the persistent key. Identical to its template in all regards, save that it
         * additionally has its 'unique' field (i.e. actual public key bits) initialized.
         */
        public pub: TPMT_PUBLIC = null
    ) {}
};

const PersKeys: PersKeyInfo[] = [new PersKeyInfo('EK', Endorsement, EK_PersHandle, EkTemplate),
                                 new PersKeyInfo('SRK', Owner, SRK_PersHandle, SrkTemplate)];
const EK: number = 0;
const SRK: number = 1;

let useTpmSim: boolean = false;
let cleanOnly: boolean = false;


console.log('DrsClientNode started on ' + (process.platform == 'win32' ? 'Windows' : process.platform) + '!!');


processCmdLine();

let tpm = new Tpm(useTpmSim);
tpm.connect(drsClientSampleMain);

function processCmdLine()
{
    for (let i = 2; i < process.argv.length; ++i)
    {
        let opt: string = process.argv[i];
        if (opt[0] == '-' || opt[0] == '/')
            opt = opt.substr(opt.length > 1 && opt[1] == '-' ? 2 : 1);

        if (opt == 'sim' || opt == 's')
            useTpmSim = true;
        else if (opt == 'clear' || opt == 'c')
            cleanOnly = true;
    }
}

function drsClientSampleMain(): void
{
    if (cleanOnly)
    {
        console.log('Removing EK & SRK');
        clearPersistentPrimary(PersKeys);
    }
    else if (TEST_MODE) {
        // Issue a simple bi-directional command to make sure that TPM communication channel is functional
        tpm.GetRandom(0x20, (err: TpmError, response: Buffer) => {
        console.log('GetRandom() returned ' + response.length + ' bytes: ' + new Uint8Array(response));

        // Clean debris possibly left from the previous run
        let numDandlingHandles: number = 0;
        tpm.FlushContext(new TPM_HANDLE(TPM_HT.HMAC_SESSION << 24), (err: TpmError) => {
        numDandlingHandles += err ? 0 : 1;
        tpm.FlushContext(new TPM_HANDLE(TPM_HT.POLICY_SESSION << 24), (err: TpmError) => {
        numDandlingHandles += err ? 0 : 1;
        tpm.FlushContext(new TPM_HANDLE(TPM_HT.TRANSIENT << 24), (err: TpmError) => {
        numDandlingHandles += err ? 0 : 1;
        console.log('Cleanup pahse: ' + numDandlingHandles + ' dandling handles discovered');

        // Kick off the sample's main logic
        createPersistentPrimary(PersKeys); }) }) }) });
    }
    else {
        // Kick off the sample's main logic
        createPersistentPrimary(PersKeys);
    }
}

function clearPersistentPrimary(persKeys: PersKeyInfo[]): void
{
    let pki = persKeys[0];
    tpm.ReadPublic(pki.handle, (err: TpmError, resp?: tss.ReadPublicResponse) => {
    console.log('ReadPublic(' + pki.name + ') returned ' + TPM_RC[tpm.lastResponseCode]);
    if (!err) {
        // Delete the existing persistent key
        tpm.EvictControl(Owner, pki.handle, pki.handle, () => {
        console.log('EvictControl(' + pki.name + ') returned ' + TPM_RC[tpm.lastResponseCode]);

        // Recurse to delete the next key in the list
        if (persKeys.length > 1)
            clearPersistentPrimary(persKeys.slice(1, persKeys.length));
        });
    }
    else
    {
        // Recurse to delete the next key in the list
        if (persKeys.length > 1)
            clearPersistentPrimary(persKeys.slice(1, persKeys.length));
        else
            console.log('All persistent keys cleared');
    } });
} // clearPersistentPrimary()


/**
 * Makes sure that the device persistent TPM keys the protocol relies on (EK and SRK) are in place.
 *
 * @note On a real device EK should be pre-installed, and its public part made available to the
 *       DRS service before the device gets to the end user.
 */
function createPersistentPrimary(persKeys: PersKeyInfo[]): void
{
    let pki = persKeys[0];
    tpm.ReadPublic(pki.handle, (err: TpmError, resp?: tss.ReadPublicResponse) => {
    console.log('ReadPublic(' + pki.name + ') returned ' + TPM_RC[tpm.lastResponseCode]);
    if (err) {
        tpm.CreatePrimary(pki.hierarchy, new tss.TPMS_SENSITIVE_CREATE(), pki.template, null, null,
                          (err: TpmError, resp: tss.CreatePrimaryResponse) => {
        if (failed(err))
            return;
        pki.pub = resp.outPublic;
        tpm.EvictControl(Owner, resp.handle, pki.handle, (err) => {
        if (failed(err))
            return;
        console.log('EvictControl() for ' + pki.name + ' succeeded');
        tpm.FlushContext(resp.handle, () => {
        console.log('FlushContext(0x' + resp.handle.handle.toString(16) + ') returned ' + TPM_RC[tpm.lastResponseCode]);
        createPersistentPrimary_Cont(persKeys);
        }) }) });
    }
    else if (TEST_MODE) {
        // Delete the existing persistent key in order to test key creation commands
        tpm.EvictControl(Owner, pki.handle, pki.handle, () => {
        console.log('EvictControl() for' + pki.name + ' returned ' + TPM_RC[tpm.lastResponseCode]);

        // Recurse to create the key anew
        createPersistentPrimary(persKeys); });
    }
    else
    {
        pki.pub = resp.outPublic;
        createPersistentPrimary_Cont(persKeys);
    } });
} // createPersistentPrimary()

function createPersistentPrimary_Cont(persKeys: PersKeyInfo[]): void
{
    if (persKeys.length > 1)
    {
        // Create the next persistent key
        createPersistentPrimary(persKeys.slice(1, persKeys.length));
        return;
    }

    // Proceed to the new ID Key activation logic
    //if (process.platform == 'win32')
        beginActivation();
}


/**
 * Represents the structure of the activation blob generated by the DRS service in response to device provisioning request:
 * Note that 'credBlob' and 'idKeyPub' members are marshaled by the DRS sized data structures, i.e. prepended with a 2-byte
 * size field (see the 'fromTpm()' method).
 */
class DrsActivationBlob
{
    public credBlob: tss.TPMS_ID_OBJECT;
    public encSecret: tss.TPM2B_ENCRYPTED_SECRET;
    public idKeyDupBlob: tss.TPM2B_PRIVATE;
    public encWrapKey: tss.TPM2B_ENCRYPTED_SECRET;
    public idKeyPub: TPMT_PUBLIC;
    public encUriData: tss.TPM2B_DATA;

    constructor (actBlob: TpmBuffer | Buffer = null)
    {
        if (actBlob != null)
            this.fromTpm(actBlob);
    }

    fromTpm (actBlob: TpmBuffer | Buffer)
    {
        let buf: TpmBuffer = actBlob instanceof Buffer ? new TpmBuffer(actBlob) : actBlob;

        this.credBlob = buf.sizedFromTpm(tss.TPMS_ID_OBJECT, 2);
	    //console.log("credBlob end: " + actBlob.getCurPos());
        this.encSecret = buf.createFromTpm(tss.TPM2B_ENCRYPTED_SECRET);
	    //console.log("encSecret end: " + actBlob.getCurPos() + "; size = " + this.encSecret.secret.length);
        this.idKeyDupBlob = buf.createFromTpm(tss.TPM2B_PRIVATE);
	    //console.log("idKeyDupBlob end: " + actBlob.getCurPos() + "; size = " + this.idKeyDupBlob.buffer.length);
        this.encWrapKey = buf.createFromTpm(tss.TPM2B_ENCRYPTED_SECRET);
	    //console.log("encWrapKey end: " + actBlob.getCurPos() + "; size = " + this.encWrapKey.secret.length);
        this.idKeyPub = buf.sizedFromTpm(TPMT_PUBLIC, 2);
	    //console.log("idKeyPub end: " + actBlob.getCurPos());
        this.encUriData = buf.createFromTpm(tss.TPM2B_DATA);
	    //console.log("encUriData end: " + actBlob.getCurPos());
        if (!buf.isOk())
            throw new Error("Failed to unmarshal Activation Blob");
        if (buf.curPos != buf.length)
            console.log("WARNING: Activation Blob sent by DRS has contains extra unidentified data");
    }
} // class DrsActivationBlob


//let policySess: Session = null;


/**
 * Performs the main phase of the device registration protocol:
 * - sends EK and SRK data to the DRS;
 * - parses returned activation data;
 * - imports and persists Device ID Key generated by the DRS.
 *
 * Also demonstrates how to decrypt secret URI data sent by the DRS.
 */

 //import * as drs from "./DrsServerEmulator.js";
 let drs = null;

function beginActivation(): void
{
    // Note that here we need complete public parts containg the actual public key bits, not just the corresponding templates.
    // Complete public parts are returned by ReadPublic() or CretePrimary() commands in the above code.
    let ekPub: Buffer = PersKeys[EK].pub.asTpm2B();
    let srkPub: Buffer = PersKeys[SRK].pub.asTpm2B();

    // Perform the DRS request.
    // Note that this code uses a call out to an emulator. The actual SDK code will have to use
    // one of the web protocols supported by the DRS to exchange these data.
    let rawActBlob = drsGetActivationBlob(tpm, ekPub, srkPub, doActivation);
}

function doActivation(rawActBlob: Buffer): void
{
	console.log("Raw Activation Blob size: " + rawActBlob.length);

    // Unmarshal components of the activation blob received from the DRS
    let actBlob = new DrsActivationBlob(rawActBlob);

	// Start a policy session to be used with ActivateCredential()
    let nonceCaller = crypto.randomBytes(20);
    tpm.StartAuthSession(null, null, nonceCaller, null, tss.TPM_SE.POLICY, NullSymDef, TPM_ALG_ID.SHA256,
                         (err: TpmError, resp: tss.StartAuthSessionResponse) => {
    console.log('StartAuthSession(POLICY_SESS) returned ' + TPM_RC[tpm.lastResponseCode] + '; sess handle: ' + resp.handle.handle.toString(16));
    let policySess = new Session(resp.handle, resp.nonceTPM);
    
	// Apply the policy necessary to authorize an EK on Windows
    tpm.PolicySecret(Endorsement, policySess.SessIn.sessionHandle, null, null, null, 0,
                     (err: TpmError, resp: tss.PolicySecretResponse) => {
    console.log('PolicySecret() returned ' + TPM_RC[tpm.lastResponseCode]);

	// Use ActivateCredential() to decrypt symmetric key that is used as an inner protector
	// of the duplication blob of the new Device ID key generated by DRS.
    tpm.withSessions(null, policySess)
       .ActivateCredential(SRK_PersHandle, EK_PersHandle, actBlob.credBlob, actBlob.encSecret.secret, 
                           (err: TpmError, innerWrapKey: Buffer) => {
    console.log('ActivateCredential() returned ' + TPM_RC[tpm.lastResponseCode] + '; innerWrapKey size ' + innerWrapKey.length);

	// Initialize parameters of the symmetric key used by DRS 
	// Note that the client uses the key size chosen by DRS, but other parameters are fixes (an AES key in CFB mode).
	let symDef = new tss.TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, innerWrapKey.length * 8, TPM_ALG_ID.CFB);
		    
	// Import the new Device ID key issued by DRS to the device's TPM
	tpm.Import(SRK_PersHandle, innerWrapKey, actBlob.idKeyPub, actBlob.idKeyDupBlob, actBlob.encWrapKey.secret, symDef,
               (err: TpmError, idKeyPriv: TPM2B_PRIVATE) => {
    console.log('Import() returned ' + TPM_RC[tpm.lastResponseCode] + '; idKeyPriv size ' + idKeyPriv.buffer.length);

    // Load the imported key into the TPM
	tpm.Load(SRK_PersHandle, idKeyPriv, actBlob.idKeyPub,
               (err: TpmError, hIdKey: TPM_HANDLE) => {
    console.log('Load() returned ' + TPM_RC[tpm.lastResponseCode] + '; ID key handle: 0x' + hIdKey.handle.toString(16));

    // Remove possibly existing persistent instance of the previous Device ID key
    tpm.EvictControl(Owner, ID_KEY_PersHandle, ID_KEY_PersHandle, () => {

    // Persist the new Device ID key
    tpm.EvictControl(Owner, hIdKey, ID_KEY_PersHandle,
                     () => {
    console.log('EvictControl(0x' + hIdKey.handle.toString(16) + ', 0x' + ID_KEY_PersHandle.handle.toString(16) +
                ') returned ' + TPM_RC[tpm.lastResponseCode]);

    // Free the ID Key transient handle
    tpm.FlushContext(hIdKey, () => {
    console.log('FlushContext(TRANS_ID_KEY) returned ' + TPM_RC[tpm.lastResponseCode]);

    // Free the session object
    tpm.FlushContext(policySess.SessIn.sessionHandle,
                     () => {
    console.log('FlushContext(POLICY_SESS) returned ' + TPM_RC[tpm.lastResponseCode]);


    //
    // Decrypt the secret URI data sent by the DRS
    //
    let symAlg = 'AES-' + (innerWrapKey.length * 8).toString(10) + '-CFB';
    let iv = new Buffer(16);
    iv.fill(0);

    let dec: crypto.Decipher = crypto.createDecipheriv(symAlg, innerWrapKey, iv);
    let decUriData: Buffer = dec.update(actBlob.encUriData.buffer);
    console.log('Decipher.update returned ' + decUriData.length + ' bytes: ' + decUriData);
    let decFinal: Buffer = dec.final();
    console.log('Decipher.final returned ' + decFinal.length + ' bytes: ' + decFinal);


	//
	// Example of signing a device token using the new Device ID key
    //
	let idKeyHashAlg: TPM_ALG_ID = (<tss.TPMS_SCHEME_HMAC>(<tss.TPMS_KEYEDHASH_PARMS>actBlob.idKeyPub.parameters).scheme).hashAlg;
    SignDeviceToken(idKeyHashAlg);

    }) }) }) }) }) }) }) }) });
} // doActivation()

/**
 * Example of signing a device token using the new persistent Device ID key
 */
function SignDeviceToken(idKeyHashAlg: TPM_ALG_ID): void
{
    //TpmHelpers.getTpmProperty(tpm, TPM_PT.INPUT_BUFFER);
    //let MaxInputBuffer: number = 1024;
    tpm.GetCapability(tss.TPM_CAP.TPM_PROPERTIES, TPM_PT.INPUT_BUFFER, 1,
                      (err: TpmError, caps: tss.GetCapabilityResponse) => {
	let props = <tss.TPML_TAGGED_TPM_PROPERTY>caps.capabilityData;
	if (props.tpmProperty.length != 1 || props.tpmProperty[0].property != TPM_PT.INPUT_BUFFER)
	    throw new Error("Unexpected result of TPM2_GetCapability(TPM_PT.INPUT_BUFFER)");
	let MaxInputBuffer: number = props.tpmProperty[0].value;


    // First, the code for a short token (<= MaxInputBuffer) signing

	// For testing purposes only. That this sample simply generates a random buffer in lieu of a valid device token
    let deviceIdToken: Buffer = crypto.randomBytes(800);

	if (deviceIdToken.length > MaxInputBuffer)
        throw new Error('Too long token to HMAC');

	tpm.HMAC(ID_KEY_PersHandle, deviceIdToken, idKeyHashAlg,
             (err: TpmError, signature: Buffer) => {
    console.log('HMAC() returned ' + TPM_RC[tpm.lastResponseCode] + '; signature size ' + signature.length);

    if (TEST_MODE)
    {
        // Verify the signature correctness
        let sigCheckRes: boolean = drsVerifyIdSignature(tpm, deviceIdToken, signature);
        console.log('Signature over short token: ' + (sigCheckRes ? 'OK' : 'FAILED'));

        signature[16] ^= 0xCC;
        sigCheckRes = drsVerifyIdSignature(tpm, deviceIdToken, signature);
        console.log('Bad signature over short token: ' + (sigCheckRes ? 'OK' : 'FAILED'));
    }

    // Now the code for long token (> MaxInputBuffer) signing
    deviceIdToken = crypto.randomBytes(5500);

    let curPos: number = 0;
    let bytesLeft: number = deviceIdToken.length;

    let hSequence: TPM_HANDLE = null;
    let loopFn = () => {
        if (bytesLeft > MaxInputBuffer) {
            tpm.SequenceUpdate(hSequence, deviceIdToken.slice(curPos, curPos + MaxInputBuffer), loopFn);
            console.log('SequenceUpdate() returned ' + TPM_RC[tpm.lastResponseCode] +
                        ' for slice [' + curPos + ', ' + (curPos + MaxInputBuffer) + ']');
            bytesLeft -= MaxInputBuffer;
            curPos += MaxInputBuffer;
        }
        else {
            tpm.SequenceComplete(hSequence, deviceIdToken.slice(curPos, curPos + bytesLeft), new TPM_HANDLE(tss.TPM_RH.NULL),
                                 (err: TpmError, resp: tss.SequenceCompleteResponse) => {
            console.log('SequenceComplete() returned ' + TPM_RC[tpm.lastResponseCode]);
            console.log('signature size ' + signature.length);

            if (TEST_MODE)
            {
                let sigCheckRes: boolean = drsVerifyIdSignature(tpm, deviceIdToken, resp.result);
                console.log('Signature over long token: ' + (sigCheckRes ? 'OK' : 'FAILED'));
            }

            // END OF SAMPLE
            finish(true, 'Sample completed successfully!');
            });
        }
    };
    tpm.HMAC_Start(ID_KEY_PersHandle, new Buffer(0), idKeyHashAlg,
                   (err: TpmError, hSeq: TPM_HANDLE) => {
    console.log('HMAC_Start() returned ' + TPM_RC[tpm.lastResponseCode]);
    hSequence = hSeq;
    loopFn();
    }) }) });
} // SignDeviceToken()


//
// DRS logic emulation
//

let keyBytes = crypto.randomBytes(32);
let idKeySens = new tss.TPMS_SENSITIVE_CREATE(null, keyBytes);
let idKeyTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
			tss.TPMA_OBJECT.sign | tss.TPMA_OBJECT.userWithAuth | tss.TPMA_OBJECT.noDA,
			null,   // Will be filled by getActivationBlob
			new tss.TPMS_KEYEDHASH_PARMS(new tss.TPMS_SCHEME_HMAC(TPM_ALG_ID.SHA256)),
			new tss.TPM2B_DIGEST_Keyedhash());

export function drsVerifyIdSignature(tpm: Tpm, data: Buffer, sig: Buffer): boolean
{
    let hmacOverData = Crypto.hmac(TPM_ALG_ID.SHA256, idKeySens.data, data);
    return Buffer.compare(sig, hmacOverData) == 0;

/*
	tpm.withSession(NullPwSession)
       .CreatePrimary(owner, idKeySens, idKeyTemplate, null, [],
                          (keyCreationErr: TpmError, idKey: tss.CreatePrimaryResponse) => {

    if (keyCreationErr)
        return setImmediate(continuation, keyCreationErr);

	tpm.allowErrors()
       .VerifySignature(idKey.handle, data, sig,
                          (verificationErr: TpmError, verified: tss.TPMT_TK_VERIFIED) => {
	tpm.FlushContext(idKey.handle, (err: TpmError) => {

    setImmediate(continuation, verificationErr);
	}); }); });
*/
}
	

export function drsGetActivationBlob(tpm: Tpm, ekPubBlob: Buffer, srkPubBlob: Buffer, continuation: (actBlob: Buffer) => void): void
{
	let ekPub: TPMT_PUBLIC = new TpmBuffer(ekPubBlob).createFromTpm(tss.TPM2B_PUBLIC).publicArea;
	let srkPub: TPMT_PUBLIC = new TpmBuffer(srkPubBlob).createFromTpm(tss.TPM2B_PUBLIC).publicArea;

	// Start a policy session to be used with ActivateCredential()
    let nonceCaller = crypto.randomBytes(20);
    tpm.StartAuthSession(null, null, nonceCaller, null, tss.TPM_SE.POLICY, NullSymDef, TPM_ALG_ID.SHA256,
                         (err: TpmError, respSas: tss.StartAuthSessionResponse) => {
    let hSess = respSas.handle;
    console.log('DRS >> StartAuthSession(POLICY_SESS) returned ' + TPM_RC[tpm.lastResponseCode] + '; sess handle: ' + hSess.handle.toString(16));
    let sess = new Session(hSess, respSas.nonceTPM);

	// Run the policy command necessary for key duplication
    tpm.PolicyCommandCode(hSess, tss.TPM_CC.Duplicate,
                          (err: TpmError, respPcc: tss.PolicyCommandCodeResponse) => {
    console.log('DRS >> PolicyCommandCode() returned ' + TPM_RC[tpm.lastResponseCode]);

	// Retrieve the policy digest computed by the TPM
	tpm.PolicyGetDigest(hSess, (err: TpmError, dupPolicyDigest: Buffer) => {
    console.log('DRS >> PolicyGetDigest() returned ' + TPM_RC[tpm.lastResponseCode]);

    idKeyTemplate.authPolicy = dupPolicyDigest;

    tpm.withSession(NullPwSession)
       .CreatePrimary(Owner, idKeySens, idKeyTemplate, null, [],
                      (err: TpmError, idKey: tss.CreatePrimaryResponse) => {
    console.log('DRS >> CreatePrimary(idKey) returned ' + TPM_RC[tpm.lastResponseCode]);
    
    tpm.LoadExternal(null, srkPub, Owner,
                     (err: TpmError, hSrkPub: tss.TPM_HANDLE) => {
    console.log('DRS >> LoadExternal(SRKpub) returned ' + TPM_RC[tpm.lastResponseCode]);

	let symWrapperDef = new tss.TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, 128, TPM_ALG_ID.CFB);
	tpm.withSession(sess)
       .Duplicate(idKey.handle, hSrkPub, null, symWrapperDef,
                      (err: TpmError, respDup: tss.DuplicateResponse) => {
    console.log('DRS >> Duplicate(...) returned ' + TPM_RC[tpm.lastResponseCode]);
    
    tpm.FlushContext(hSrkPub, (err: TpmError) => {

    tpm.LoadExternal(null, ekPub, Endorsement,
                     (err: TpmError, hEkPub: tss.TPM_HANDLE) => {
    console.log('DRS >> LoadExternal(EKpub) returned ' + TPM_RC[tpm.lastResponseCode]);

    tpm.MakeCredential(hEkPub, respDup.encryptionKeyOut, srkPub.getName(),
                       (err: TpmError, cred: tss.MakeCredentialResponse) => {
    console.log('DRS >> MakeCredential(...) returned ' + TPM_RC[tpm.lastResponseCode]);

	// Delete the key and session handles
    tpm.FlushContext(hEkPub, (err: TpmError) => {
	tpm.FlushContext(idKey.handle, (err: TpmError) => {
	tpm.FlushContext(hSess, (err: TpmError) => {
    console.log('DRS >> Cleanup done');

    //
	// Encrypt URI data to be passed to the client device
	//
    let symWrapperTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
			    tss.TPMA_OBJECT.decrypt | tss.TPMA_OBJECT.encrypt | tss.TPMA_OBJECT.userWithAuth,
			    null,
			    new tss.TPMS_SYMCIPHER_PARMS(symWrapperDef),
			    new tss.TPM2B_DIGEST());
	let sens = new tss.TPMS_SENSITIVE_CREATE(null, respDup.encryptionKeyOut);
	tpm.withSession(NullPwSession)
       .CreatePrimary(Owner, sens, symWrapperTemplate, null, [],
                      (err: TpmError, symWrapperKey: tss.CreatePrimaryResponse) => {
    console.log('DRS >> CreatePrimary(SymWrapperKey) returned ' + TPM_RC[tpm.lastResponseCode]);

    let uriData = Buffer.from("http://my.test.url/TestDeviceID=F4ED90771DAA7C0B3230FF675DF8A61104AE7C8BB0093FD6A", 'utf8');
	let iv = Buffer.alloc(respDup.encryptionKeyOut.length, 0);
	tpm.withSession(NullPwSession)
       .EncryptDecrypt(symWrapperKey.handle, 0, TPM_ALG_ID.CFB, iv, uriData,
                       (err: TpmError, respEnc: tss.EncryptDecryptResponse) => {
    console.log('DRS >> EncryptDecrypt() returned ' + TPM_RC[tpm.lastResponseCode]);
    let encryptedUri = respEnc.outData;

	// Delete the key and session handles
	tpm.FlushContext(symWrapperKey.handle, (err: TpmError) => {
    console.log('DRS >> Final cleanup done');

    //
    // Prepare data to send back to the DRS client
    //
    let actBlob = new TpmBuffer(4096);
		
    actBlob.sizedToTpm(cred.credentialBlob, 2);
    actBlob.toTpm2B(cred.secret);
    respDup.duplicate.toTpm(actBlob);
    actBlob.toTpm2B(respDup.outSymSeed);
    actBlob.sizedToTpm(idKey.outPublic, 2);
    actBlob.toTpm2B(encryptedUri);
    console.log('DRS >> Activation blob of ' + actBlob.curPos + ' bytes generated');
		
    setImmediate(continuation, actBlob.trim().buffer);

    }); }); }); }); }); }); }); }); }); }); }); }); }); }); });
} // drsGetActivationBlob()


function failed(err: TpmError, msg: string = null): boolean {
    if (err) {
        finish(false, msg ? msg : err.tpmCommand + " FAILED with error " + TPM_RC[err.responseCode] + ": " + err.message);
        return true;
    }
    return false;
}

function finish(ok: boolean, msg: string) {
    console.log(msg);
    console.log('Node.JS demo ' + (ok ? 'successfully finished!' : 'terminated because of a TPM error'));
    tpm.close();
}

