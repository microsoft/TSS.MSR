/**
 * Simple command line parser
 */
package samples;

/**
 * Simple command line parser
 */
public class CmdLine {

	// Information about command line arguments
	static int     	s_ArgsMask = 0;     // Bit mask of unmatched command line args
	static String[] Args = null;

	public static void setArgs(String[] args)
	{
	    Args = args;
	    s_ArgsMask = (1 << Args.length) - 1;
	}

	public static boolean isOptionPresent(String optFull, String optShort)
	{
	    if (s_ArgsMask == 0 || optFull == null || optFull.length() == 0)
	        return false;

	    for (int i = 0, curArgBit = 1; i < Args.length; ++i, curArgBit <<= 1)
	    {
	        if (    (s_ArgsMask & curArgBit) == curArgBit && Args[i] != null
	            && (IsOpt(Args[i], optFull, optShort)
	                || (Args[i].charAt(0) == '/' || Args[i].charAt(0) == '-')
	                    && IsOpt(Args[i].substring(1), optFull, optShort)))
	        {
	            s_ArgsMask ^= curArgBit;
	            return true;
	        }
	    }
	    return false;
	}

	static boolean IsOpt(String cmdLinelParam, String optFull, String optShort)
	{
	    return 0 == cmdLinelParam.compareToIgnoreCase(optFull)
	        || (optShort != null && optShort.length() > 0 && cmdLinelParam.length() == 1
				&& cmdLinelParam.toLowerCase().charAt(0) == optShort.toLowerCase().charAt(0));
	}
}
