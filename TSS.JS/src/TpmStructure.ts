/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */


import { TpmMarshaller, TpmBuffer } from "./TpmMarshaller.js";


export class TpmStructure implements TpmMarshaller
{
    /** TpmMarshaller method */
	toTpm(buf: TpmBuffer) : void {}

    /** TpmMarshaller method */
    initFromTpm(buf: TpmBuffer) : void {}

    /** @return TPM binary representation of this object. */
    toBytes(): Buffer
    {
        let buf = new TpmBuffer();
        this.toTpm(buf);
        return buf.trim();
    }

    /** Initializes this object from a TPM binary representation in the given byte buffer */
    initFromBytes(buffer: Buffer | Uint8Array | ArrayBuffer | any[]): void
    {
        this.initFromTpm(new TpmBuffer(buffer));
    }

    /** @return 2B size-prefixed TPM binary representation of this object. */
    asTpm2B(): Buffer
    {
        let buf = new TpmBuffer();
        buf.writeSizedObj(this);
        return buf.trim();
    }

    /** ISerializable method */
    typeName (): string { return "TpmStructure"; }
}; // class TpmStructure


/** Parameters of the TPM command request data structure field, to which session based
 *  encryption can be applied (i.e. the first non-handle field marshaled in size-prefixed
 *  form, if any) */
export class SessEncInfo
{
    constructor (
        /** Length of the size prefix in bytes. The size prefix contains the number of
         *  elements in the sized fieled (normally just bytes).
         */
        public sizeLen : number = 0,

        /** Length of an element of the sized area in bytes (in most cases 1) */
        public valLen : number = 0
    ) {}
};


/** Base class for custom (not TPM 2.0 spec defined) auto-generated classes
 *  representing a TPM command or response parameters and handles, if any.
 *  
 *  These data structures differ from the spec-defined ones derived directly
 *  from the TpmStructure class in that their handle fields are not marshaled
 *  by their toTpm() and initFrom() methods, but rather are acceesed and
 *  manipulated via an interface defined by this structs and its derivatives
 *  ReqStructure and RespStructure. */
export class CmdStructure extends TpmStructure
{
    /** @return Number of TPM handles contained (as fields) in this data structure */
    numHandles(): number { return 0; }

    /** @return Non-zero size info of the encryptable command/response parameter if 
     *          session based encryption can be applied to this object (i.e. its first
     *          non-handle field is marshaled in size-prefixed form). Otherwise returns
     *          zero initialized struct. */
    sessEncInfo(): SessEncInfo { return new SessEncInfo(); }
};


import { TPM_HANDLE } from "./TpmTypes.js";


/** Base class for custom (not TPM 2.0 spec defined) auto-generated data structures
 *  representing a TPM command parameters and handles, if any. */
export class ReqStructure extends CmdStructure
{
    /** @return An array of TPM handles contained in this TPM request data structure */
    getHandles(): TPM_HANDLE[] { return null; }

    /** @return Number of authorization TPM handles contained in this data structure */
    numAuthHandles(): number { return 0; }

    /** ISerializable method */
    typeName (): string { return "ReqStructure"; }
};


/** Base class for custom (not TPM 2.0 spec defined) auto-generated data structures
 *  representing a TPM response parameters and handles, if any. */
export class RespStructure extends CmdStructure
{
    /** @return The TPM handle contained in this TPM response data structure */
    getHandle(): TPM_HANDLE { return null; }

    /** Sets this structure's handle field (TPM_HANDLE) if it is present */
    setHandle(h: TPM_HANDLE): void {}

    /** ISerializable method */
    typeName (): string { return "RespStructure"; }
};

