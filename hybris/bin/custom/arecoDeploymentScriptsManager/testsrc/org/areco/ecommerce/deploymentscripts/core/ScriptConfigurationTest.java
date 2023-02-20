/**
 * Copyright 2014 Antonio Robirosa
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.core;

import de.hybris.bootstrap.annotations.IntegrationTest;
import org.areco.ecommerce.deploymentscripts.core.impl.FlexibleSearchDeploymentEnvironmentDAO;
import org.junit.Assert;
import org.junit.Test;

/**
 * It checks that the script configuration including the contraints are working correctly.
 *
 * @author arobirosa
 */
@IntegrationTest
@SuppressWarnings({"PMD.TooManyMethods", "PMD.JUnitTestsShouldIncludeAssert"})
//It a test with many cases. The assert statements are inside a private method.
public class ScriptConfigurationTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test/script-configuration-test";

    @Test
    public void testCurrentEnvironment() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "dev-only");
        this.getDeploymentConfigurationSetter().setEnvironment(ServerEnvironments.DEVELOPMENT);
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20140814_TICKET_DEV_CRONJOBS", this.getScriptExecutionResultDAO().getSuccessResult());
    }

    private void runAndAssertNoErrors() {
        Assert.assertFalse("There were errors", this.getDeploymentScriptStarter().runAllPendingScripts());
    }

    @Test
    public void currentTenant() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "junit-only");
        this.getDeploymentConfigurationSetter().setEnvironment(ServerEnvironments.DEVELOPMENT);
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20140814_TICKET_ADD_TEST_CRONJOBS",
                this.getScriptExecutionResultDAO().getSuccessResult());
    }

    @Test
    public void otherEnvironment() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "prod-only");
        this.getDeploymentConfigurationSetter().setEnvironment(ServerEnvironments.DEVELOPMENT);
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20140814_TICKET_ADD_PROD_CRONJOBS",
                this.getScriptExecutionResultDAO().getIgnoredOtherEnvironmentResult());
    }

    @Test
    public void otherEnvironmentAndTenant() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "prod-env-and-tenant-master-only");
        this.getDeploymentConfigurationSetter().setEnvironment(ServerEnvironments.DEVELOPMENT);
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20140814_TICKET_ADD_PROD_CRONJOBS",
                this.getScriptExecutionResultDAO().getIgnoredOtherTenantResult());
    }

    @Test
    public void otherTenant() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "master-tenant-only");
        this.getDeploymentConfigurationSetter().setEnvironment(ServerEnvironments.DEVELOPMENT);
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20140814_TICKET_ADD_TEST_CRONJOBS",
                this.getScriptExecutionResultDAO().getIgnoredOtherTenantResult());
    }

    @Test(expected = DeploymentScriptConfigurationException.class)
    public void twoConfigurations() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "two-configurations");
        this.getDeploymentConfigurationSetter().setEnvironment(ServerEnvironments.DEVELOPMENT);
        this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.fail("An exception must have been thrown");
    }

    @Test(expected = IllegalStateException.class)
    public void unknownEnvironment() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "unknown-environment");
        this.getDeploymentConfigurationSetter().setEnvironment(ServerEnvironments.DEVELOPMENT);
        this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.fail("An exception must have been thrown");
    }

    @Test(expected = DeploymentScriptConfigurationException.class)
    public void unknownTenant() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "unknown-tenant");
        this.getDeploymentConfigurationSetter().setEnvironment(ServerEnvironments.DEVELOPMENT);
        this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.fail("An exception must have been thrown");
    }

    @Test
    public void justCreatedEnvironment() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "just-created-environment");
        // We simulate that we are in the just created environment
        this.getDeploymentConfigurationSetter().setEnvironment("QA_WEBSERVICE");
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20140814_02_TICKET_ADD_QA_CRONJOBS",
                this.getScriptExecutionResultDAO().getSuccessResult());
    }

    @Test
    public void undefindCurrentEnvironment() {
        try {
            this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "just-created-environment");
            this.getDeploymentConfigurationSetter().setEnvironment(""); // We cannot set a null value.
            this.getDeploymentScriptStarter().runAllPendingScripts();
        } catch (final IllegalStateException e) {
            Assert.assertEquals("The exception has the wrong error message",
                    FlexibleSearchDeploymentEnvironmentDAO.UNCONFIGURATED_CURRENT_ENVIRONMENT_ERROR_MESSAGE, e.getMessage());
            return;
        }
        Assert.fail("An exception must have been thrown.");
    }

    @Test
    public void blankCurrentEnvironment() {
        try {
            this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "just-created-environment");
            this.getDeploymentConfigurationSetter().setEnvironment("                "); // We cannot set a null value.
            this.getDeploymentScriptStarter().runAllPendingScripts();
        } catch (final IllegalStateException e) {
            Assert.assertEquals("The exception has the wrong error message",
                    FlexibleSearchDeploymentEnvironmentDAO.UNCONFIGURATED_CURRENT_ENVIRONMENT_ERROR_MESSAGE, e.getMessage());
            return;
        }
        Assert.fail("An exception must have been thrown.");
    }
}
