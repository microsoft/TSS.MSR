package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* TPM Hash structure
*/
public class TPMT_HA extends TpmStructure
{
    /**
     * TPM Hash structure
     * 
     * @param _hashAlg Algorithm 
     * @param _digest Hash value
     */
    public TPMT_HA(TPM_ALG_ID _hashAlg,byte[] _digest)
    {
        hashAlg = _hashAlg;
        digest = _digest;
    }
    /**
    * TPM Hash structure
    */
    public TPMT_HA() {};
    /**
    * Algorithm
    */
    public TPM_ALG_ID hashAlg;
    /**
    * Hash value
    */
    public byte[] digest;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        hashAlg.toTpm(buf);
        buf.write(digest);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        hashAlg = TPM_ALG_ID.fromTpm(buf);
        digest = new byte[Crypto.digestSize(hashAlg)];
        buf.readArrayOfInts(digest, 1, digest.length);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMT_HA fromTpm (byte[] x) 
    {
        TPMT_HA ret = new TPMT_HA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMT_HA fromTpm (InByteBuf buf) 
    {
        TPMT_HA ret = new TPMT_HA();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_HA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
        _p.add(d, "byte", "digest", digest);
    };
    
    
    /**
    * Create a TPMT_HA from the hash of data
    * 
    * @param hashAlg The hash algorithm
    * @param data The data to hash
    * @return A new TPMT_HA
    */
    public static TPMT_HA fromHashOf(TPM_ALG_ID hashAlg, byte[] data)
    {
    	return new TPMT_HA(hashAlg, Crypto.hash(hashAlg, data));
    }
    
    
    
    /**
    * Create a TPMT_HA from the hash of a UTF8 encoded string 
    * 
    * @param hashAlg The hash algorithm
    * @param s The string to hash
    * @return A new TPMT_HA
    */
    public static TPMT_HA fromHashOf(TPM_ALG_ID hashAlg, String s)
    {
    	try {
    		byte[] buf = s.getBytes("UTF8");
    		return TPMT_HA.fromHashOf(hashAlg, buf);
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		throw new RuntimeException("unexpected error");
    	} 
    
    }
    
    /**
    * Perform a TPM Extend operation on the contents of this TPMT_HA 
    * 
    * @param x The data to extend
    * @return The same object (to allow chaining)
    */
    public TPMT_HA extend(byte[] x)
    {
        byte[] t = Helpers.concatenate(digest, x);
        digest = Crypto.hash(hashAlg, t);
        return this;
    }
    
    /**
    * Perform a TPM Event operation on the contents of this TPMT_HA 
    * 
    * @param x The data to event
    * @return The same object (to allow chaining)
    */
    public TPMT_HA event(byte[] x)
    {
        byte[] s = Crypto.hash(hashAlg, x);
        byte[] t = Helpers.concatenate(digest, s);
        digest = Crypto.hash(hashAlg, t);
        return this;
    }
    
    /**
    * Reset the contents of this hash object to all zeros 
    * 
    */
    public void reset()
    {
    	digest = new byte[Crypto.digestSize(hashAlg)];
    }
    
    /**
    * Create an all zero hash object 
    * 
    * @param alg The hash algorithm to use
    * @return The new zero TPMT_HA
    */
    public static TPMT_HA zeroHash(TPM_ALG_ID alg)
    {
    	return new TPMT_HA(alg, new byte[Crypto.digestSize(alg)]);
    }
    
    
};

//<<<

