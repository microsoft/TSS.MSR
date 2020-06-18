package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to prove the association between an object and its creation data.
 *  The TPM will validate that the ticket was produced by the TPM and that the ticket
 *  validates the association between a loaded public area and the provided hash of the
 *  creation data (creationHash).
 */
public class CertifyCreationResponse extends TpmStructure
{
    /** The structure that was signed  */
    public TPMS_ATTEST certifyInfo;
    
    /** Selector of the algorithm used to construct the signature  */
    public TPM_ALG_ID signatureSigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** The signature over certifyInfo  */
    public TPMU_SIGNATURE signature;
    
    public CertifyCreationResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeShort(certifyInfo != null ? certifyInfo.toTpm().length : 0);
        if (certifyInfo != null)
            certifyInfo.toTpm(buf);
        signature.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)signature).toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _certifyInfoSize = buf.readShort() & 0xFFFF;
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _certifyInfoSize));
        certifyInfo = TPMS_ATTEST.fromTpm(buf);
        buf.structSize.pop();
        int _signatureSigAlg = buf.readShort() & 0xFFFF;
        signature = UnionFactory.create("TPMU_SIGNATURE", new TPM_ALG_ID(_signatureSigAlg));
        signature.initFromTpm(buf);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static CertifyCreationResponse fromBytes (byte[] byteBuf) 
    {
        CertifyCreationResponse ret = new CertifyCreationResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static CertifyCreationResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static CertifyCreationResponse fromTpm (InByteBuf buf) 
    {
        CertifyCreationResponse ret = new CertifyCreationResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CertifyCreation_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ATTEST", "certifyInfo", certifyInfo);
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    }
}

//<<<
