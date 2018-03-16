/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */


import { TPM_ALG_ID, TPMT_SYM_DEF_OBJECT, TPMT_SYM_DEF } from "./TpmTypes.js";


class SizedStructInfo
{
	constructor(
        public startPos: number,
        public size: number
    ) {}
};

export interface TpmMarshaller
{
    /**
	 *  Convert this object to its TPM representation and store in the output byte buffer object
	 *  @param buf Output byte buffer for the marshaled representation of this object
	 *  @param startPos Current write position in the output buffer
     *  @returnsNew write position in the output buffer
     */
	toTpm(buf: TpmBuffer) : void;
	
    /**
	 *  Populate this object from the TPM representation in the input byte buffer object
	 *  @param buf  An input byte buffer containg marshaled representation of the object
	 *  @param startPos  Current read position in the input buffer
     *  @returns Number of bytes unmarshaled
     */
	fromTpm(buf: TpmBuffer) : void;
} // interface TpmMarshaller


export abstract class TpmStructure implements TpmMarshaller
{
    /** TpmMarshaller method */
	abstract toTpm(buf: TpmBuffer) : void;

    /** TpmMarshaller method */
    abstract fromTpm(buf: TpmBuffer) : void;

    asTpm2B(): Buffer
    {
        let buf = new TpmBuffer(4096);
        buf.sizedToTpm(this, 2);
        return buf.slice(0, buf.curPos).buffer;
    }

    asTpm(): Buffer
    {
        let buf = new TpmBuffer(4096);
        this.toTpm(buf);
        return buf.slice(0, buf.curPos).buffer;
    }

	toTpm2B(buf: TpmBuffer) : void
    {
        return buf.toTpm2B(this.asTpm());
    }
};

export class TpmBuffer
{
    protected buf: Buffer = null;
    protected pos: number = 0;
    protected outOfBounds: boolean = false;

    private sizedStructSizes: Array<SizedStructInfo> = null;

    constructor(length: number);
    constructor(length: number[]);
    constructor(srcBuf: Buffer);
    constructor(srcBuf: TpmBuffer);
    constructor(lengthOrSrcBuf: any)
    {
        this.buf = new Buffer(lengthOrSrcBuf instanceof TpmBuffer ? lengthOrSrcBuf.buf : lengthOrSrcBuf);
        this.sizedStructSizes = new Array<SizedStructInfo>();
    }

    get buffer(): Buffer { return this.buf; }

    get length(): number { return this.buf.length; }

    get curPos(): number { return this.pos; }
    set curPos(newPos: number)
    {
        this.pos = newPos;
        this.outOfBounds = newPos <= this.buf.length;
    }

    public isOk(): boolean
    {
        return !this.outOfBounds;
    }

    public trim() : TpmBuffer
    {
        return new TpmBuffer(this.buf.slice(0, this.pos));
    }

    public slice(startPos: number, endPos: number) : TpmBuffer
    {
        return new TpmBuffer(this.buf.slice(startPos, endPos));
    }

    public copy(target: TpmBuffer, targetStart: number) : number
    {
        if (target.length < targetStart + this.length)
            return 0;
        let result = this.buf.copy(target.buf, targetStart);
        target.pos = targetStart + this.length;
        return result;
    }

    public getCurStuctRemainingSize() : number
    {
        let ssi: SizedStructInfo = this.sizedStructSizes[this.sizedStructSizes.length - 1];
        return ssi.size - (this.pos - ssi.startPos);
    }

    private checkLen(len: number): boolean
    {
        if (this.buf.length < this.pos + len)
        {
            this.outOfBounds = true;
            this.pos = this.buf.length;
            return false;
        }
        return true;
    }

    /**
     *  Converts the given numerical value of the given size to the TPM wire format.
     *  @param val  Numerical value to marshal
     *  @param len  Size of the numerical value in bytes
     */
    public toTpm(val: number, len: number) : void
    {
        if (!this.checkLen(len))
            return;
        if (len >= 4) {
            this.buf[this.pos++] = (val >> 24) & 0x000000FF;
            this.buf[this.pos++] = (val >> 16) & 0x000000FF;
        }
        if (len >= 2)
            this.buf[this.pos++] = (val >> 8) & 0x000000FF;
        this.buf[this.pos++] = val & 0x000000FF;
    }

    /**
     *  Reads a numerical value of the given size from the input buffer containg data in the TPM wire format.
     *  @param len  Size of the numerical value in bytes
     *  @returns Extracted numerical value
     */
    public fromTpm(len: number) : number
    {
        if (!this.checkLen(len))
            return 0;
        let res : number = 0;
        if (len >= 4) {
            res += (this.buf[this.pos++] << 24);
            res += (this.buf[this.pos++] << 16);
        }
        if (len >= 2)
            res += (this.buf[this.pos++] << 8);
        res += this.buf[this.pos++];
        return res;
    }

    /**
     *  Writes the given byte array to the output buffer as a TPM2B structure in the TPM wire format.
     *  @param val  Byte array to marshal
     *  @param sizeLen  Length of the byte array size in bytes
     */
    public toTpm2B(data: Buffer, sizeLen: number = 2) : void
    {
        if (data == null || data.length == 0)
        {
            this.toTpm(0, sizeLen);
        }
        else if (this.checkLen(data.length + sizeLen))
        {
            this.toTpm(data.length, sizeLen);
            data.copy(this.buf, this.pos);
            this.pos += data.length;
        }
    }

