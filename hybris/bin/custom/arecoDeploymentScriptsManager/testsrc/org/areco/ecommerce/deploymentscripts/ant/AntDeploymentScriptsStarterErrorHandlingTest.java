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
package org.areco.ecommerce.deploymentscripts.ant;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.user.UserService;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * It checks if the antDeploymentScriptStarter stops the build if there is an error running a deployment script.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
@IntegrationTest
// PMD doesn't see the assert in the private methods.
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
public class AntDeploymentScriptsStarterErrorHandlingTest extends AbstractWithConfigurationRestorationTest {

    private static final String RESOURCES_FOLDER = "/resources/test/ant-scripts-starter-error-handling";

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Resource
    private AntDeploymentScriptsStarter antDeploymentScriptsStarter;

    @Resource
    private UserService userService;

    @Test
    public void testNoPendingScripts() {
        this.assertReturnValue("no-scripts", true);
    }

    @Test
    public void testScriptWithError() {
        this.assertReturnValue("script-with-error", false);
        this.deploymentScriptResultAsserter.assertErrorResult("20141003_PENDING_SCRIPT");
    }

    @Test
    public void testScriptWithoutError() {
        this.assertReturnValue("script-without-error", true);
        this.deploymentScriptResultAsserter.assertSuccessfulResult("20141003_PENDING_SCRIPT");
    }

    @Test
    public void testScriptWhichRequiresAdmin() {
        Assert.assertEquals("The user before running the scripts must be anonymous", userService.getAnonymousUser(), userService.getCurrentUser());

        this.assertReturnValue("script-requiring-admin", true);
        this.deploymentScriptResultAsserter.assertSuccessfulResult("20230216_18_REQUIRES_ADMIN");

        Assert.assertEquals("The user after running the scripts must be anonymous", userService.getAnonymousUser(), userService.getCurrentUser());
    }

    private void assertReturnValue(final String scriptFolder, final boolean expectedWereScriptsSuccessful) {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, scriptFolder);

        try {
            this.antDeploymentScriptsStarter.runPendingScriptsAndThrowExceptionIfThereWasAnError();
            if (!expectedWereScriptsSuccessful) {
                Assert.fail("There were no errors");
            }
        } catch (final DeploymentScriptFailureException e) {
            if (expectedWereScriptsSuccessful) {
                Assert.fail("There were errors");
            }
        }

        try {
            this.antDeploymentScriptsStarter.stopAntBuildIfTheLastScriptFailed();
            if (!expectedWereScriptsSuccessful) {
                Assert.fail("The last script was expected to fail");
            }
        } catch (final DeploymentScriptFailureException e) {
            if (expectedWereScriptsSuccessful) {
                Assert.fail("The last script failed");
            }
        }
    }

}
