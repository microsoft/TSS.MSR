package tss;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Stack;

public class TpmBuffer
{
    /** Information about the TPM data structure being currently unmarshaled. **/
    public class SizedStructInfo {
        /** A TPM structure start position in the marshaled input buffer. **/
        public int startPos;

        /** Total size of the structure in bytes. **/
        public int size;

        public SizedStructInfo(int startPos, int size) {
            this.startPos = startPos;
            this.size = size;
        }
    } // class SizedStructInfo

    ByteBuffer buf;
    boolean outOfBounds;
    Stack<SizedStructInfo> sizedStructSizes;

    void init(byte[] backingBuffer)
    {
        buf = ByteBuffer.wrap(backingBuffer);
        sizedStructSizes = new Stack<SizedStructInfo>();
        outOfBounds = false;
    }

    void init(int capacity) { init(new byte[capacity]); }

    /** Constructs output marshling buffer with the default capacity of 4096 bytes */
    public TpmBuffer() { init(4096); }

    /** Constructs an output marshling buffer with the given capacity
     * @param capacity Capacity in bytes
     */
    public TpmBuffer(int capacity) { init(capacity); }

    /** Constructs an initialized input marshling buffer
     * @param buf A marshaled representation to initialize the new input buffer with
     */
    public TpmBuffer(byte[] buf) { init(buf); }

    public void clear() { buf.clear(); }

    public void reset() { clear(); }

    /** @return Reference to the backing byte buffer */
    public byte[] buffer() { return buf.array(); }

    /** @return Size of the backing byte buffer. Note that during marshaling this
     *          size normally exceeds the amount of actually stored data until trim()
     *          is invoked.
     */
    public int size() { return buf.capacity(); }

    /** @return Current read/write position in the the backing byte buffer */
    public int curPos() { return buf.position(); }

    /** Sets current read/write position in the the backing byte buffer
     * @param newPos New read/write position
     */
    public void curPos(int newPos)
    {
        buf.position(newPos);
        outOfBounds = size() < newPos;
    }

    /** @return true unless a previous read/write operation caused under/overflow correspondingly. */
    public boolean isOk() { return !outOfBounds; }

    /** Shrinks the backing byte buffer so that it ends at the current position
     * @return Reference to the shrunk backing byte array
     */
    public byte[] trim()
    {
        if (curPos() < size())
            init(Arrays.copyOf(buf.array(), curPos()));
        return this.buf.array();
    }

    public int getCurStuctRemainingSize()
    {
        SizedStructInfo ssi = sizedStructSizes.peek();
        return ssi.size - (curPos() - ssi.startPos);
    }

    boolean checkLen(int len)
    {
        if (size() < curPos() + len) {
            outOfBounds = true;
            return false;
        }
        return true;
    }

    public void writeNum(long val, int len)
    {
        if (!checkLen(len))
            return;
        switch (len) {
            case 1:
                buf.put((byte) val);
                return;
            case 2:
                writeByteBuf(Helpers.hostToNet((short)val));
                return;
            case 4:
                writeByteBuf(Helpers.hostToNet((int)val));
                return;
            case 8:
                writeByteBuf(Helpers.hostToNet(val));
                return;
        }
        assert (false);
    }

    public long readNum(int len)
    {
        if (!checkLen(len))
            return 0;
        switch (len) {
            case 1:
                return buf.get() & 0x00FF;
            case 2:
                return buf.getShort() & 0x0000FFFF;
            case 4:
                return buf.getInt() & 0x00000000FFFFFFFF;
            case 8:
                return buf.getLong();
        }
        assert (false);
        return 0;
    }

    public void writeNumAtPos(int val, int pos, int len)
    {
        int curPos = this.curPos();
        this.curPos(pos);
        this.writeNum(val, len);
        this.curPos(curPos);
    }

    public void writeNumAtPos(int val, int pos) { writeNumAtPos(val, pos, 4); }

    /** Writes the given 8-bit integer to this buffer
     * @param val 8-bit integer value to marshal
     */
    public void writeByte(byte val) {
        if (checkLen(1))
            buf.put(val);
    }

    public <T extends TpmEnum<T>> void writeByte(TpmEnum<T> val) {
        writeByte((byte) val.toInt());
    }

    /** Marshals the given 16-bit integer to this buffer.
     * @param val 16-bit integer value to marshal
     */
    public void writeShort(int val) { writeNum(val, 2); }

    public <T extends TpmEnum<T>>
    void writeShort(TpmEnum<T> val) { writeShort(val.toInt()); }

