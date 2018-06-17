package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* The platform values in Table 25 are used for the TPM_PT_PS_FAMILY_INDICATOR.
*/
public final class TPM_PS extends TpmEnum<TPM_PS>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_PS. qualifier.
    public enum _N {
        /**
        * not platform specific
        */
        MAIN,
        
        /**
        * PC Client
        */
        PC,
        
        /**
        * PDA (includes all mobile devices that are not specifically cell phones)
        */
        PDA,
        
        /**
        * Cell Phone
        */
        CELL_PHONE,
        
        /**
        * Server WG
        */
        SERVER,
        
        /**
        * Peripheral WG
        */
        PERIPHERAL,
        
        /**
        * TSS WG (deprecated)
        */
        TSS,
        
        /**
        * Storage WG
        */
        STORAGE,
        
        /**
        * Authentication WG
        */
        AUTHENTICATION,
        
        /**
        * Embedded WG
        */
        EMBEDDED,
        
        /**
        * Hardcopy WG
        */
        HARDCOPY,
        
        /**
        * Infrastructure WG (deprecated)
        */
        INFRASTRUCTURE,
        
        /**
        * Virtualization WG
        */
        VIRTUALIZATION,
        
        /**
        * Trusted Network Connect WG (deprecated)
        */
        TNC,
        
        /**
        * Multi-tenant WG (deprecated)
        */
        MULTI_TENANT,
        
        /**
        * Technical Committee (deprecated)
        */
        TC
        
    }
    
    private static ValueMap<TPM_PS> _ValueMap = new ValueMap<TPM_PS>();
    
    public static final TPM_PS
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        MAIN = new TPM_PS(0x00000000, _N.MAIN),
        PC = new TPM_PS(0x00000001, _N.PC),
        PDA = new TPM_PS(0x00000002, _N.PDA),
        CELL_PHONE = new TPM_PS(0x00000003, _N.CELL_PHONE),
        SERVER = new TPM_PS(0x00000004, _N.SERVER),
        PERIPHERAL = new TPM_PS(0x00000005, _N.PERIPHERAL),
        TSS = new TPM_PS(0x00000006, _N.TSS),
        STORAGE = new TPM_PS(0x00000007, _N.STORAGE),
        AUTHENTICATION = new TPM_PS(0x00000008, _N.AUTHENTICATION),
        EMBEDDED = new TPM_PS(0x00000009, _N.EMBEDDED),
        HARDCOPY = new TPM_PS(0x0000000A, _N.HARDCOPY),
        INFRASTRUCTURE = new TPM_PS(0x0000000B, _N.INFRASTRUCTURE),
        VIRTUALIZATION = new TPM_PS(0x0000000C, _N.VIRTUALIZATION),
        TNC = new TPM_PS(0x0000000D, _N.TNC),
        MULTI_TENANT = new TPM_PS(0x0000000E, _N.MULTI_TENANT),
        TC = new TPM_PS(0x0000000F, _N.TC);
    public TPM_PS (int value) { super(value, _ValueMap); }
    
    public static TPM_PS fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_PS.class); }
    
    public static TPM_PS fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_PS.class); }
    
    public static TPM_PS fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_PS.class); }
    
    public TPM_PS._N asEnum() { return (TPM_PS._N)NameAsEnum; }
    
    public static Collection<TPM_PS> values() { return _ValueMap.values(); }
    
    private TPM_PS (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_PS (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

