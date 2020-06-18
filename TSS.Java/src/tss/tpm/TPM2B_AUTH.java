package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used for an authorization value and limits an authValue to being no
 *  larger than the largest digest produced by a TPM. In order to ensure consistency
 *  within an object, the authValue may be no larger than the size of the digest produced
 *  by the objects nameAlg. This ensures that any TPM that can load the object will be
 *  able to handle the authValue of the object.
 */
public class TPM2B_AUTH extends TPM2B_DIGEST
{
    public TPM2B_AUTH() {}
    
    /** @param _buffer The buffer area that can be no larger than a digest  */
    public TPM2B_AUTH(byte[] _buffer) { super(_buffer); }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_AUTH");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<
