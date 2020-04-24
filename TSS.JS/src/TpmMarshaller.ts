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
     *  
	 *  @param buf Output byte buffer for the marshaled representation of this object
     */
	toTpm(buf: TpmBuffer) : void;
	
    /**
	 *  Populate this object from the TPM representation in the input byte buffer object
     *  
	 *  @param buf  An input byte buffer containg marshaled representation of the object
     */
	fromTpm(buf: TpmBuffer) : void;
} // interface TpmMarshaller


export class TpmStructure implements TpmMarshaller
{
    /** TpmMarshaller method */
	toTpm(buf: TpmBuffer) : void {}

    /** TpmMarshaller method */
    fromTpm(buf: TpmBuffer) : void {}

    asTpm2B(): Buffer
    {
        let buf = new TpmBuffer(4096);
        buf.writeSizedObj(this, 2);
        return buf.trim().buffer;
    }

    asTpm(): Buffer
    {
        let buf = new TpmBuffer(4096);
        this.toTpm(buf);
        return buf.trim().buffer;
    }

	writeSizedByteBuf(buf: TpmBuffer) : void
    {
        return buf.writeSizedByteBuf(this.asTpm());
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
        this.outOfBounds = newPos <= this.length;
    }

    public isOk(): boolean
    {
        return !this.outOfBounds;
    }

    public trim() : TpmBuffer
    {
        // New buffer references the same memory
        this.buf = this.buf.slice(0, this.pos)
        return this;
    }
/*
    public slice(startPos: number, endPos: number) : TpmBuffer
    {
        return new TpmBuffer(this.buf.slice(startPos, endPos));
    }

    public copy(dst: TpmBuffer, dstStart: number) : number
    {
        if (dst.length < dstStart + this.length)
            return 0;
        this.buf.copy(dst.buf, dstStart);
        dst.pos = dstStart + this.length;
        return this.length;
    }
*/

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
    public writeInt(val: number, len: number) : void
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

    public writeInt64(val: number) : void
    {
        if (!this.checkLen(8))
            return;

        this.buf[this.pos++] = (val >> 56) & 0x00000000000000FF;
        this.buf[this.pos++] = (val >> 48) & 0x00000000000000FF;
        this.buf[this.pos++] = (val >> 40) & 0x00000000000000FF;
        this.buf[this.pos++] = (val >> 32) & 0x00000000000000FF;
        this.buf[this.pos++] = (val >> 24) & 0x00000000000000FF;
        this.buf[this.pos++] = (val >> 16) & 0x00000000000000FF;
        this.buf[this.pos++] = (val >> 8) & 0x00000000000000FF;
        this.buf[this.pos++] = val & 0x00000000000000FF;
    }

    /**
     *  Reads a numerical value of the given size from the input buffer containg data in the TPM wire format.
     *  @param len  Size of the numerical value in bytes
     *  @returns Extracted numerical value
     */
    public readInt(len: number) : number
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

    public readInt64() : number
    {
        if (!this.checkLen(8))
            return 0;

        let res : number = 0;
        res += (this.buf[this.pos++] << 56);
        res += (this.buf[this.pos++] << 48);
        res += (this.buf[this.pos++] << 40);
        res += (this.buf[this.pos++] << 32);
        res += (this.buf[this.pos++] << 24);
        res += (this.buf[this.pos++] << 16);
        res += (this.buf[this.pos++] << 8);
        res += this.buf[this.pos++];
        return res;
    }

    /**
     *  Writes the given byte array to the output buffer as a TPM2B structure in the TPM wire format.
     *  @param val  Byte array to marshal
     *  @param sizeLen  Length of the byte array size in bytes
     */
    public writeSizedByteBuf(data: Buffer, sizeLen: number = 2) : void
    {
        if (data == null || data.length == 0)
        {
            this.writeInt(0, sizeLen);
        }
        else if (this.checkLen(data.length + sizeLen))
        {
            this.writeInt(data.length, sizeLen);
            data.copy(this.buf, this.pos);
            this.pos += data.length;
        }
    }

    /**
     *  Reads a byte array from its a TPM2B structure representation in the TPM wire format.
     *  @param sizeLen  Length of the byte array size in bytes
     *  @returns Extracted byte buffer
     */
    public readSizedByteBuf(sizeLen: number = 2) : Buffer
    {
        let len : number = this.readInt(sizeLen);
        let start: number = this.pos;
        this.pos += len;
        return this.buf.slice(start, this.pos);
    }

    public createObj<T extends TpmMarshaller>(type: {new(): T}): T
    {
        let newObj = new type();
        newObj.fromTpm(this);
        return newObj;
    }

