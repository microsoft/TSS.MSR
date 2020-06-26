
package tss;

// import tss.tpm.*;

public abstract class TpmStructure implements TpmMarshaller {
    
    /**
     * Serialize this object to the structure printer
     * 
     * @param _p The structure accumulator
     * @param d The data to serialize
     */
    public void toStringInternal(TpmStructurePrinter _p, int d) {}
    
    @Override
    public void toTpm(TpmBuffer buf) {}
    
    @Override
    public void initFromTpm(TpmBuffer buf) {}
    
    public byte[] toBytes()
    {
        TpmBuffer buf = new TpmBuffer();
        toTpm(buf);
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
