package com.github.chrisblutz;


import com.github.chrisblutz.properties.Localization;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class LocalizationTest extends TestCase {

    public LocalizationTest(String name) {

        super(name);
    }

    public static TestSuite suite() {

        return new TestSuite(LocalizationTest.class);
    }

    public void testLocalization() {

        System.out.println("Current Locale: " + Localization.getLocale());
        System.out.println("Test message (unreadable_data): " + Localization.getMessage(Localization.UNREADABLE_DATA));

        if(Localization.isLoaded()) {

            System.out.println("Success!");

        }else{

            System.out.println("Failure!");
            fail("Localization did not load correctly!");
        }
    }
}
