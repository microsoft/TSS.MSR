package tss;

public interface TpmMarshaller {
	/**
	 * Convert this object to its TPM representation and return it as a byte array
	 * @return Byte buffer with the TPM 2.0 compatible wire representation of the object
	 */
	public byte[] toTpm();
	
	/**
	 * Convert this object to its TPM representation and store in the output byte buffer object
	 * 
	 * @param buf An output byte buffer
	 */
	public void toTpm(OutByteBuf buf) ;
	
	/**
	 * Populate this object from the TPM representation in the input byte buffer object
	 * 
	 * @param buf An input byte buffer
	 */
	public void initFromTpm(InByteBuf buf);
}
