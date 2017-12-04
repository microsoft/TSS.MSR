package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Command header [TSS]
*/
public class CommandHeader extends TpmStructure
{
    /**
     * Command header [TSS]
     * 
     * @param _Tag Command tag (sessions, or no sessions) 
     * @param _CommandSize Total command buffer length 
     * @param _CommandCode Command code
     */
    public CommandHeader(TPM_ST _Tag,int _CommandSize,TPM_CC _CommandCode)
    {
        Tag = _Tag;
        CommandSize = _CommandSize;
        CommandCode = _CommandCode;
    }
    /**
    * Command header [TSS]
    */
    public CommandHeader() {};
    /**
    * Command tag (sessions, or no sessions)
    */
    public TPM_ST Tag;
    /**
    * Total command buffer length
    */
    public int CommandSize;
    /**
    * Command code
    */
    public TPM_CC CommandCode;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        Tag.toTpm(buf);
        buf.write(CommandSize);
        CommandCode.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        Tag = TPM_ST.fromTpm(buf);
        CommandSize =  buf.readInt(4);
        CommandCode = TPM_CC.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static CommandHeader fromTpm (byte[] x) 
    {
        CommandHeader ret = new CommandHeader();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static CommandHeader fromTpm (InByteBuf buf) 
    {
        CommandHeader ret = new CommandHeader();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("CommandHeader");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ST", "Tag", Tag);
        _p.add(d, "uint", "CommandSize", CommandSize);
        _p.add(d, "TPM_CC", "CommandCode", CommandCode);
    };
    
    
};

//<<<

