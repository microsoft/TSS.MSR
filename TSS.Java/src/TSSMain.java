import java.util.Arrays;

import TSS.*;
import TSS.TpmTypes.*;

public class TSSMain 
{
	public enum TestEnum
	{
		TestVal1,
		TestVal2
	}
	
	public static void main(String[] args) 
	{

		TPM_RC rc = TPM_RC.VALUE;
		TPM_RC[] rcs = {TPM_RC.SIZE, TPM_RC.HANDLE};
		
		System.out.println("rc(asString) = " + rc + "; rc(short) = " + rc.name() + "; rc(asStringVerbose) = " + rc.toStringVerbose());
		System.out.println("rcs = " + Arrays.toString(rcs));
/*		
		String actBlobString = "00340020c848ddfe4577db6fad0fa1c0ddf92847daeb9a3761c4a3216f71b23ed2e1d354192ee115fd160549575853eda974008635f00100541a6e93fd652cce88418113680d17b256db053108dc9160c29a25cfba4492363d004f37ec11147e2188887a420558ce36a7f8f0e5a9cbfc569553f65f59b5ecd71b0272ce302d08839d7592c420786e88f391b3c53b898f22a80604493b2e5d1b1f8c20c3ce39267d8ef39cbec22503cda236d13c87c1be714504b8f2a80a6d3956ab06014ceb88a1fb7b68dba667cf141a578b7e2f39f2816a2ae982db07bdffc76ea85a9b98290b11a6b755daf37b63ae3b83e34d67ff8aa37d5d4283d2ab4e6276a04b3dba983be2d04cee3ef3647b8b1ca3a0a3e71e4f8a0ba602c2a06db52acda1a2a702b8680b3ee59d02da6405d56735d585b43bf0f19c5d8294a434008e0020d8e444f9c827dcd4b91487bb9623a673554ab5b6fc6fae1bb2386e813fcf6b1dbea5e8adaec1a9a61d3c52a7a9736b61acaadb0fc4edfbd2a50424e424e9646ccac4d19368541745e19e4eb35e21902d4c212b145cb68fc3e854d8a30db46c06894b56a0fca67808a57fe8a5fb63cfd565c6e5b10f74f67c7b0d8e4628fddf00d71aa12553667706dddf42a70100425d35b4aece54d6b36043908c4c5fa7a48c3ef2ae41817f9da229efefb595dc6aa1fcf1eb8c27901d3f8bad23cf98ae755b8ed06c61bab9e459f2e3fe23b1fc4018e5857649cf0c3a2dac89f8ff088d07ea12735d92d186f7d747f0c64e26ab258100a4fdedd3ba8cc6c5f365017bd6299c1db77db149c247c8e8d11c223c69010225e655cb5d34d7b0b82097f5c93e8e3c2a630435ac6aa2cfd8a85ed85a46a0dfff0744db19d4065d6a52d7e63249aaca849cbf1725f71a521efbb1f2e9ac4d5a418a96399ff750111681cd864ce4308f95a655b4ce90b77d6775402365b980960c0e5467b4c2b0e12d12b58eb0b6ecb8fc0f2645f0aef52e042f4ffe7b2800300008000b0004044000000005000b002063dd21e803aad497b847a8d84308f8a00bba49bb70172a6eb738cf1140e61766003ede82552c171693037b0a86828f2f64940fcabfed3e6014e7bdaa54801902b5f6568493076301c6db0b93b6b7291b45b4a3f7559e9839c198dc753fe0a297";
		
	    InByteBuf actBlob = new InByteBuf(Helpers.fromHex(actBlobString));
	    
	    TPM2B_ID_OBJECT         credBlob = TPM2B_ID_OBJECT.fromTpm(actBlob);
	    TPM2B_ENCRYPTED_SECRET  encSecret = TPM2B_ENCRYPTED_SECRET.fromTpm(actBlob);
	    TPM2B_PRIVATE           idKeyDupBlob = TPM2B_PRIVATE.fromTpm(actBlob);
	    TPM2B_ENCRYPTED_SECRET  encWrapKey = TPM2B_ENCRYPTED_SECRET.fromTpm(actBlob);
	    TPM2B_PUBLIC    		drsIdKeyPub = TPM2B_PUBLIC.fromTpm(actBlob);
	    TPM2B_DATA				encUriData = TPM2B_DATA.fromTpm(actBlob);
*/	

		// DocSamples s1 = new DocSamples();
		// s1.doAll();
	
		
		Samples s2 = new Samples();
		s2.doAll();

		System.out.println("TSSMain finished!");
		return;
	}
}
