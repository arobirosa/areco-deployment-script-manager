package org.areco.ecommerce.deploymentscripts.beanshell;

import de.hybris.bootstrap.annotations.UnitTest;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageExecutionException;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageService;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.beanshell.DefaultBeanShellService;
import org.junit.Assert;
import org.junit.Test;

/**
 * It checks if the beanshell service handles the returned codes from the script correctly.
 *
 * Created by arobirosa on 27.01.15.
 */
@UnitTest
public class BeanShellReturnedCodeValidationTest {

        //We don't have any mocks to inject.
        private final ScriptingLanguageService defaultBeanShellService = new DefaultBeanShellService();

        @Test
        @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") //This test doesn't required to assert anything.
        public void testCorrectReturnedValue() throws ScriptingLanguageExecutionException {
                defaultBeanShellService.executeScript("return \"OK\";");
        }

        @Test
        public void testIncorrectReturnedValue() {
                try {
                        defaultBeanShellService.executeScript("return \"Error99\";");
                } catch (ScriptingLanguageExecutionException e) {
                        Assert.assertTrue("The message of the exception must contain the returned value: "
                                + e.getMessage(), e.getMessage().contains("Error99"));
                        return;
                }
                Assert.fail("An exception must have been thrown.");
        }
}
