/* 
 * The copyright in this software is being made available under the MIT License,
 *  included below. This software may be subject to other third party and
 *  contributor rights, including patent rights, and no such rights are granted
 *  under this license.
 *
 *  Copyright (c) Microsoft Corporation
 *
 *  All rights reserved.
 *
 *  The MIT License (MIT)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

import { TPM_RC, TPM_ALG_ID, TPM_HANDLE, TPM_RH, TPMA_SESSION, TPMT_SYM_DEF, TPMS_AUTH_COMMAND, TPMS_AUTH_RESPONSE } from "./TpmTypes";


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
        nonceTpm: Buffer = null,
        sessionAttributes: number = TPMA_SESSION.continueSession,
        nonceCaller: Buffer = null
    ) {
        this.SessIn = new TPMS_AUTH_COMMAND(sessionHandle, nonceCaller, sessionAttributes);
        this.SessOut = new TPMS_AUTH_RESPONSE(nonceTpm, sessionAttributes);
    }

    public static Pw(authValue: Buffer): Session
    {
        let s = new Session();
        s.SessIn.sessionHandle = new TPM_HANDLE(TPM_RH.RS_PW);
        s.SessIn.nonce = null;
        s.SessIn.sessionAttributes = TPMA_SESSION.continueSession;
        s.SessIn.hmac = authValue;
        s.SessOut.sessionAttributes = TPMA_SESSION.continueSession;
        return s;
    }
};

export const NullPwSession = Session.Pw(new Buffer(0));

