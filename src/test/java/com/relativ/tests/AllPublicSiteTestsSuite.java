package com.relativ.tests;

import com.relativ.tests.blog.BlogPageTest;
import com.relativ.tests.careers.CareersPageTest;
import com.relativ.tests.company.CompanyPageTest;
import com.relativ.tests.contact.ContactPageTest;
import com.relativ.tests.custom.ConfiguredClickTest;
import com.relativ.tests.footer.FooterContactSectionTest;
import com.relativ.tests.home.HomePageTest;
import com.relativ.tests.navigation.SiteNavigationTest;
import com.relativ.tests.visual.VisualRegressionTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Public Site Suite")
@SelectClasses({
        HomePageTest.class,
        SiteNavigationTest.class,
        CompanyPageTest.class,
        BlogPageTest.class,
        CareersPageTest.class,
        ContactPageTest.class,
        FooterContactSectionTest.class,
        ConfiguredClickTest.class,
        VisualRegressionTest.class
})
public class AllPublicSiteTestsSuite {
}
