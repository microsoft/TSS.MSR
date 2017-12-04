package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command supports two-phase key exchange protocols. The command is used in combination with TPM2_EC_Ephemeral(). TPM2_EC_Ephemeral() generates an ephemeral key and returns the public point of that ephemeral key along with a numeric value that allows the TPM to regenerate the associated private key.
*/
public class TPM2_ZGen_2Phase_REQUEST extends TpmStructure
{
    /**
     * This command supports two-phase key exchange protocols. The command is used in combination with TPM2_EC_Ephemeral(). TPM2_EC_Ephemeral() generates an ephemeral key and returns the public point of that ephemeral key along with a numeric value that allows the TPM to regenerate the associated private key.
     * 
     * @param _keyA handle of an unrestricted decryption key ECC The private key referenced by this handle is used as dS,A Auth Index: 1 Auth Role: USER 
     * @param _inQsB other partys static public key (Qs,B = (Xs,B, Ys,B)) 
     * @param _inQeB other party's ephemeral public key (Qe,B = (Xe,B, Ye,B)) 
     * @param _inScheme the key exchange scheme 
     * @param _counter value returned by TPM2_EC_Ephemeral()
     */
    public TPM2_ZGen_2Phase_REQUEST(TPM_HANDLE _keyA,TPMS_ECC_POINT _inQsB,TPMS_ECC_POINT _inQeB,TPM_ALG_ID _inScheme,int _counter)
    {
        keyA = _keyA;
        inQsB = _inQsB;
        inQeB = _inQeB;
        inScheme = _inScheme;
        counter = (short)_counter;
    }
    /**
    * This command supports two-phase key exchange protocols. The command is used in combination with TPM2_EC_Ephemeral(). TPM2_EC_Ephemeral() generates an ephemeral key and returns the public point of that ephemeral key along with a numeric value that allows the TPM to regenerate the associated private key.
    */
    public TPM2_ZGen_2Phase_REQUEST() {};
    /**
    * handle of an unrestricted decryption key ECC The private key referenced by this handle is used as dS,A Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE keyA;
    /**
    * size of the remainder of this structure
    */
    // private short inQsBSize;
    /**
    * other partys static public key (Qs,B = (Xs,B, Ys,B))
    */
    public TPMS_ECC_POINT inQsB;
    /**
    * size of the remainder of this structure
    */
    // private short inQeBSize;
    /**
    * other party's ephemeral public key (Qe,B = (Xe,B, Ye,B))
    */
    public TPMS_ECC_POINT inQeB;
    /**
    * the key exchange scheme
    */
    public TPM_ALG_ID inScheme;
    /**
    * value returned by TPM2_EC_Ephemeral()
    */
    public short counter;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        keyA.toTpm(buf);
        buf.writeInt((inQsB!=null)?inQsB.toTpm().length:0, 2);
        if(inQsB!=null)
            inQsB.toTpm(buf);
        buf.writeInt((inQeB!=null)?inQeB.toTpm().length:0, 2);
        if(inQeB!=null)
            inQeB.toTpm(buf);
        inScheme.toTpm(buf);
        buf.write(counter);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        keyA = TPM_HANDLE.fromTpm(buf);
        int _inQsBSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inQsBSize));
        inQsB = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        int _inQeBSize = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _inQeBSize));
        inQeB = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        inScheme = TPM_ALG_ID.fromTpm(buf);
        counter = (short) buf.readInt(2);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ZGen_2Phase_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ZGen_2Phase_REQUEST ret = new TPM2_ZGen_2Phase_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ZGen_2Phase_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ZGen_2Phase_REQUEST ret = new TPM2_ZGen_2Phase_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ZGen_2Phase_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyA", keyA);
        _p.add(d, "TPMS_ECC_POINT", "inQsB", inQsB);
        _p.add(d, "TPMS_ECC_POINT", "inQeB", inQeB);
        _p.add(d, "TPM_ALG_ID", "inScheme", inScheme);
        _p.add(d, "ushort", "counter", counter);
    };
    
    
};

//<<<

