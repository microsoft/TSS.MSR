package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 80 shows the basic hash-agile structure used in this specification. To handle
 *  hash agility, this structure uses the hashAlg parameter to indicate the algorithm used
 *  to compute the digest and, by implication, the size of the digest.
 */
public class TPMT_HA extends TpmStructure implements TPMU_SIGNATURE
{
    /** Selector of the hash contained in the digest that implies the size of the digest
     *  NOTE The leading + on the type indicates that this structure should pass an indication
     *  to the unmarshaling function for TPMI_ALG_HASH so that TPM_ALG_NULL will be allowed if
     *  a use of a TPMT_HA allows TPM_ALG_NULL.
     */
    public TPM_ALG_ID hashAlg;

    /** Hash value  */
    public byte[] digest;

    public TPMT_HA() { hashAlg = TPM_ALG_ID.NULL; }

    /** @param _hashAlg Selector of the hash contained in the digest that implies the size of
     *  the digest
     *         NOTE The leading + on the type indicates that this structure should pass an
     *         indication to the unmarshaling function for TPMI_ALG_HASH so that TPM_ALG_NULL will
     *         be allowed if a use of a TPMT_HA allows TPM_ALG_NULL.
     *  @param _digest Hash value
     */
    public TPMT_HA(TPM_ALG_ID _hashAlg, byte[] _digest)
    {
        hashAlg = _hashAlg;
        digest = _digest;
    }

    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.HMAC; }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        hashAlg.toTpm(buf);
        buf.writeByteBuf(digest);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        digest = buf.readByteBuf(Crypto.digestSize(hashAlg));
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMT_HA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_HA.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_HA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMT_HA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_HA.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_HA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
        _p.add(d, "byte[]", "digest", digest);
    }

    /** Create a TPMT_HA from the hash of data
     * @param hashAlg The hash algorithm
     * @param data The data to hash
     * @return A new TPMT_HA
     */
    public static TPMT_HA fromHashOf(TPM_ALG_ID hashAlg, byte[] data)
    {
        return new TPMT_HA(hashAlg, Crypto.hash(hashAlg, data));
    }

    /** Create a TPMT_HA from the hash of a UTF8 encoded string 
     * @param hashAlg The hash algorithm
     * @param s The string to hash
     * @return A new TPMT_HA
     */
    public static TPMT_HA fromHashOf(TPM_ALG_ID hashAlg, String s)
    {
        byte[] buf = s.getBytes();
        return TPMT_HA.fromHashOf(hashAlg, buf);
    }

    /** Perform a TPM Extend operation on the contents of this TPMT_HA 
     * @param x The data to extend
     * @return The same object (to allow chaining)
     */
    public TPMT_HA extend(byte[] x)
    {
        byte[] t = Helpers.concatenate(digest, x);
        digest = Crypto.hash(hashAlg, t);
        return this;
    }

    /** Perform a TPM Event operation on the contents of this TPMT_HA 
     * @param x The data to event
     * @return The same object (to allow chaining)
     */
    public TPMT_HA event(byte[] x)
    {
        byte[] s = Crypto.hash(hashAlg, x);
        byte[] t = Helpers.concatenate(digest, s);
        digest = Crypto.hash(hashAlg, t);
        return this;
    }

    /** Reset the contents of this hash object to all zeros */
    public void reset()
    {
        digest = new byte[Crypto.digestSize(hashAlg)];
    }

    /** Create an all zero hash object 
     * @param alg The hash algorithm to use
     * @return The new zero TPMT_HA
     */
    public static TPMT_HA zeroHash(TPM_ALG_ID alg)
    {
        return new TPMT_HA(alg, new byte[Crypto.digestSize(alg)]);
    }
}

//<<<
