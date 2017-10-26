import { TPM_ALG_ID, TPM_HANDLE, TPM_RH, TPMA_SESSION, TPMT_SYM_DEF, TPMS_AUTH_COMMAND, TPMS_AUTH_RESPONSE } from "./TpmTypes.js";


export const Owner = new TPM_HANDLE(TPM_RH.OWNER);
export const Endorsement = new TPM_HANDLE(TPM_RH.ENDORSEMENT);

export const NullSymDef = new TPMT_SYM_DEF(TPM_ALG_ID.NULL, 0, TPM_ALG_ID.NULL);
//export const NullSymDefObj = new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL, 0, TPM_ALG_ID.NULL);

export class Session
{
    public SessIn: TPMS_AUTH_COMMAND = new TPMS_AUTH_COMMAND();
    public SessOut: TPMS_AUTH_RESPONSE = new TPMS_AUTH_RESPONSE();

    constructor(
        sessionHandle: TPM_HANDLE = new TPM_HANDLE(0),
        nonceTpm: Buffer = new Buffer(0),
        sessionAttributes: number = TPMA_SESSION.continueSession,
        nonceCaller: Buffer = new Buffer(0)
    ) {
        this.SessIn = new TPMS_AUTH_COMMAND(sessionHandle, nonceCaller, sessionAttributes);
        this.SessOut = new TPMS_AUTH_RESPONSE(nonceTpm, sessionAttributes);
    }

    public static Pw(authValue: Buffer): Session
    {
        let s = new Session();
        s.SessIn.sessionHandle = new TPM_HANDLE(TPM_RH.RS_PW);
        s.SessIn.nonce = new Buffer(0);
        s.SessIn.sessionAttributes = TPMA_SESSION.continueSession;
        s.SessIn.hmac = authValue;
        s.SessOut.sessionAttributes = TPMA_SESSION.continueSession;
        return s;
    }
};

export const NullPwSession = Session.Pw(new Buffer(0));
