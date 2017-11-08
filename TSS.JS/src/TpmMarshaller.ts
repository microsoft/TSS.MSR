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

let g_structSize = new Array<SizedStructInfo>();

export function getCurStuctRemainingSize(curPos: number): number
{
    let ssi: SizedStructInfo = g_structSize[g_structSize.length - 1];
    return ssi.size - (curPos - ssi.startPos);
}

export interface TpmMarshaller
{
    /**
	 *  Convert this object to its TPM representation and store in the output byte buffer object
	 *  @param buf Output byte buffer for the marshaled representation of this object
	 *  @param startPos Current write position in the output buffer
     *  @returnsNew write position in the output buffer
     */
	toTpm(buf: Buffer, startPos: number): number;
	
    /**
	 *  Populate this object from the TPM representation in the input byte buffer object
	 *  @param buf  An input byte buffer containg marshaled representation of the object
	 *  @param startPos  Current read position in the input buffer
     *  @returns Number of bytes unmarshaled
     */
	fromTpm(buf: Buffer, startPos: number): number;
} // interface TpmMarshaller


export abstract class TpmStructure implements TpmMarshaller
{
    /** TpmMarshaller method */
	abstract toTpm(buf: Buffer, pos: number): number;

    /** TpmMarshaller method */
    abstract fromTpm(buf: Buffer, pos: number): number;

    asTpm2B(): Buffer
    {
        let buf = new Buffer(4096);
        let size = this.toTpm(buf, 2);
        toTpm(size - 2, buf, 2, 0);
        return buf.slice(0, size);
    }

    asTpm(): Buffer
    {
        let buf = new Buffer(4096);
        let size = this.toTpm(buf, 0);
        return buf.slice(0, size);
    }

	toTpm2B(buf: Buffer, pos: number): number
    {
        return toTpm2B(this.asTpm(), buf, pos);
    }
};



/**
 *  Converts the given numerical value of the given size to the TPM wire format.
 *  @param val  Numerical value to marshal
 *  @param buf  Output buffer
 *  @param size  Size of the numerical value in bytes
 *  @param val  Current write position in the output buffer
 *  @returns New write posisition in the output buffer
 */
export function toTpm(val: number, buf: Buffer, size: number, pos: number): number
{
    // TODO: Replace with Buffer.writeUIntBE()
    if (size >= 4) {
        buf[pos++] = (val >> 24) & 0x000000FF;
        buf[pos++] = (val >> 16) & 0x000000FF;
    }
    if (size >= 2)
        buf[pos++] = (val >> 8) & 0x000000FF;
    buf[pos++] = val & 0x000000FF;
    return pos;
}

/**
 *  Reads a numerical value of the given size from the input buffer containg data in the TPM wire format.
 *  @param buf  Input byte buffer
 *  @param size  Size of the numerical value in bytes
 *  @param val  Current read position in the output buffer
 *  @returns A pair containg the extracted numerical value and the new read posisition in the input buffer
 */
export function fromTpm(buf : Buffer, size : number, pos : number = 0) : [number, number]
{
    // TODO: Replace with Buffer.readUIntBE()
    let res : number = 0;
    if (size >= 4) {
        res += (buf[pos++] << 24);
        res += (buf[pos++] << 16);
    }
    if (size >= 2)
        res += (buf[pos++] << 8);
    res += buf[pos++];
    return [res, pos];
}

/**
 *  Writes the given byte array to the output buffer as a TPM2B structure in the TPM wire format.
 *  @param val  Byte array to marshal
 *  @param buf  Output byte buffer
 *  @param val  Current position in the output buffer
 *  @returns New posisition in the output buffer
 */
export function toTpm2B(val: Buffer, buf: Buffer, pos: number): number
{
    if (val == null)
        pos = toTpm(0, buf, 2, pos);
    else {
        pos = toTpm(val.length, buf, 2, pos);
        val.copy(buf, pos);
        pos += val.length;
    }
    return pos;
}

/**
 *  Reads a byte array from its a TPM2B structure representation in the TPM wire format.
 *  @param buf  Input buffer
 *  @param val  Current position in the output buffer
 *  @returns A pair containg the extracted byte buffer and new posisition in the input buffer
 */
export function fromTpm2B(buf : Buffer, pos : number = 0) : [Buffer, number]
{
    let len : number = fromTpm(buf, 2, pos)[0];
    let end : number = pos + 2 + len;
    return [buf.slice(pos + 2, end), end];
}

