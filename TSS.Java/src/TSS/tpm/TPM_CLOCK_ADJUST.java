package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* A TPM_CLOCK_ADJUST value is used to change the rate at which the TPM internal oscillator is divided. A change to the divider will change the rate at which Clock and Time change.
*/
public final class TPM_CLOCK_ADJUST extends TpmEnum<TPM_CLOCK_ADJUST>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_CLOCK_ADJUST. qualifier.
    public enum _N {
        /**
        * Slow the Clock update rate by one coarse adjustment step.
        */
        COARSE_SLOWER,
        
        /**
        * Slow the Clock update rate by one medium adjustment step.
        */
        MEDIUM_SLOWER,
        
        /**
        * Slow the Clock update rate by one fine adjustment step.
        */
        FINE_SLOWER,
        
        /**
        * No change to the Clock update rate.
        */
        NO_CHANGE,
        
        /**
        * Speed the Clock update rate by one fine adjustment step.
        */
        FINE_FASTER,
        
        /**
        * Speed the Clock update rate by one medium adjustment step.
        */
        MEDIUM_FASTER,
        
        /**
        * Speed the Clock update rate by one coarse adjustment step.
        */
        COARSE_FASTER
        
    }
    
    private static ValueMap<TPM_CLOCK_ADJUST> _ValueMap = new ValueMap<TPM_CLOCK_ADJUST>();
    
    public static final TPM_CLOCK_ADJUST
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        COARSE_SLOWER = new TPM_CLOCK_ADJUST(-3, _N.COARSE_SLOWER),
        MEDIUM_SLOWER = new TPM_CLOCK_ADJUST(-2, _N.MEDIUM_SLOWER),
        FINE_SLOWER = new TPM_CLOCK_ADJUST(-1, _N.FINE_SLOWER),
        NO_CHANGE = new TPM_CLOCK_ADJUST(0, _N.NO_CHANGE),
        FINE_FASTER = new TPM_CLOCK_ADJUST(1, _N.FINE_FASTER),
        MEDIUM_FASTER = new TPM_CLOCK_ADJUST(2, _N.MEDIUM_FASTER),
        COARSE_FASTER = new TPM_CLOCK_ADJUST(3, _N.COARSE_FASTER);
    public TPM_CLOCK_ADJUST (int value) { super(value, _ValueMap); }
    
    public static TPM_CLOCK_ADJUST fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_CLOCK_ADJUST.class); }
    
    public static TPM_CLOCK_ADJUST fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_CLOCK_ADJUST.class); }
    
    public static TPM_CLOCK_ADJUST fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_CLOCK_ADJUST.class); }
    
    public TPM_CLOCK_ADJUST._N asEnum() { return (TPM_CLOCK_ADJUST._N)NameAsEnum; }
    
    public static Collection<TPM_CLOCK_ADJUST> values() { return _ValueMap.values(); }
    
    private TPM_CLOCK_ADJUST (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_CLOCK_ADJUST (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 1; }
}

//<<<

