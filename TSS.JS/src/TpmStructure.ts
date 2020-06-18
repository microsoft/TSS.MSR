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

    toBytes(): Buffer
    {
        let buf = new TpmBuffer();
        this.toTpm(buf);
        return buf.trim();
    }

    initFromBytes(buffer: Buffer | Uint8Array | ArrayBuffer | any[]): void
    {
        this.initFromTpm(new TpmBuffer(buffer));
    }

    asTpm2B(): Buffer
    {
        let buf = new TpmBuffer();
        buf.writeSizedObj(this);
        return buf.trim();
    }

/*
    public static createFrom<T extends TpmMarshaller>(type: {new(): T}, buffer: TpmBuffer | Buffer | Uint8Array | ArrayBuffer | any[]): T
    {
        let buf: TpmBuffer = buffer instanceof TpmBuffer ? buffer: new TpmBuffer(buffer);
        return buf.createObj(type);
    }
*/
}; // class TpmStructure


/** Parameters of the field, to which session based encryption can be applied (i.e.
 * the first non-handle field marshaled in size-prefixed form) */
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


export class CmdStructure extends TpmStructure
{
    numHandles(): number { return 0; }

    /** If session based encryption can be applied to this object (i.e. its first 
     *  non-handle field is marshaled in size-prefixed form), returns non-zero parameters of
     *  the encryptable command/response parameter. Otherwise returns zero initialized struct.
     */
    sessEncInfo(): SessEncInfo { return new SessEncInfo(); }
};


import { TPM_HANDLE } from "./TpmTypes.js";


export class ReqStructure extends CmdStructure
{
    getHandles(): TPM_HANDLE[] { return null; }

    numAuthHandles(): number { return 0; }

    /** ISerializable method */
    TypeName (): string { return "ReqStructure"; }
};

export class RespStructure extends CmdStructure
{
    getHandle(): TPM_HANDLE { return null; }

    setHandle(h: TPM_HANDLE): void {}

    /** ISerializable method */
    TypeName (): string { return "RespStructure"; }
};

