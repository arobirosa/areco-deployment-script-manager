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
import org.junit.Assert;
import org.junit.Test;

/**
 * It checks that the script configuration including the contraints are working correctly.
 *
 * @author arobirosa
 */
@IntegrationTest
public class MixedCaseEnvironmentNamesTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test";

    @Test
    public void testMixedCase() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "mixedcaseenvironmentnames");
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        getDeploymentScriptResultAsserter().assertResult("20150801_TICKET_DEV_CRONJOBS", this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
    }

    @Test
    public void testEnvironmentWithSpaces() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "environmentnameswithspaces");
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        getDeploymentScriptResultAsserter().assertResult("20150801_TICKET_DEV_CRONJOBS", this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
    }
}
