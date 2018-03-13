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

import { TPM_ALG_ID } from "./TpmTypes";
import * as crypto from 'crypto';


export class Crypto
{
    public static digestSize(alg: TPM_ALG_ID) : number
    {
        switch (alg)
        {
        case TPM_ALG_ID.SHA1: return 20;
        case TPM_ALG_ID.SHA256: return 32;
        case TPM_ALG_ID.SHA384: return 48;
        case TPM_ALG_ID.SHA512: return 64;
        }
        return 0;
    }

    public static tpmAlgToNode(alg: TPM_ALG_ID) : string
    {
        switch (alg)
        {
        case TPM_ALG_ID.SHA1: return 'sha1';
        case TPM_ALG_ID.SHA256: return 'sha256';
        case TPM_ALG_ID.SHA384: return 'sha384';
        case TPM_ALG_ID.SHA512: return 'sha512';
        }
        return null;
    }

    public static hash(alg: TPM_ALG_ID, data: Buffer) : Buffer
    {
        const hash = crypto.createHash(Crypto.tpmAlgToNode(alg));
        hash.update(data);
        return hash.digest();
    }

    public static hmac(alg: TPM_ALG_ID, key: Buffer, data: Buffer) : Buffer
    {
        const hash = crypto.createHmac(Crypto.tpmAlgToNode(alg), key);
        hash.update(data);
        return hash.digest();
    }
}; // class Crypto