export function createFromTpm<T extends TpmMarshaller>(type: {new(): T}, buf: Buffer, pos: number): [T, number]
{
    let newObj = new type();
    pos = newObj.fromTpm(buf, pos);
    return [newObj, pos];
}

export function sizedToTpm<T extends TpmMarshaller>(obj: T, buf: Buffer, lenSize: number, pos: number): number
{
    if (obj == null)
        return toTpm(0, buf, lenSize, pos);

    // Remember position to marshal the size of the data structure
    let posSize = pos;
    // '+ 2' accounts for the reserved size area
    pos = obj.toTpm(buf, pos + lenSize);
    // Marshal the data structure size
    toTpm(pos - (posSize + lenSize), buf, lenSize, posSize);
    return pos;
}

export function sizedFromTpm<T extends TpmMarshaller>(type: {new(): T}, buf: Buffer, lenSize: number, pos: number): [T, number]
{
    let size;
    [size, pos] = fromTpm(buf, lenSize, pos);
    if (size == 0)
        return [null, pos];

    g_structSize.push(new SizedStructInfo(pos, size));
    let newObj: T;
    [newObj, pos] = createFromTpm(type, buf, pos);
    g_structSize.pop();
    return [newObj, pos];
}

export function arrayToTpm<T extends TpmMarshaller>(arr: T[], buf: Buffer, lenSize: number, pos: number): number
{
    if (arr == null)
        return toTpm(0, buf, lenSize, pos);

    pos = toTpm(arr.length, buf, lenSize, pos);
    for (let elt of arr)
        pos = elt.toTpm(buf, pos);
    return pos;
}

export function arrayFromTpm<T extends TpmMarshaller>(type: {new(): T}, buf: Buffer, lenSize: number, pos: number): [T[], number]
{
    let len;
    [len, pos] = fromTpm(buf, lenSize, pos);
    if (len == 0)
        return [[], pos];

    let newArr = new Array<T>(len);
    for (let i = 0; i < len; ++i)
        [newArr[i], pos] = createFromTpm(type, buf, pos);
    return [newArr, pos];
}


export function nonStandardToTpm(s: TpmMarshaller, buf: Buffer, pos: number): number
{
	if (s instanceof TPMT_SYM_DEF_OBJECT)
	{
		let sdo = <TPMT_SYM_DEF_OBJECT>s;
		pos = toTpm(sdo.algorithm, buf, 2, pos);
		if (sdo.algorithm != TPM_ALG_ID.NULL) {
		    pos = toTpm(sdo.keyBits, buf, 2, pos);
		    pos = toTpm(sdo.mode, buf, 2, pos);
        }
	}
	else if (s instanceof TPMT_SYM_DEF)
	{
		let sd = <TPMT_SYM_DEF>s;
		pos = toTpm(sd.algorithm, buf, 2, pos);
		if (sd.algorithm != TPM_ALG_ID.NULL) {
		    pos = toTpm(sd.keyBits, buf, 2, pos);
		    pos = toTpm(sd.mode, buf, 2, pos);
        }
	}
	else
		throw new Error("nonStandardMarshallOut(): Unexpected TPM structure type");
    return pos;
}

export function nonStandardFromTpm(s: TpmMarshaller, buf: Buffer, pos: number): number
{
	if (s instanceof TPMT_SYM_DEF_OBJECT)
	{
		let sdo = <TPMT_SYM_DEF_OBJECT>s;
		[sdo.algorithm, pos] = fromTpm(buf, 2, pos);
		if (sdo.algorithm != TPM_ALG_ID.NULL) {
		    [sdo.keyBits, pos] = fromTpm(buf, 2, pos);
		    [sdo.mode, pos] = fromTpm(buf, 2, pos);
        }
	}
	else if (s instanceof TPMT_SYM_DEF)
	{
		let sd = <TPMT_SYM_DEF>s;
		[sd.algorithm, pos] = fromTpm(buf, 2, pos);
		if (sd.algorithm != TPM_ALG_ID.NULL) {
		    [sd.keyBits, pos] = fromTpm(buf, 2, pos);
		    [sd.mode, pos] = fromTpm(buf, 2, pos);
        }
	}
	else
    	throw new Error("nonStandardMarshallIn(): Unexpected TPM structure type");
    return pos;
}
