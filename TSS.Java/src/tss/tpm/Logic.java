package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>

/** Table 4 Defines for Logic Values  */
public final class Logic extends TpmEnum<Logic>
{
    /** Values from enum _N are only intended to be used in case labels of a switch statement
     *  using the result of this.asEnum() method as the switch condition. However, their Java
     *  names are identical to those of the constants defined in this class further below, so
     *  for any other usage just prepend them with the Logic. qualifier.
     */
    public enum _N {
        TRUE, 
        FALSE, 
        YES, 
        NO, 
        SET, 
        CLEAR
    }

    private static ValueMap<Logic> _ValueMap = new ValueMap<Logic>();

    /** These definitions provide mapping of the Java enum constants to their TPM integer values  */
    public static final Logic
        TRUE = new Logic(1, _N.TRUE),
        FALSE = new Logic(0, _N.FALSE),
        YES = new Logic(1, _N.YES),
        NO = new Logic(0, _N.NO),
        SET = new Logic(1, _N.SET),
        CLEAR = new Logic(0, _N.CLEAR);

    public Logic () { super(0, _ValueMap); }
    public Logic (int value) { super(value, _ValueMap); }
    public static Logic fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, Logic.class); }
    public static Logic fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, Logic.class); }
    public static Logic fromTpm (TpmBuffer buf) { return TpmEnum.fromTpm(buf, _ValueMap, Logic.class); }
    public Logic._N asEnum() { return (Logic._N)NameAsEnum; }
    public static Collection<Logic> values() { return _ValueMap.values(); }
    private Logic (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    private Logic (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }

    @Override
    protected int wireSize() { return 1; }
}

//<<<
