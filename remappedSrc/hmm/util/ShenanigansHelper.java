package hmm.util;

/**
 * ShenanigansHelper
 */
public class ShenanigansHelper
{
    public static boolean glassBlockRemovedFromPoweredRail = false;

    private static long railUpdatesThisTick = 0;

    public static void OnTickBegin()
    {
        allowFunkyBehaviour = false;
        
        glassBlockRemovedFromPoweredRail = false;

        railUpdatesThisTick = 0;
    }

    public static boolean allowFunkyBehaviour = false;

    public static void IncrementRailUpdateTicker()
    {
        if (allowFunkyBehaviour) return;
        
        railUpdatesThisTick += 1;

        allowFunkyBehaviour = (railUpdatesThisTick >= 512 && glassBlockRemovedFromPoweredRail);

        // if (railUpdatesThisTick != 0 && glassBlockRemovedFromPoweredRail) System.out.println("u " + railUpdatesThisTick);

        // if (allowFunkyBehaviour) System.out.println("Thing");
    }
}