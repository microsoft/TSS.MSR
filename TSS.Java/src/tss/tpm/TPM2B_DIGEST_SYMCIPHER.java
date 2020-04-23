package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Auto-derived from TPM2B_DIGEST to provide unique GetUnionSelector() implementation */
public class TPM2B_DIGEST_SYMCIPHER extends TPM2B_DIGEST
{
    public TPM2B_DIGEST_SYMCIPHER() {}
    
    /** @param _buffer the buffer area that can be no larger than a digest */
    public TPM2B_DIGEST_SYMCIPHER(byte[] _buffer) { super(_buffer); }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_DIGEST_SYMCIPHER");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

