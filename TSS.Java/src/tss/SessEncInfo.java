package tss;

/**
 *  Parameters of the field, to which session based encryption can be applied (i.e.
 *  the first non-handle field marshaled in size-prefixed form)
 */
public class SessEncInfo
{
    /** Length of the size prefix in bytes. The size prefix contains the number of
     *  elements in the sized fieled (normally just bytes).
     */
    public int sizeLen = 0;

    /** Length of an element of the sized area in bytes (in most cases 1) */
    public int valLen = 0;

    public SessEncInfo(int sizeLen, int valLen)
    {
        this.sizeLen = sizeLen;
        this.valLen = valLen;
    }
}