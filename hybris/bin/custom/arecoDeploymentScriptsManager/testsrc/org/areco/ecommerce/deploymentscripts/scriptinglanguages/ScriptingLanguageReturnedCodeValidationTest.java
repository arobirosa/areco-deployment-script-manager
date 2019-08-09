package org.areco.ecommerce.deploymentscripts.scriptinglanguages;

import de.hybris.bootstrap.annotations.IntegrationTest;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * It checks if the beanshell service handles the returned codes from the script correctly.
 *
 * Created by arobirosa on 27.01.15.
 */
@IntegrationTest
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") //This test asserts using the asserter class
public class ScriptingLanguageReturnedCodeValidationTest extends AbstractWithConfigurationRestorationTest {

        private static final String RESOURCES_FOLDER = "/resources/test";

        @Test
        public void testReturnOK() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "scripts-returning-ok");
                final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.assertFalse("There were errors", wereThereErrors);
                getDeploymentScriptResultAsserter().assertResult("20190807_54_GROOVY_RETURN_OK", this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        }

        @Test
        public void testReturnAnotherValue() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "scripts-returning-nok");
                final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.assertTrue("There were no errors", wereThereErrors);
                getDeploymentScriptResultAsserter().assertErrorResult("20190807_54_GROOVY_RETURN_NOK");
        }
}
