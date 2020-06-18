/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */


class SizedStructInfo
{
	constructor(
        public startPos: number,
        public size: number
    ) {}
};

export interface TpmMarshaller
{
    /** Convert this object to its TPM representation and store it in the given marshalng buffer
	 *  @param buf  Output marshaling buffer
     */
	toTpm(buf: TpmBuffer) : void;
	
    /** Populate this object from the TPM representation in the given marshaling buffer
	 *  @param buf  Input marshaling buffer
     */
	initFromTpm(buf: TpmBuffer) : void;
} // interface TpmMarshaller


export class TpmBuffer
{
    protected buf: Buffer = null;
    protected pos: number = 0;
    protected outOfBounds: boolean = false;

    private sizedStructSizes: Array<SizedStructInfo> = null;

    /** Constructs output (default) or input marshaling buffer depending on the parameter.
     *  @param  capacityOrSrcBuf  For output marshling buffer this is the buffer's capacity
     *          (i.e. the maximum allowed total size of the marshaled data). 
     *          For input marshling buffer this is an existing TpmBuffer or any kind of 
     *          a byte buffer or array that can be used to construct a Buffer object.
     */
    constructor(capacityOrSrcBuf: TpmBuffer | any = 4096)
    {
        if (capacityOrSrcBuf instanceof TpmBuffer)
        {
            this.buf = new Buffer(capacityOrSrcBuf.buf);
            this.pos = capacityOrSrcBuf.pos;
        }
        else
        {
            if (capacityOrSrcBuf === undefined)
                capacityOrSrcBuf = 4096
            this.buf = new Buffer(capacityOrSrcBuf);
        }
        this.sizedStructSizes = new Array<SizedStructInfo>();
    }

    /** @return  Reference to the underlying byte buffer */
    get buffer(): Buffer { return this.buf; }

    /** @return  Size of the backing byte buffer.
     *      Note that during marshaling this size normally exceeds the amount of actually
     *      stored data until trim() is invoked. 
     */
    get size(): number { return this.buf.length; }

    get curPos(): number { return this.pos; }
    set curPos(newPos: number)
    {
        this.pos = newPos;
        this.outOfBounds = newPos <= this.size;
    }

    public isOk(): boolean
    {
        return !this.outOfBounds;
    }

    /** Shrinks the backing byte buffer so that it ends at the current position
     *  @return  Reference to the (shrunk) backing byte buffer
     */
    public trim() : Buffer
    {
        // New buffer references the same memory
        return this.buf = this.buf.slice(0, this.pos)
    }

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

    /** Writes the given 8-bit integer to this buffer
     *  @param val  8-bit integer value to marshal
     */
    public writeByte(val: number) : void
    {
        if (this.checkLen(1))
            this.buf[this.pos++] = val & 0x00FF;
    }

    /** Marshals the given 16-bit integer to this buffer.
     *  @param val  16-bit integer value to marshal
     */
    public writeShort(val: number) : void
    {
        this.writeNum(val, 2);
    }

    /** Marshals the given 32-bit integer to this buffer.
     *  @param val  32-bit integer value to marshal
     */
    public writeInt(val: number) : void
    {
        this.writeNum(val, 4);
    }

    /** Marshals the given 64-bit integer to this buffer.
     *  @param val  64-bit integer value to marshal
     */
    public writeInt64(val: number) : void
    {
        this.writeNum(val, 8);
    }

    /** Reads a byte from this buffer.
     *  @return The byte read
     */
    public readByte() : number
    {
        if (this.checkLen(1))
            return this.buf[this.pos++];
    }

    /** Unmarshals a 16-bit integer from this buffer.
     *  @return Unmarshaled 16-bit integer
     */
    public readShort() : number
    {
        return this.readNum(2);
    }

    /** Unmarshals a 32-bit integer from this buffer.
     *  @return Unmarshaled 32-bit integer
     */
    public readInt() : number
    {
        return this.readNum(4);
    }

    /** Unmarshals a 64-bit integer from this buffer.
     *  @return Unmarshaled 64-bit integer
     */
    public readInt64() : number
    {
        return this.readNum(8);
    }

    /** Marshalls the given byte buffer with no length prefix.
     *  @param data  Byte buffer to marshal
     */
    public writeByteBuf(data: Buffer) : void
    {
        if (!this.checkLen(data.length))
            return;
        data.copy(this.buf, this.pos);
        this.pos += data.length;
    }

    /** Unmarshalls a byte buffer of the given size (no marshaled length prefix).
     *  @param size  Size of the byte buffer to unmarshal
     *  @return  Unmarshaled byte buffer
     */
    public readByteBuf(size: number) : Buffer
    {
        if (!this.checkLen(size))
            return null;
        let newBuf = new Buffer(size);
        this.buf.copy(newBuf, 0, this.pos, this.pos + size);
        this.pos += size;
        return newBuf;
    }

    /** Marshalls the given byte buffer with a length prefix.
     *  @param data  Byte buffer to marshal
     *  @param sizeLen  Length of the size prefix in bytes
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

    /** Unmarshals a byte buffer from its size-prefixed representation in the TPM wire format.
     *  @param sizeLen  Length of the size prefix in bytes
     *  @return  Unmarshaled byte buffer
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
        newObj.initFromTpm(this);
        return newObj;
    }

    public writeSizedObj<T extends TpmMarshaller>(obj: T) : void
    {
        const lenSize = 2;  // Length of the object size is always 2 bytes
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
        const lenSize = 2;  // Length of the object size is always 2 bytes
        let size = this.readShort();
        if (size == 0)
            return null;

        this.sizedStructSizes.push(new SizedStructInfo(this.pos, size));
        let newObj = new type();
        newObj.initFromTpm(this);
        this.sizedStructSizes.pop();
        return newObj;
    }

    public writeObjArr(arr: TpmMarshaller[]) : void
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
            newArr[i] = new type();
            newArr[i].initFromTpm(this);
        }
        return newArr;
    }

    public writeValArr(arr: number[], valSize: number) : void
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

