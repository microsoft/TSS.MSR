package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure allows the TPM to keep track of the data and permissions to manipulate an NV Index.
*/
public final class TPMA_NV extends TpmAttribute<TPMA_NV>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPMA_NV. qualifier.
    public enum _N {
        /**
        * SET (1): The Index data can be written if Platform Authorization is provided. CLEAR (0): Writing of the Index data cannot be authorized with Platform Authorization.
        */
        PPWRITE,
        /**
        * SET (1): The Index data can be written if Owner Authorization is provided. CLEAR (0): Writing of the Index data cannot be authorized with Owner Authorization.
        */
        OWNERWRITE,
        /**
        * SET (1): Authorizations to change the Index contents that require USER role may be provided with an HMAC session or password. CLEAR (0): Authorizations to change the Index contents that require USER role may not be provided with an HMAC session or password.
        */
        AUTHWRITE,
        /**
        * SET (1): Authorizations to change the Index contents that require USER role may be provided with a policy session. CLEAR (0): Authorizations to change the Index contents that require USER role may not be provided with a policy session. NOTE TPM2_NV_ChangeAuth() always requires that authorization be provided in a policy session.
        */
        POLICYWRITE,
        /**
        * Ordinary contains data that is opaque to the TPM that can only be modified using TPM2_NV_Write().
        */
        ORDINARY,
        /**
        * Counter contains an 8-octet value that is to be used as a counter and can only be modified with TPM2_NV_Increment()
        */
        COUNTER,
        /**
        * Bit Field contains an 8-octet value to be used as a bit field and can only be modified with TPM2_NV_SetBits().
        */
        BITS,
        /**
        * Extend contains a digest-sized value used like a PCR. The Index can only be modified using TPM2_NV_Extend(). The extend will use the nameAlg of the Index.
        */
        EXTEND,
        /**
        * PIN Fail - contains pinCount that increments on a PIN authorization failure and a pinLimit
        */
        PIN_FAIL,
        /**
        * PIN Pass - contains pinCount that increments on a PIN authorization success and a pinLimit
        */
        PIN_PASS,
        /**
        * The type of the index. NOTE A TPM is not required to support all TPM_NT values
        */
        TpmNt_BIT_0,
        TpmNt_BIT_1,
        TpmNt_BIT_2,
        TpmNt_BIT_3,
        /**
        * SET (1): Index may not be deleted unless the authPolicy is satisfied using TPM2_NV_UndefineSpaceSpecial(). CLEAR (0): Index may be deleted with proper platform or owner authorization using TPM2_NV_UndefineSpace(). NOTE An Index with this attribute and a policy that cannot be satisfied (e.g., an Empty Policy) cannot be deleted.
        */
        POLICY_DELETE,
        /**
        * SET (1): Index cannot be written. CLEAR (0): Index can be written.
        */
        WRITELOCKED,
        /**
        * SET (1): A partial write of the Index data is not allowed. The write size shall match the defined space size. CLEAR (0): Partial writes are allowed. This setting is required if the .dataSize of the Index is larger than NV_MAX_BUFFER_SIZE for the implementation.
        */
        WRITEALL,
        /**
        * SET (1): TPM2_NV_WriteLock() may be used to prevent further writes to this location. CLEAR (0): TPM2_NV_WriteLock() does not block subsequent writes if TPMA_NV_WRITE_STCLEAR is also CLEAR.
        */
        WRITEDEFINE,
        /**
        * SET (1): TPM2_NV_WriteLock() may be used to prevent further writes to this location until the next TPM Reset or TPM Restart. CLEAR (0): TPM2_NV_WriteLock() does not block subsequent writes if TPMA_NV_WRITEDEFINE is also CLEAR.
        */
        WRITE_STCLEAR,
        /**
        * SET (1): If TPM2_NV_GlobalWriteLock() is successful, then further writes to this location are not permitted until the next TPM Reset or TPM Restart. CLEAR (0): TPM2_NV_GlobalWriteLock() has no effect on the writing of the data at this Index.
        */
        GLOBALLOCK,
        /**
        * SET (1): The Index data can be read if Platform Authorization is provided. CLEAR (0): Reading of the Index data cannot be authorized with Platform Authorization.
        */
        PPREAD,
        /**
        * SET (1): The Index data can be read if Owner Authorization is provided. CLEAR (0): Reading of the Index data cannot be authorized with Owner Authorization.
        */
        OWNERREAD,
        /**
        * SET (1): The Index data may be read if the authValue is provided. CLEAR (0): Reading of the Index data cannot be authorized with the Index authValue.
        */
        AUTHREAD,
        /**
        * SET (1): The Index data may be read if the authPolicy is satisfied. CLEAR (0): Reading of the Index data cannot be authorized with the Index authPolicy.
        */
        POLICYREAD,
        /**
        * SET (1): Authorization failures of the Index do not affect the DA logic and authorization of the Index is not blocked when the TPM is in Lockout mode. CLEAR (0): Authorization failures of the Index will increment the authorization failure counter and authorizations of this Index are not allowed when the TPM is in Lockout mode.
        */
        NO_DA,
        /**
        * SET (1): NV Index state is only required to be saved when the TPM performs an orderly shutdown (TPM2_Shutdown()). CLEAR (0): NV Index state is required to be persistent after the command to update the Index completes successfully (that is, the NV update is synchronous with the update command). NOTE If TPMA_NV_ORDERLY is SET, TPMA_NV_WRITTEN will be CLEAR by TPM Reset.
        */
        ORDERLY,
        /**
        * SET (1): TPMA_NV_WRITTEN for the Index is CLEAR by TPM Reset or TPM Restart. CLEAR (0): TPMA_NV_WRITTEN is not changed by TPM Restart. NOTE This attribute may only be SET if TPM_NT is not TPM_NT_COUNTER.
        */
        CLEAR_STCLEAR,
        /**
        * SET (1): Reads of the Index are blocked until the next TPM Reset or TPM Restart. CLEAR (0): Reads of the Index are allowed if proper authorization is provided.
        */
        READLOCKED,
        /**
        * SET (1): Index has been written. CLEAR (0): Index has not been written.
        */
        WRITTEN,
        /**
        * SET (1): This Index may be undefined with Platform Authorization but not with Owner Authorization. CLEAR (0): This Index may be undefined using Owner Authorization but not with Platform Authorization. The TPM will validate that this attribute is SET when the Index is defined using Platform Authorization and will validate that this attribute is CLEAR when the Index is defined using Owner Authorization.
        */
        PLATFORMCREATE,
        /**
        * SET (1): TPM2_NV_ReadLock() may be used to SET TPMA_NV_READLOCKED for this Index. CLEAR (0): TPM2_NV_ReadLock() has no effect on this Index.
        */
        READ_STCLEAR
    }
    
    private static ValueMap<TPMA_NV>	_ValueMap = new ValueMap<TPMA_NV>();
    
    public static final TPMA_NV
    
        PPWRITE = new TPMA_NV(0x1, _N.PPWRITE),
        OWNERWRITE = new TPMA_NV(0x2, _N.OWNERWRITE),
        AUTHWRITE = new TPMA_NV(0x4, _N.AUTHWRITE),
        POLICYWRITE = new TPMA_NV(0x8, _N.POLICYWRITE),
        ORDINARY = new TPMA_NV(0x0, _N.ORDINARY),
        COUNTER = new TPMA_NV(0x10, _N.COUNTER),
        BITS = new TPMA_NV(0x20, _N.BITS),
        EXTEND = new TPMA_NV(0x40, _N.EXTEND),
        PIN_FAIL = new TPMA_NV(0x80, _N.PIN_FAIL),
        PIN_PASS = new TPMA_NV(0x90, _N.PIN_PASS),
        TpmNt_BIT_0 = new TPMA_NV(0x10, _N.TpmNt_BIT_0, true),
        TpmNt_BIT_1 = new TPMA_NV(0x20, _N.TpmNt_BIT_1, true),
        TpmNt_BIT_2 = new TPMA_NV(0x40, _N.TpmNt_BIT_2, true),
        TpmNt_BIT_3 = new TPMA_NV(0x80, _N.TpmNt_BIT_3, true),
        POLICY_DELETE = new TPMA_NV(0x400, _N.POLICY_DELETE),
        WRITELOCKED = new TPMA_NV(0x800, _N.WRITELOCKED),
        WRITEALL = new TPMA_NV(0x1000, _N.WRITEALL),
        WRITEDEFINE = new TPMA_NV(0x2000, _N.WRITEDEFINE),
        WRITE_STCLEAR = new TPMA_NV(0x4000, _N.WRITE_STCLEAR),
        GLOBALLOCK = new TPMA_NV(0x8000, _N.GLOBALLOCK),
        PPREAD = new TPMA_NV(0x10000, _N.PPREAD),
        OWNERREAD = new TPMA_NV(0x20000, _N.OWNERREAD),
        AUTHREAD = new TPMA_NV(0x40000, _N.AUTHREAD),
        POLICYREAD = new TPMA_NV(0x80000, _N.POLICYREAD),
        NO_DA = new TPMA_NV(0x2000000, _N.NO_DA),
        ORDERLY = new TPMA_NV(0x4000000, _N.ORDERLY),
        CLEAR_STCLEAR = new TPMA_NV(0x8000000, _N.CLEAR_STCLEAR),
        READLOCKED = new TPMA_NV(0x10000000, _N.READLOCKED),
        WRITTEN = new TPMA_NV(0x20000000, _N.WRITTEN),
        PLATFORMCREATE = new TPMA_NV(0x40000000, _N.PLATFORMCREATE),
        READ_STCLEAR = new TPMA_NV(0x80000000, _N.READ_STCLEAR);
    public TPMA_NV (int value) { super(value, _ValueMap); }
    
    public TPMA_NV (TPMA_NV...attrs) { super(_ValueMap, attrs); }
    
    public static TPMA_NV fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPMA_NV.class); }
    
    public static TPMA_NV fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_NV.class); }
    
    public static TPMA_NV fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPMA_NV.class); }
    
    public TPMA_NV._N asEnum() { return (TPMA_NV._N)NameAsEnum; }
    
    public static Collection<TPMA_NV> values() { return _ValueMap.values(); }
    
    public boolean hasAttr (TPMA_NV attr) { return super.hasAttr(attr); }
    
    public TPMA_NV maskAttr (TPMA_NV attr) { return super.maskAttr(attr, _ValueMap, TPMA_NV.class); }
    
    private TPMA_NV (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPMA_NV (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<

