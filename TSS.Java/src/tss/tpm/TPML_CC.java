package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* A list of command codes may be input to the TPM or returned by the TPM depending on the command.
*/
public class TPML_CC extends TpmStructure implements TPMU_CAPABILITIES 
{
    /**
     * A list of command codes may be input to the TPM or returned by the TPM depending on the command.
     * 
     * @param _commandCodes a list of command codes The maximum only applies to a command code list in a command. The response size is limited only by the size of the parameter buffer.
     */
    public TPML_CC(TPM_CC[] _commandCodes)
    {
        commandCodes = _commandCodes;
    }
    /**
    * A list of command codes may be input to the TPM or returned by the TPM depending on the command.
    */
    public TPML_CC() {};
    /**
    * number of commands in the commandCode list; may be 0
    */
    // private int count;
    /**
    * a list of command codes The maximum only applies to a command code list in a command. The response size is limited only by the size of the parameter buffer.
    */
    public TPM_CC[] commandCodes;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((commandCodes!=null)?commandCodes.length:0, 4);
        if(commandCodes!=null)
            buf.writeArrayOfTpmObjects(commandCodes);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _count = buf.readInt(4);
        commandCodes = new TPM_CC[_count];
        for(int j=0;j<_count;j++){commandCodes[j]=TPM_CC.fromTpm(buf);};
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPML_CC fromTpm (byte[] x) 
    {
        TPML_CC ret = new TPML_CC();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPML_CC fromTpm (InByteBuf buf) 
    {
        TPML_CC ret = new TPML_CC();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_CC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_CC", "commandCodes", commandCodes);
    };
    
    
};

//<<<