    public writeSizedObj<T extends TpmMarshaller>(obj: T, lenSize: number) : void
    {
        if (obj == null)
            return this.writeInt(0, lenSize);

        if (!this.checkLen(lenSize))
            return;

        // Remember position to marshal the size of the data structure
        let sizePos = this.pos;
        // Account for the reserved size area
        this.pos += lenSize;
        // Marshal the object
        obj.toTpm(this);
        // Calc marshaled object len
        let objLen = this.pos - (sizePos + lenSize);
        // Marshal it in the appropriate position
        //this.buf.writeUIntBE(objLen, sizePos, lenSize);
        this.pos = sizePos;
        this.writeInt(objLen, lenSize);
        this.pos += objLen;
    }

    public createSizedObj<T extends TpmMarshaller>(type: {new(): T}, lenSize: number) : T
    {
        let size = this.readInt(lenSize);
        if (size == 0)
            return null;

        this.sizedStructSizes.push(new SizedStructInfo(this.pos, size));
        let newObj: T;
        newObj = this.createObj(type);
        this.sizedStructSizes.pop();
        return newObj;
    }

    // Marshal only data, no size prefix
    public writeByteBuf(data: Buffer) : void
    {
        if (!this.checkLen(data.length))
            return;
        data.copy(this.buf, this.pos);
        this.pos += data.length;
    }

    public readByteBuf(size: number) : Buffer
    {
        if (!this.checkLen(size))
            return null;
        let newBuf = new Buffer(size);
        this.buf.copy(newBuf, 0, this.pos, this.pos + size);
        this.pos += size;
        return newBuf;
    }

    public writeObjArr<T extends TpmMarshaller>(arr: T[], lenSize: number) : void
    {
        if (arr == null)
            return this.writeInt(0, lenSize);

        this.writeInt(arr.length, lenSize);
        for (let elt of arr)
        {
            if (!this.isOk())
                break;
            elt.toTpm(this);
        }
    }

    public readObjArr<T extends TpmMarshaller>(type: {new(): T}, lenSize: number) : T[]
    {
        let len = this.readInt(lenSize);
        if (len == 0)
            return [];

        let newArr = new Array<T>(len);
        for (let i = 0; i < len; ++i)
        {
            if (!this.isOk())
                break;
            newArr[i] = this.createObj(type);
        }
        return newArr;
    }

    public writeValArr<T extends number>(arr: T[], valSize: number, lenSize: number) : void
    {
        if (arr == null)
            return this.writeInt(0, lenSize);

        this.writeInt(arr.length, lenSize);
        for (let val of arr)
        {
            if (!this.isOk())
                break;
            this.writeInt(val, valSize);
        }
    }

    public readValArr<T extends number>(valSize: number, lenSize: number): T[]
    {
        let len = this.readInt(lenSize);
        if (len == 0)
            return [];

        let newArr = new Array<T>(len);
        for (let i = 0; i < len; ++i)
        {
            if (!this.isOk())
                break;
            newArr[i] = <T>this.readInt(valSize);
        }
        return newArr;
    }
}; // class TpmBuffer



export function nonStandardToTpm(s: TpmMarshaller, buf: TpmBuffer)
{
	if (s instanceof TPMT_SYM_DEF_OBJECT)
	{
		let sdo = <TPMT_SYM_DEF_OBJECT>s;
		buf.writeInt(sdo.algorithm, 2);
		if (sdo.algorithm != TPM_ALG_ID.NULL) {
		    buf.writeInt(sdo.keyBits, 2);
		    buf.writeInt(sdo.mode, 2);
        }
	}
	else if (s instanceof TPMT_SYM_DEF)
	{
		let sd = <TPMT_SYM_DEF>s;
		buf.writeInt(sd.algorithm, 2);
		if (sd.algorithm != TPM_ALG_ID.NULL) {
		    buf.writeInt(sd.keyBits, 2);
		    buf.writeInt(sd.mode, 2);
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
		sdo.algorithm = buf.readInt(2);
		if (sdo.algorithm != TPM_ALG_ID.NULL) {
		    sdo.keyBits = buf.readInt(2);
		    sdo.mode = buf.readInt(2);
        }
	}
	else if (s instanceof TPMT_SYM_DEF)
	{
		let sd = <TPMT_SYM_DEF>s;
		sd.algorithm = buf.readInt(2);
		if (sd.algorithm != TPM_ALG_ID.NULL) {
		    sd.keyBits = buf.readInt(2);
		    sd.mode = buf.readInt(2);
        }
	}
	else
    {
    	throw new Error("nonStandardFromTpm(): Unexpected TPM structure type");
        //console.log("nonStandardFromTpm(): Unexpected TPM structure type");
    }
}
