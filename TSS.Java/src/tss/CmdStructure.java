package tss;

/**
 *  Base class for custom (not TPM 2.0 spec defined) auto-generated classes
 *  representing a TPM command or response parameters and handles, if any.
 *  
 *  These data structures differ from the spec-defined ones derived directly
 *  from the TpmStructure class in that their handle fields are not marshaled
 *  by their toTpm() and initFrom() methods, but rather are acceesed and
 *  manipulated via an interface defined by this structs and its derivatives
 *  ReqStructure and RespStructure.
 */
public class CmdStructure extends TpmStructure
{
    /** @return Number of TPM handles contained (as fields) in this data structure */
    public int numHandles() { return 0; }

    /** @return Non-zero size info of the encryptable command/response parameter if 
     *          session based encryption can be applied to this object (i.e. its first
     *          non-handle field is marshaled in size-prefixed form). Otherwise returns
     *          zero initialized struct. */
    public SessEncInfo sessEncInfo() { return new SessEncInfo(0, 0); }
}