    /** Marshals the given 32-bit integer to this buffer.
     * @param val 32-bit integer value to marshal
     */
    public void writeInt(int val) { writeNum(val, 4); }

    public <T extends TpmEnum<T>>
    void writeInt(TpmEnum<T> val) { writeShort(val.toInt()); }

    /** Marshals the given 64-bit integer to this buffer.
     * @param val 64-bit integer value to marshal
     */
    public void writeInt64(long val) { writeNum(val, 8); }

    /** Reads a byte from this buffer.
     * @return The byte read
     */
    public byte readByte() { return checkLen(1) ? buf.get() : 0; }

    /** Unmarshals a 16-bit integer from this buffer.
     * @return Unmarshaled 16-bit integer (as 32-bit int because of Java's abysmal
     *         unsigned values handling)
     */
    public int readShort() { return (int) readNum(2); }

    /** Unmarshals a 32-bit integer from this buffer.
     * @return Unmarshaled 32-bit integer
     */
    public int readInt() { return (int) readNum(4); }

    /** Unmarshals a 64-bit integer from this buffer.
     * @return Unmarshaled 64-bit integer
     */
    public long readInt64() { return readNum(8); }

    /** Marshalls the given byte buffer with no length prefix.
     * @param data Byte buffer to marshal
     */
    public void writeByteBuf(byte[] data)
    {
        int dataSize = data != null ? data.length : 0;
        if (dataSize == 0 || !checkLen(data.length))
            return;
        buf.put(data, 0, data.length);
    }

    /** Unmarshalls a byte buffer of the given size (no marshaled length prefix).
     * @param size Size of the byte buffer to unmarshal
     * @return Unmarshaled byte buffer
     */
    public byte[] readByteBuf(int size)
    {
        if (!checkLen(size))
            return null;
        byte[] data = new byte[size];
        buf.get(data);
        return data;
    }

    /** Marshalls the given byte buffer with a length prefix.
     * @param data    Byte buffer to marshal
     * @param sizeLen Length of the size prefix in bytes
     */
    public void writeSizedByteBuf(byte[] data, int sizeLen)
    {
        writeNum(data != null ? data.length : 0, sizeLen);
        writeByteBuf(data);
    }

    public void writeSizedByteBuf(byte[] data) { writeSizedByteBuf(data, 2); }

    /** Unmarshals a byte buffer from its size-prefixed representation in the TPM wire format.
     * @param sizeLen Length of the size prefix in bytes
     * @return Unmarshaled byte buffer
     */
    public byte[] readSizedByteBuf(int sizeLen)
    {
        return readByteBuf((int) readNum(sizeLen));
    }

    public byte[] readSizedByteBuf() { return readSizedByteBuf(2); }

    public <T extends TpmMarshaller>
    T createObj(Class<T> type)
    {
        T newObj;
        try {
            newObj = type.newInstance();
        } catch (Exception e) {
            return null;
        }
        newObj.initFromTpm(this);
        return newObj;
    }

    public <T extends TpmMarshaller>
    void writeSizedObj(T obj)
    {
        final int lenSize = 2;  // Length of the object size is always 2 bytes
        if (obj == null)
        {
            writeShort(0);
            return;
        }
        if (!this.checkLen(lenSize))
            return;

        // Remember position to marshal the size of the data structure
        int sizePos = curPos();
        // Account for the reserved size area
        curPos(sizePos + lenSize);
        // Marshal the object
        obj.toTpm(this);
        // Calc marshaled object len
        int objSize = curPos() - (sizePos + lenSize);
        // Marshal it in the appropriate position
        writeNumAtPos(objSize, sizePos, lenSize);
    }

    public <T extends TpmMarshaller>
    T createSizedObj(Class<T> type)
    {
        // Length of the object size is always 2 bytes
        int size = readShort();
        if (size == 0)
            return null;

        sizedStructSizes.push(new SizedStructInfo(curPos(), size));
        T newObj = createObj(type);
        this.sizedStructSizes.pop();
        return newObj;
    }

    // Array element type is not TpmStructure as the method needs to handle not only 
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
        for(TpmMarshaller obj : arr)
        {
            if (!isOk())
                break;
            obj.toTpm(this);
        }
    }

    public <T extends TpmMarshaller>
    T[] readObjArr(Class<T> type)
    {
        // Length of the array size is always 4 bytes
        int numElems = readInt();
        @SuppressWarnings("unchecked")
        T[] arr = (T[])Array.newInstance(type, numElems);
        for (int i=0; i < numElems; ++i)
        {
            if (!isOk())
                break;
            arr[i] = createObj(type);
        }
        return arr;
    }
}
