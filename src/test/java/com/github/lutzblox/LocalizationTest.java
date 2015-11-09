package com.github.lutzblox;


import com.github.lutzblox.properties.Localization;
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
        System.out.println("Test message (unreadable_packet): " + Localization.getMessage(Localization.UNREADABLE_PACKET));
    }
}