    /**
     *  Reads a byte array from its a TPM2B structure representation in the TPM wire format.
     *  @param sizeLen  Length of the byte array size in bytes
     *  @returns Extracted byte buffer
     */
    public fromTpm2B(sizeLen: number = 2) : Buffer
    {
        let len : number = this.fromTpm(sizeLen);
        let begin: number = this.pos;
        this.pos += len;
        return this.buf.slice(begin, this.pos);
    }

    public createFromTpm<T extends TpmMarshaller>(type: {new(): T}): T
    {
        let newObj = new type();
        newObj.fromTpm(this);
        return newObj;
    }

    public sizedToTpm<T extends TpmMarshaller>(obj: T, lenSize: number) : void
    {
        if (obj == null)
            return this.toTpm(0, lenSize);

        if (!this.checkLen(lenSize))
            return;

        // Remember position to marshal the size of the data structure
        let sizePos = this.pos;
        // Account for the reserved size area
        this.pos += lenSize;
        obj.toTpm(this);
        let finalPos = this.pos;
        // Marshal the data structure size
        this.buf.writeUIntBE(finalPos - (sizePos + lenSize), sizePos, lenSize);
    }

    public sizedFromTpm<T extends TpmMarshaller>(type: {new(): T}, lenSize: number) : T
    {
        let size = this.fromTpm(lenSize);
        if (size == 0)
            return null;

        this.sizedStructSizes.push(new SizedStructInfo(this.pos, size));
        let newObj: T;
        newObj = this.createFromTpm(type);
        this.sizedStructSizes.pop();
        return newObj;
    }

    public bufferToTpm(buf: Buffer) : void
    {
        if (!this.checkLen(buf.length))
            return;
        buf.copy(this.buf, this.pos);
        this.pos += buf.length;
    }

    public bufferFromTpm(size: number) : Buffer
    {
        if (!this.checkLen(size))
            return null;
        let newBuf = new Buffer(size);
        this.buf.copy(newBuf, 0, this.pos, this.pos + size);
        this.pos += size;
        return newBuf;
    }

    public arrayToTpm<T extends TpmMarshaller>(arr: T[], lenSize: number) : void
    {
        if (arr == null)
            return this.toTpm(0, lenSize);

        this.toTpm(arr.length, lenSize);
        for (let elt of arr)
        {
            if (!this.isOk())
                break;
            elt.toTpm(this);
        }
    }

    public arrayFromTpm<T extends TpmMarshaller>(type: {new(): T}, lenSize: number) : T[]
    {
        let len = this.fromTpm(lenSize);
        if (len == 0)
            return [];

        let newArr = new Array<T>(len);
        for (let i = 0; i < len; ++i)
        {
            if (!this.isOk())
                break;
            newArr[i] = this.createFromTpm(type);
        }
        return newArr;
    }

    public valArrToTpm<T extends number>(arr: T[], size: number, lenSize: number)
    {
        if (arr == null)
            return this.toTpm(0, lenSize);

        this.toTpm(arr.length, lenSize);
        for (let val of arr)
        {
            if (!this.isOk())
                break;
            this.toTpm(val, size);
        }
    }

    public valArrFromTpm<T extends number>(size: number, lenSize: number): T[]
    {
        let len = this.fromTpm(lenSize);
        if (len == 0)
            return [];

        let newArr = new Array<T>(len);
        for (let i = 0; i < len; ++i)
        {
            if (!this.isOk())
                break;
            newArr[i] = <T>this.fromTpm(size);
        }
        return newArr;
    }
}; // class TpmBuffer



export function nonStandardToTpm(s: TpmMarshaller, buf: TpmBuffer)
{
	if (s instanceof TPMT_SYM_DEF_OBJECT)
	{
		let sdo = <TPMT_SYM_DEF_OBJECT>s;
		buf.toTpm(sdo.algorithm, 2);
		if (sdo.algorithm != TPM_ALG_ID.NULL) {
		    buf.toTpm(sdo.keyBits, 2);
		    buf.toTpm(sdo.mode, 2);
        }
	}
	else if (s instanceof TPMT_SYM_DEF)
	{
		let sd = <TPMT_SYM_DEF>s;
		buf.toTpm(sd.algorithm, 2);
		if (sd.algorithm != TPM_ALG_ID.NULL) {
		    buf.toTpm(sd.keyBits, 2);
		    buf.toTpm(sd.mode, 2);
        }
	}
	else
    {
		throw new Error("nonStandardToTpm(): Unexpected TPM structure type");
        //console.log("nonStandardToTpm(): Unexpected TPM structure type");
    }
}

export function nonStandardFromTpm(s: TpmMarshaller, buf: TpmBuffer)
{
	if (s instanceof TPMT_SYM_DEF_OBJECT)
	{
		let sdo = <TPMT_SYM_DEF_OBJECT>s;
		sdo.algorithm = buf.fromTpm(2);
		if (sdo.algorithm != TPM_ALG_ID.NULL) {
		    sdo.keyBits = buf.fromTpm(2);
		    sdo.mode = buf.fromTpm(2);
        }
	}
	else if (s instanceof TPMT_SYM_DEF)
	{
		let sd = <TPMT_SYM_DEF>s;
		sd.algorithm = buf.fromTpm(2);
		if (sd.algorithm != TPM_ALG_ID.NULL) {
		    sd.keyBits = buf.fromTpm(2);
		    sd.mode = buf.fromTpm(2);
        }
	}
	else
    {
    	throw new Error("nonStandardFromTpm(): Unexpected TPM structure type");
        //console.log("nonStandardFromTpm(): Unexpected TPM structure type");
    }
}
