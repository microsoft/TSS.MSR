package tss;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class OutByteBuf
{
    ByteBuffer buf;

    void init(byte[] backingBuffer) { buf = ByteBuffer.wrap(backingBuffer); }

    void init(int capacity) { init(new byte[capacity]); }

    /** Constructs output marshling buffer with the default capacity */
    public OutByteBuf() { init(4096); }

    /** Constructs output marshling buffer with the given capacity */
    public OutByteBuf(int capacity) { init(capacity); }

    //public OutByteBuf(byte[] backingBuffer) { init(backingBuffer); }

    public void clear() { buf.clear(); }

    public void reset() { clear(); }

    public int curPos() { return buf.position(); }
    
    public void curPos(int newPos) { buf.position(newPos); }

    /** @return  Size of the backing byte buffer.
     *           Note that during marshaling this size normally exceeds the amount of actually
     *           stored data until trim() is invoked. 
     */
    public int size() { return buf.capacity(); }

    /** @return  Copy of the filled part of this marshaling buffer */
    public byte[] buffer()
    {
        return Arrays.copyOf(buf.array(), curPos());
    }

    /** Shrinks the backing byte buffer so that it ends at the current position
     *  @return  reference to the backing byte array
     */
    public byte[] trim()
    {
        init(buffer());
        return this.buf.array();
    }

    public void writeSizedByteBuf(byte[] data, int sizeLen)
    {
        writeNum(data != null ? data.length : 0, sizeLen);
        if (data != null)
            buf.put(data, 0, data.length);
    }

    public void writeSizedByteBuf(byte[] data) { writeSizedByteBuf(data, 2);    }

    public void writeByteBuf(byte[] data) 
    {
        if (data != null)
            buf.put(data, 0, data.length);
    }

    public void writeNum(long val, int len) 
    {
        switch(len)
        {
        case 1: buf.put((byte)val); return;
        case 2: writeByteBuf(Helpers.hostToNet((short)val)); return;
        case 4: writeByteBuf(Helpers.hostToNet((int)val)); return;
        case 8: writeByteBuf(Helpers.hostToNet(val)); return;
        default: assert(false);
        }
    }

    public void writeByte(byte val) { buf.put((byte)val); }

    public void writeShort(int val) { writeNum(val, 2); }

    public void writeInt(int val) { writeNum(val, 4); }

    public void writeInt64(long val) { writeNum(val, 8); }

    public void writeObj(TpmMarshaller o) { o.toTpm(this); }
    
    // Argument type is not TpmStructure as the method needs to handle not only 
    // TPM structures but also enums that are implemented as first class objects
    // (rather than value types) in Java.
    public void writeObjArr(TpmMarshaller[] arr) 
    {
        // Length of the array size is always 4 bytes
        if (arr == null)
        {
            writeInt(0);
            return;
        }

        writeInt(arr.length);
        for(TpmMarshaller o : arr)
            o.toTpm(this);
    }

    public void writeNumAtPos(int val, int pos, int len)
    {
        int curPos = this.curPos();
        this.curPos(pos);
        this.writeNum(val, len);
        this.curPos(curPos);
    }
    public void writeNumAtPos(int val, int pos) { writeNumAtPos(val, pos, 4); }
}
