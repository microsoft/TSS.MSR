
package tss;

// import tss.tpm.*;

public class TpmStructure implements TpmMarshaller {
    
    /** Serialize this object to the structure printer
     * 
     * @param _p The structure accumulator
     * @param d The data to serialize
     */
    public void toStringInternal(TpmStructurePrinter _p, int d) {}
    
    /** ISerializable method
     * @return Human readable type name for the purposes of text serialization and pretty-printing
     */
    String typeName () { return "TpmStructure"; }

    @Override
    public void toTpm(TpmBuffer buf) {}
    
    @Override
    public void initFromTpm(TpmBuffer buf) {}
    
    /** @return TPM binary representation of this object. */
    public byte[] toBytes()
    {
        TpmBuffer buf = new TpmBuffer();
        toTpm(buf);
        return buf.trim();
    }

    /** Initializes this object from a TPM binary representation in the given byte buffer
     * @return The TPM binary representation of this object.
     */
    void initFromBytes(final byte[] buffer)
    {
        TpmBuffer buf = new TpmBuffer(buffer);
        initFromTpm(buf);
        assert(buf.curPos() == buffer.length);
    }

    /** @return 2B size-prefixed TPM binary representation of this object. */
    byte[] asTpm2B()
    {
        TpmBuffer buf = new TpmBuffer();
        buf.writeSizedObj(this);
        return buf.trim();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) 
            return true;
        else if (obj == null) 
            return false;
        else if (obj instanceof TpmStructure) 
        {
            byte[] thisObject = this.toBytes();
            byte[] thatObject = ((TpmStructure)obj).toBytes();
            return Helpers.arraysAreEqual(thisObject,  thatObject);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return toBytes().hashCode();
    }
}
