
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
    
    public void toTpm(OutByteBuf buf) {}
    
    public void initFromTpm(InByteBuf buf) {}
    
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) 
            return true;
        else if (obj == null) 
            return false;
        else if (obj instanceof TpmMarshaller) 
        {
            TpmMarshaller b = (TpmMarshaller) obj;
            byte[] thisObject = ((TpmMarshaller) this).toTpm();
            byte[] thatObject = b.toTpm();
            return Helpers.arraysAreEqual(thisObject,  thatObject);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return toTpm().hashCode();
    }
}
