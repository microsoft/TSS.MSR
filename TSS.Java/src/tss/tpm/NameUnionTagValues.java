package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* Selector type for TPMU_NAME [TSS]
*/
public final class NameUnionTagValues extends TpmEnum<NameUnionTagValues>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the NameUnionTagValues. qualifier.
    public enum _N {
        TAG_TPMU_NAME_TPMT_HA,
        
        TAG_TPMU_NAME_TPM_HANDLE
        
    }
    
    private static ValueMap<NameUnionTagValues> _ValueMap = new ValueMap<NameUnionTagValues>();
    
    public static final NameUnionTagValues
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        TAG_TPMU_NAME_TPMT_HA = new NameUnionTagValues(0, _N.TAG_TPMU_NAME_TPMT_HA),
        TAG_TPMU_NAME_TPM_HANDLE = new NameUnionTagValues(1, _N.TAG_TPMU_NAME_TPM_HANDLE);
    public NameUnionTagValues (int value) { super(value, _ValueMap); }
    
    public static NameUnionTagValues fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, NameUnionTagValues.class); }
    
    public static NameUnionTagValues fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, NameUnionTagValues.class); }
    
    public static NameUnionTagValues fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, NameUnionTagValues.class); }
    
    public NameUnionTagValues._N asEnum() { return (NameUnionTagValues._N)NameAsEnum; }
    
    public static Collection<NameUnionTagValues> values() { return _ValueMap.values(); }
    
    private NameUnionTagValues (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private NameUnionTagValues (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 1; }
}

//<<<

