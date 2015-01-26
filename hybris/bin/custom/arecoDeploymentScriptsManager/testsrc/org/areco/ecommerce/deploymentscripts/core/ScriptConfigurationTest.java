/**
 * Copyright 2014 Antonio Robirosa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.core;

import junit.framework.Assert;
import org.junit.Test;

/**
 * It checks that the script configuration including the contraints are working correctly.
 *
 * @author arobirosa
 */
public class ScriptConfigurationTest extends AbstractWithConfigurationRestorationTest {
        private static final String RESOURCES_FOLDER = "/resources/test/script-configuration-test";

        @Test
        public void currentEnvironment() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "dev-only", null);
                this.getDeploymentConfigurationSetter().setEnvironment("DEV");
                final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.assertFalse("There were errors", wereThereErrors);
                getDeploymentScriptResultAsserter().assertResult("20140814_TICKET_DEV_CRONJOBS",
                        this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        }

        @Test
        public void currentTenant() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "junit-only", null);
                this.getDeploymentConfigurationSetter().setEnvironment("DEV");
                final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.assertFalse("There were errors", wereThereErrors);
                getDeploymentScriptResultAsserter().assertResult("20140814_TICKET_ADD_TEST_CRONJOBS",
                        this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        }

        @Test
        public void otherEnvironment() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "prod-only", null);
                this.getDeploymentConfigurationSetter().setEnvironment("DEV");
                final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.assertFalse("There were errors", wereThereErrors);
                getDeploymentScriptResultAsserter().assertResult("20140814_TICKET_ADD_PROD_CRONJOBS",
                        this.getFlexibleSearchScriptExecutionResultDao().getIgnoredOtherEnvironmentResult());
        }

        public void otherEnvironmentAndTenant() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "prod-env-and-tenant-master-only", null);
                this.getDeploymentConfigurationSetter().setEnvironment("DEV");
                final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.assertFalse("There were errors", wereThereErrors);
                getDeploymentScriptResultAsserter().assertResult("20140814_TICKET_ADD_PROD_CRONJOBS",
                        this.getFlexibleSearchScriptExecutionResultDao().getIgnoredOtherTenantResult());
        }

        @Test
        public void otherTenant() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "master-tenant-only", null);
                this.getDeploymentConfigurationSetter().setEnvironment("DEV");
                final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.assertFalse("There were errors", wereThereErrors);
                getDeploymentScriptResultAsserter().assertResult("20140814_TICKET_ADD_TEST_CRONJOBS",
                        this.getFlexibleSearchScriptExecutionResultDao().getIgnoredOtherTenantResult());
        }

        @Test(expected = DeploymentScriptConfigurationException.class)
        public void twoConfigurations() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "two-configurations", null);
                this.getDeploymentConfigurationSetter().setEnvironment("DEV");
                this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.fail("An exception must have been thrown");
        }

        @Test(expected = IllegalStateException.class)
        public void unknownEnvironment() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "unknown-environment", null);
                this.getDeploymentConfigurationSetter().setEnvironment("DEV");
                this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.fail("An exception must have been thrown");
        }

        @Test(expected = DeploymentScriptConfigurationException.class)
        public void unknownTenant() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "unknown-tenant", null);
                this.getDeploymentConfigurationSetter().setEnvironment("DEV");
                this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.fail("An exception must have been thrown");
        }

        @Test
        public void justCreatedEnvironment() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "just-created-environment", null);
                // We simulate that we are in the just created environment
                this.getDeploymentConfigurationSetter().setEnvironment("QA_WEBSERVICE");
                final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.assertFalse("There were errors", wereThereErrors);
                getDeploymentScriptResultAsserter().assertResult("20140814_02_TICKET_ADD_QA_CRONJOBS",
                        this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        }

        @Test(expected = IllegalStagitteException.class)
        public void undefindCurrentEnvironment() {
                this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "just-created-environment", null);
                this.getDeploymentConfigurationSetter().setEnvironment("");// We cannot set a null value.
                this.getDeploymentScriptStarter().runAllPendingScripts();
                Assert.fail("An exception must have been thrown.");
        }
}
