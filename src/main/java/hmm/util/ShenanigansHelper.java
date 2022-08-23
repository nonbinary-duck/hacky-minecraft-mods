package hmm.util;

/**
 * ShenanigansHelper
 */
public class ShenanigansHelper
{
    public static boolean glassBlockRemovedFromPoweredRail = false;

    public static long railUpdatesThisTick = 0;

    public static void OnTickBegin()
    {
        allowFunkyBehaviour = false;
        
        glassBlockRemovedFromPoweredRail = false;

        railUpdatesThisTick = 0;
    }

    public static boolean allowFunkyBehaviour = false;

    public static void OnTickEnd()
    {
        allowFunkyBehaviour = (railUpdatesThisTick >= 5 && glassBlockRemovedFromPoweredRail);

        if (allowFunkyBehaviour) System.out.println("funky behaviour achieved!");
        if (railUpdatesThisTick > 0) System.out.println("Rails updated: " + railUpdatesThisTick);
    }
}