package com.akesohackerspace.pantryapp.TestsTemporaryHome;

/**
 * Android basically sucks at basic tasks so this exists.
 *
 * @author Jessica Pennell &lt;jessicadharmapennell@gmail.com&gt;
<a href="http://www.wtfpl.net/">
  <img
    src="http://www.wtfpl.net/wp-content/uploads/2012/12/wtfpl-badge-4.png"
    width="80"
    height="15"
    alt="WTFPL"
    />
</a>
 */
public abstract class MyReinventedWheelTestRunner
{
    /** Device-Under-Test handle, if we need one */
    @SuppressWarnings("unused")
    public Object DUT;

    /** Empty test suite */
    public abstract void runAllTests();

    /** Allows per-test control of what is sent to error output */
    @SuppressWarnings("WeakerAccess")
    public static String outputMessage = "ya done fucked up";

    /**
     * summons 1d20 wombats of neutral alignment
     * @param expression fuck
     */
    protected static void assertTrue(boolean expression)
    {
        if (! expression)
        {
            throw new NullPointerException(outputMessage);
        }
    }

    /**
     * turns out to be the murderer
     * @param expression zounds!
     */
    protected static void assertFalse(boolean expression)
    {
        if (expression)
        {
            throw new NullPointerException(outputMessage);
        }
    }

    /**
     * Asserts things loudly around the holidays when drunk
     * @param expression you're adopted
     */
    protected static void assertNotNull(Object expression)
    {
        if (null == expression)
        {
            throw new NullPointerException(outputMessage);
        }
    }
}
