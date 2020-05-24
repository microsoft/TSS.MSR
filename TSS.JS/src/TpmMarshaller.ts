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
        buf.writeSizedObj(this);
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

    protected checkLen(len: number): boolean
    {
        if (this.buf.length < this.pos + len)
        {
            this.outOfBounds = true;
            this.pos = this.buf.length;
            return false;
        }
        return true;
    }

    protected writeNum(val: number, len: number) : void
    {
        if (!this.checkLen(len))
            return;
        if (len == 8) {
            this.buf[this.pos++] = (val >> 56) & 0x00FF;
            this.buf[this.pos++] = (val >> 48) & 0x00FF;
            this.buf[this.pos++] = (val >> 40) & 0x00FF;
            this.buf[this.pos++] = (val >> 32) & 0x00FF;
        }
        if (len >= 4) {
            this.buf[this.pos++] = (val >> 24) & 0x00FF;
            this.buf[this.pos++] = (val >> 16) & 0x00FF;
        }
        if (len >= 2)
            this.buf[this.pos++] = (val >> 8) & 0x00FF;
        this.buf[this.pos++] = val & 0x00FF;
    }

    protected readNum(len: number) : number
    {
        if (!this.checkLen(len))
            return 0;

        let res : number = 0;
        if (len == 8) {
            res += (this.buf[this.pos++] << 56);
            res += (this.buf[this.pos++] << 48);
            res += (this.buf[this.pos++] << 40);
            res += (this.buf[this.pos++] << 32);
        }
        if (len >= 4) {
            res += (this.buf[this.pos++] << 24);
            res += (this.buf[this.pos++] << 16);
        }
        if (len >= 2)
            res += (this.buf[this.pos++] << 8);
        res += this.buf[this.pos++];
        return res;
    }

    public writeNumAtPos(val: number, pos: number, len: number = 4) : void
    {
        let curPos = this.pos;
        this.pos = pos;
        this.writeNum(val, len);
        this.pos = curPos;
    }

    /**
     *  Writes the given 8-bit integer to the buffer
     *  @param val  8-bit integer value to marshal
     */
    public writeByte(val: number) : void
    {
        if (this.checkLen(1))
            this.buf[this.pos++] = val & 0x00FF;
    }

    /**
     *  Converts the given 16-bit integer to the TPM wire format, and writes it to the buffer.
     *  @param val  16-bit integer value to marshal
     */
    public writeShort(val: number) : void
    {
        this.writeNum(val, 2);
    }

    /**
     *  Converts the given 32-bit integer to the TPM wire format, and writes it to the buffer.
     *  @param val  32-bit integer value to marshal
     */
    public writeInt(val: number) : void
    {
        this.writeNum(val, 4);
    }

    /**
     *  Converts the given 64-bit integer to the TPM wire format, and writes it to the buffer.
     *  @param val  64-bit integer value to marshal
     */
    public writeInt64(val: number) : void
    {
        this.writeNum(val, 8);
    }

    /**
     *  Reads an 8-bit integer from the buffer containg data in the TPM wire format.
     *  @return Unmarshaled 8-bit integer
     */
    public readByte() : number
    {
        if (this.checkLen(1))
            return this.buf[this.pos++];
    }

    /**
     *  Reads a 16-bit integer from the buffer containg data in the TPM wire format.
     *  @return Unmarshaled 16-bit integer
     */
    public readShort() : number
    {
        return this.readNum(2);
    }

    /**
     *  Reads a 32-bit integer from the buffer containg data in the TPM wire format.
     *  @return Unmarshaled 32-bit integer
     */
    public readInt() : number
    {
        return this.readNum(4);
    }

    /**
     *  Reads a 64-bit integer from the buffer containg data in the TPM wire format.
     *  @return Unmarshaled 64-bit integer
     */
    public readInt64() : number
    {
        return this.readNum(8);
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
            this.writeNum(0, sizeLen);
        }
        else if (this.checkLen(data.length + sizeLen))
        {
            this.writeNum(data.length, sizeLen);
            data.copy(this.buf, this.pos);
            this.pos += data.length;
        }
    }

    /**
     *  Reads a byte array from its a TPM2B structure representation in the TPM wire format.
     *  @param sizeLen  Length of the byte array size in bytes
     *  @return Extracted byte buffer
     */
    public readSizedByteBuf(sizeLen: number = 2) : Buffer
    {
        let len : number = this.readNum(sizeLen);
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

    public writeSizedObj<T extends TpmMarshaller>(obj: T) : void
    {
        const lenSize = 2;  // Length of the object size in bytes
        
        if (obj == null)
            return this.writeShort(0);

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
        this.writeShort(objLen);
        this.pos += objLen;
    }

    public createSizedObj<T extends TpmMarshaller>(type: {new(): T}) : T
    {
        const lenSize = 2;  // Length of the object size in bytes
        let size = this.readShort();
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

    public writeObjArr<T extends TpmMarshaller>(arr: T[]) : void
    {
        // Length of the array size is always 4 bytes
        if (arr == null)
            return this.writeInt(0);

        this.writeInt(arr.length);
        for (let elt of arr)
        {
            if (!this.isOk())
                break;
            elt.toTpm(this);
        }
    }

    public readObjArr<T extends TpmMarshaller>(type: {new(): T}) : T[]
    {
        // Length of the array size is always 4 bytes
        let len = this.readInt();
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

    public writeValArr<T extends number>(arr: T[], valSize: number) : void
    {
        // Length of the array size is always 4 bytes
        if (arr == null)
            return this.writeInt(0);

        this.writeInt(arr.length);
        for (let val of arr)
        {
            if (!this.isOk())
                break;
            this.writeNum(val, valSize);
        }
    }

    public readValArr<T extends number>(valSize: number): T[]
    {
        // Length of the array size is always 4 bytes
        let len = this.readInt();
        if (len == 0)
            return [];

        let newArr = new Array<T>(len);
        for (let i = 0; i < len; ++i)
        {
            if (!this.isOk())
                break;
            newArr[i] = <T>this.readNum(valSize);
        }
        return newArr;
    }
}; // class TpmBuffer



export function nonStandardToTpm(s: TpmMarshaller, buf: TpmBuffer)
{
	if (s instanceof TPMT_SYM_DEF_OBJECT)
	{
		let sdo = <TPMT_SYM_DEF_OBJECT>s;
		buf.writeShort(sdo.algorithm);
		if (sdo.algorithm != TPM_ALG_ID.NULL) {
		    buf.writeShort(sdo.keyBits);
		    buf.writeShort(sdo.mode);
        }
	}
	else if (s instanceof TPMT_SYM_DEF)
	{
		let sd = <TPMT_SYM_DEF>s;
		buf.writeShort(sd.algorithm);
		if (sd.algorithm != TPM_ALG_ID.NULL) {
		    buf.writeShort(sd.keyBits);
		    buf.writeShort(sd.mode);
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
		sdo.algorithm = buf.readShort();
		if (sdo.algorithm != TPM_ALG_ID.NULL) {
		    sdo.keyBits = buf.readShort();
		    sdo.mode = buf.readShort();
        }
	}
	else if (s instanceof TPMT_SYM_DEF)
	{
		let sd = <TPMT_SYM_DEF>s;
		sd.algorithm = buf.readShort();
		if (sd.algorithm != TPM_ALG_ID.NULL) {
		    sd.keyBits = buf.readShort();
		    sd.mode = buf.readShort();
        }
	}
	else
    {
    	throw new Error("nonStandardFromTpm(): Unexpected TPM structure type");
        //console.log("nonStandardFromTpm(): Unexpected TPM structure type");
    }
}
