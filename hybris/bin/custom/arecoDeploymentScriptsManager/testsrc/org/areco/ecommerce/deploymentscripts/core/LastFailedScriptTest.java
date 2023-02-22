/**
 * Copyright 2017 Antonio Robirosa
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
import org.areco.ecommerce.deploymentscripts.ant.AntDeploymentScriptsStarter;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * It checks if the detection of the failure of the last script is working correctly.
 * <p/>
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
@IntegrationTest
@SuppressWarnings("PMD.AvoidDuplicateLiterals") // Without constants the query is easier to read
public class LastFailedScriptTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test/last-failed-script-test";

    @Resource
    private AntDeploymentScriptsStarter antDeploymentScriptsStarter;

    @Resource
    private DeploymentScriptService arecoDeploymentScriptService;

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Before
    public void setUpFolder() {
        Assert.assertTrue("The database has failed scripts from other test", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }

    @Test
    public void testTwoShortScripts() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "update-deployment-scripts");
        antDeploymentScriptsStarter.runPendingScripts();
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertErrorResult("20150513_PENDING_SCRIPT_WRONG");
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }

    @Test
    public void testIfWillBeExecutedScriptsAreIgnored() {
        // Given
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-will-executed-test");

        // When
        antDeploymentScriptsStarter.runPendingScripts();

        // Then
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertErrorResult("20150513_PENDING_SCRIPT_WRONG");
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDao().getWillBeExecuted());
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }

    @Test
    public void testIfFailedScriptWhichAreRemovedOnDiskAreIgnored() {
        // Given
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-will-executed-test");
        antDeploymentScriptsStarter.runPendingScripts();
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertErrorResult("20150513_PENDING_SCRIPT_WRONG");
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDao().getWillBeExecuted());
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());
        // Change the script directory. Now 20150513_PENDING_SCRIPT_WRONG don't exist on Disk
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-removed-error-script-on-disk");

        // When
        antDeploymentScriptsStarter.runPendingScripts();

        // Then
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertResult("20150513_PENDING_SCRIPT_WRONG", getScriptExecutionResultDao().getIgnoredRemovedOnDisk());
        deploymentScriptResultAsserter.assertSuccessfulResult("20230220_WILL_BE_EXECUTED");
        Assert.assertTrue("The last script must succeed", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }

    @Test
    public void testIfFailedScriptWhichAreRemovedOnDiskAndAddedAgainAreExecuted() {
        // Given
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-will-executed-test");
        antDeploymentScriptsStarter.runPendingScripts();
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertErrorResult("20150513_PENDING_SCRIPT_WRONG");
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDao().getWillBeExecuted());
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());

        // Change the script directory. Now 20150513_PENDING_SCRIPT_WRONG don't exist on Disk
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-removed-error-script-on-disk");
        antDeploymentScriptsStarter.runPendingScripts();
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertResult("20150513_PENDING_SCRIPT_WRONG", getScriptExecutionResultDao().getIgnoredRemovedOnDisk());
        deploymentScriptResultAsserter.assertSuccessfulResult("20230220_WILL_BE_EXECUTED");
        Assert.assertTrue("The last script must succeed", arecoDeploymentScriptService.wasLastScriptSuccessful());

        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-corrected-will-executed-test");
        // When
        antDeploymentScriptsStarter.runPendingScripts();

        // Then
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertSuccessfulResult("20150513_PENDING_SCRIPT_WRONG");
        deploymentScriptResultAsserter.assertSuccessfulResult("20230220_WILL_BE_EXECUTED");
        Assert.assertTrue("The last script must succeed", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }

    @Test
    public void testIfWillBeExecutedScriptWhichAreRemovedOnDiskAreIgnored() {
        // Given
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-will-executed-test");
        antDeploymentScriptsStarter.runPendingScripts();
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertErrorResult("20150513_PENDING_SCRIPT_WRONG");
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDao().getWillBeExecuted());
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());
        // Change the script directory. Now 20150513_PENDING_SCRIPT_WRONG don't exist on Disk
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-removed-will-be-executed-script-on-disk");

        // When
        antDeploymentScriptsStarter.runPendingScripts();

        // Then
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertSuccessfulResult("20150513_PENDING_SCRIPT_WRONG");
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDao().getIgnoredRemovedOnDisk());
        Assert.assertTrue("The last script must succeed", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }

    @Test
    public void testIfFailedAndWillBeExecutedScriptsWhichAreRemovedOnDiskAreIgnored() {
        // Given
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-will-executed-test");
        antDeploymentScriptsStarter.runPendingScripts();
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertErrorResult("20150513_PENDING_SCRIPT_WRONG");
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDao().getWillBeExecuted());
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());
        // Change the script directory. Now 20150513_PENDING_SCRIPT_WRONG don't exist on Disk
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-removed-failed-and-will-be-executed-script-on-disk");

        // When
        antDeploymentScriptsStarter.runPendingScripts();

        // Then
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertResult("20150513_PENDING_SCRIPT_WRONG", getScriptExecutionResultDao().getIgnoredRemovedOnDisk());
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDao().getIgnoredRemovedOnDisk());
        Assert.assertTrue("The last script must succeed", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }
}
