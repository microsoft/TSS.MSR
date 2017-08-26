package TSS;

import TSS.TpmTypes.TPMT_PUBLIC;

public class TssKey
{
	public TssKey()
	{
	}
	public byte[] PrivatePart;
	public TPMT_PUBLIC PublicPart;
}
