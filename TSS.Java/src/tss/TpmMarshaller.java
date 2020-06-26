package tss;

public interface TpmMarshaller
{
    /** Convert this object to its TPM representation and store in the output byte buffer object
     * @param buf An output byte buffer
     */
    public void toTpm(TpmBuffer buf);
    
    /** Populate this object from the TPM representation in the input byte buffer object
     * @param buf An input byte buffer
     */
    public void initFromTpm(TpmBuffer buf);
}
