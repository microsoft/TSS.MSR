package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>

/** This attribute is used to report the ACT state. This attribute may be read using
 *  TPM2_GetCapability(capability = TPM_CAP_ACT, property = TPM_RH_ACT_x where x is the
 *  ACT number (0-F)). The signaled value must be preserved across TPM Resume or if the
 *  TPM has not lost power. The signaled value may be preserved over a power cycle of a TPM.
 */
public final class TPMA_ACT extends TpmAttribute<TPMA_ACT>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_ACT. qualifier.
    public enum _N {
        /** SET (1): The ACT has signaled
         *  CLEAR (0): The ACT has not signaled
         */
        signaled,
        
        /** Preserves the state of signaled, depending on the power cycle  */
        preserveSignaled
    }
    
    private static ValueMap<TPMA_ACT> _ValueMap = new ValueMap<TPMA_ACT>();
    
    /** These definitions provide mapping of the Java enum constants to their TPM integer values  */
    public static final TPMA_ACT
        signaled = new TPMA_ACT(0x1, _N.signaled),
        preserveSignaled = new TPMA_ACT(0x2, _N.preserveSignaled);
    
    public TPMA_ACT () { super(0, _ValueMap); }
    
    public TPMA_ACT (int value) { super(value, _ValueMap); }
    
    public TPMA_ACT (TPMA_ACT...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_ACT fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_ACT.class); }
    
    public static TPMA_ACT fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_ACT.class); }
    
    public static TPMA_ACT fromTpm (TpmBuffer buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_ACT.class); }
    
    public TPMA_ACT._N asEnum() { return (TPMA_ACT._N)NameAsEnum; }
    
    public static Collection<TPMA_ACT> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_ACT attr) { return super.hasAttr(attr); }
    
    public TPMA_ACT maskAttr (TPMA_ACT attr) { return super.maskAttr(attr, _ValueMap, TPMA_ACT.class); }
    
    private TPMA_ACT (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_ACT (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<
