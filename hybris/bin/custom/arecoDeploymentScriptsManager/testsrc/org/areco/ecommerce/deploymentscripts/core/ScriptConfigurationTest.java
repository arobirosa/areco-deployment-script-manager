/**
 * Copyright 2014 Antonio Robirosa

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.core;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Test;

/**
 * It checks that the script configuration including the contraints are working correctly.
 * 
 * @author arobirosa
 * 
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
// PMD doesn't find any test method in this class and complains.
public class ScriptConfigurationTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test/script-configuration-test";

    @Resource
    private DeploymentScriptStarter deploymentScriptStarter;

    @Resource
    private ScriptExecutionResultDAO flexibleSearchScriptExecutionResultDao;

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Test
    public void currentEnvironment() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "dev-only", null);
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.deploymentScriptStarter.runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        deploymentScriptResultAsserter.assertResult("20140814_TICKET_DEV_CRONJOBS", this.flexibleSearchScriptExecutionResultDao.getSuccessResult());
    }

    @Test
    public void currentTenant() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "junit-only", null);
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.deploymentScriptStarter.runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        deploymentScriptResultAsserter.assertResult("20140814_TICKET_ADD_TEST_CRONJOBS", this.flexibleSearchScriptExecutionResultDao.getSuccessResult());
    }

    @Test
    public void otherEnvironment() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "prod-only", null);
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.deploymentScriptStarter.runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        deploymentScriptResultAsserter.assertResult("20140814_TICKET_ADD_PROD_CRONJOBS",
                this.flexibleSearchScriptExecutionResultDao.getIgnoredOtherEnvironmentResult());
    }

    public void otherEnvironmentAndTenant() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "prod-env-and-tenant-master-only", null);
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.deploymentScriptStarter.runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        deploymentScriptResultAsserter.assertResult("20140814_TICKET_ADD_PROD_CRONJOBS",
                this.flexibleSearchScriptExecutionResultDao.getIgnoredOtherTenantResult());
    }

    @Test
    public void otherTenant() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "master-tenant-only", null);
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.deploymentScriptStarter.runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        deploymentScriptResultAsserter.assertResult("20140814_TICKET_ADD_TEST_CRONJOBS",
                this.flexibleSearchScriptExecutionResultDao.getIgnoredOtherTenantResult());
    }

    @Test(expected = DeploymentScriptConfigurationException.class)
    public void twoConfigurations() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "two-configurations", null);
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        this.deploymentScriptStarter.runAllPendingScripts();
        Assert.fail("An exception must have been thrown");
    }

    @Test(expected = IllegalStateException.class)
    public void unknownEnvironment() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "unknown-environment", null);
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        this.deploymentScriptStarter.runAllPendingScripts();
        Assert.fail("An exception must have been thrown");
    }

    @Test(expected = DeploymentScriptConfigurationException.class)
    public void unknownTenant() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "unknown-tenant", null);
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        this.deploymentScriptStarter.runAllPendingScripts();
        Assert.fail("An exception must have been thrown");
    }

    @Test
    public void justCreatedEnvironment() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "just-created-environment", null);
        // We simulate that we are in the just created environment
        this.getDeploymentConfigurationSetter().setEnvironment("QA_WEBSERVICE");
        final boolean wereThereErrors = this.deploymentScriptStarter.runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        deploymentScriptResultAsserter.assertResult("20140814_02_TICKET_ADD_QA_CRONJOBS", this.flexibleSearchScriptExecutionResultDao.getSuccessResult());
    }

    @Test(expected = IllegalStateException.class)
    public void undefindCurrentEnvironment() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "just-created-environment", null);
        this.getDeploymentConfigurationSetter().setEnvironment(""); // We cannot set a null value.
        this.deploymentScriptStarter.runAllPendingScripts();
        Assert.fail("An exception must have been thrown.");
    }
}
