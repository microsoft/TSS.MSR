package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* The TPM_CAP values are used in TPM2_GetCapability() to select the type of the value to be returned. The format of the response varies according to the type of the value.
*/
public final class TPM_CAP extends TpmEnum<TPM_CAP>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_CAP. qualifier.
    public enum _N {
        FIRST,
        
        /**
        * TPML_ALG_PROPERTY
        */
        ALGS,
        
        /**
        * TPML_HANDLE
        */
        HANDLES,
        
        /**
        * TPML_CCA
        */
        COMMANDS,
        
        /**
        * TPML_CC
        */
        PP_COMMANDS,
        
        /**
        * TPML_CC
        */
        AUDIT_COMMANDS,
        
        /**
        * TPML_PCR_SELECTION
        */
        PCRS,
        
        /**
        * TPML_TAGGED_TPM_PROPERTY
        */
        TPM_PROPERTIES,
        
        /**
        * TPML_TAGGED_PCR_PROPERTY
        */
        PCR_PROPERTIES,
        
        /**
        * TPML_ECC_CURVE
        */
        ECC_CURVES,
        
        /**
        * TPML_TAGGED_POLICY
        */
        AUTH_POLICIES,
        
        LAST,
        
        /**
        * manufacturer-specific values
        */
        VENDOR_PROPERTY
        
    }
    
    private static ValueMap<TPM_CAP> _ValueMap = new ValueMap<TPM_CAP>();
    
    public static final TPM_CAP
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        FIRST = new TPM_CAP(0x00000000, _N.FIRST),
        ALGS = new TPM_CAP(0x00000000, _N.ALGS),
        HANDLES = new TPM_CAP(0x00000001, _N.HANDLES),
        COMMANDS = new TPM_CAP(0x00000002, _N.COMMANDS),
        PP_COMMANDS = new TPM_CAP(0x00000003, _N.PP_COMMANDS),
        AUDIT_COMMANDS = new TPM_CAP(0x00000004, _N.AUDIT_COMMANDS),
        PCRS = new TPM_CAP(0x00000005, _N.PCRS),
        TPM_PROPERTIES = new TPM_CAP(0x00000006, _N.TPM_PROPERTIES),
        PCR_PROPERTIES = new TPM_CAP(0x00000007, _N.PCR_PROPERTIES),
        ECC_CURVES = new TPM_CAP(0x00000008, _N.ECC_CURVES),
        AUTH_POLICIES = new TPM_CAP(0x00000009, _N.AUTH_POLICIES),
        LAST = new TPM_CAP(0x00000009, _N.LAST),
        VENDOR_PROPERTY = new TPM_CAP(0x00000100, _N.VENDOR_PROPERTY);
    public TPM_CAP (int value) { super(value, _ValueMap); }
    
    public static TPM_CAP fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_CAP.class); }
    
    public static TPM_CAP fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_CAP.class); }
    
    public static TPM_CAP fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_CAP.class); }
    
    public TPM_CAP._N asEnum() { return (TPM_CAP._N)NameAsEnum; }
    
    public static Collection<TPM_CAP> values() { return _ValueMap.values(); }
    
    private TPM_CAP (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_CAP (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

