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
package org.areco.ecommerce.deploymentscripts.ant;

import de.hybris.bootstrap.annotations.IntegrationTest;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDAO;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Test;

/**
 * It checks if the antDeploymentScriptStarter stops the build if there is an error running a deployment script.
 * 
 * @author arobirosa
 * 
 */
@IntegrationTest
// PMD doesn't see the assert in the private methods.
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
public class AntDeploymentScriptsStarterErrorHandlingTest extends AbstractWithConfigurationRestorationTest {

    private static final String RESOURCES_FOLDER = "/resources/test/ant-scripts-starter-error-handling";

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Resource
    private ScriptExecutionResultDAO flexibleSearchScriptExecutionResultDao;

    @Resource
    private AntDeploymentScriptsStarter antDeploymentScriptsStarter;

    @Test
    public void testNoPendingScripts() {
        this.assertReturnValue("no-scripts", true);
    }

    @Test
    public void testScriptWithError() {
        this.assertReturnValue("script-with-error", false);
        deploymentScriptResultAsserter.assertResult("20141003_PENDING_SCRIPT", this.flexibleSearchScriptExecutionResultDao.getErrorResult());
    }

    @Test
    public void testScriptWithoutError() {
        this.assertReturnValue("script-without-error", true);
        deploymentScriptResultAsserter.assertResult("20141003_PENDING_SCRIPT", this.flexibleSearchScriptExecutionResultDao.getSuccessResult());
    }

    private void assertReturnValue(final String scriptFolder, final boolean expectedWereScriptsSuccessful) {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, scriptFolder, null);

        final int returnValueScripts = antDeploymentScriptsStarter.runPendingScripts();
        if (expectedWereScriptsSuccessful) {
            Assert.assertEquals("There were errors", 0, returnValueScripts);
        } else {
            Assert.assertNotSame("There were no errors", Integer.valueOf(0), Integer.valueOf(returnValueScripts));
        }
        final boolean wereScriptsSuccessful = antDeploymentScriptsStarter.wasLastScriptSuccessful();
        Assert.assertEquals("The result of the check after the scripts were run is wrong", expectedWereScriptsSuccessful, wereScriptsSuccessful);
    }

}
