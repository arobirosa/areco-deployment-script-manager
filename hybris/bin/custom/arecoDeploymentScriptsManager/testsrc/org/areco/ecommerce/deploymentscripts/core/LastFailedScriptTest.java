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
 * Created by arobirosa on 12.05.15.
 */
@IntegrationTest
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
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDAO().getWillBeExecuted());
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }

    @Test
    public void testIfFailedScriptWhichAreRemovedOnDiskAreIgnored() {
        // Given
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-will-executed-test");
        antDeploymentScriptsStarter.runPendingScripts();
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertErrorResult("20150513_PENDING_SCRIPT_WRONG");
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDAO().getWillBeExecuted());
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());
        // Change the script directory. Now 20150513_PENDING_SCRIPT_WRONG don't exist on Disk
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-removed-error-script-on-disk");

        // When
        antDeploymentScriptsStarter.runPendingScripts();

        // Then
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertResult("20150513_PENDING_SCRIPT_WRONG", getScriptExecutionResultDAO().getIgnoredRemovedOnDisk());
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
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDAO().getWillBeExecuted());
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());

        // Change the script directory. Now 20150513_PENDING_SCRIPT_WRONG don't exist on Disk
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-removed-error-script-on-disk");
        antDeploymentScriptsStarter.runPendingScripts();
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertResult("20150513_PENDING_SCRIPT_WRONG", getScriptExecutionResultDAO().getIgnoredRemovedOnDisk());
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
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDAO().getWillBeExecuted());
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());
        // Change the script directory. Now 20150513_PENDING_SCRIPT_WRONG don't exist on Disk
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-removed-will-be-executed-script-on-disk");

        // When
        antDeploymentScriptsStarter.runPendingScripts();

        // Then
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertSuccessfulResult("20150513_PENDING_SCRIPT_WRONG");
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDAO().getIgnoredRemovedOnDisk());
        Assert.assertTrue("The last script must succeed", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }

    @Test
    public void testIfFailedAndWillBeExecutedScriptsWhichAreRemovedOnDiskAreIgnored() {
        // Given
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-will-executed-test");
        antDeploymentScriptsStarter.runPendingScripts();
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertErrorResult("20150513_PENDING_SCRIPT_WRONG");
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDAO().getWillBeExecuted());
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());
        // Change the script directory. Now 20150513_PENDING_SCRIPT_WRONG don't exist on Disk
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "with-removed-failed-and-will-be-executed-script-on-disk");

        // When
        antDeploymentScriptsStarter.runPendingScripts();

        // Then
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertResult("20150513_PENDING_SCRIPT_WRONG", getScriptExecutionResultDAO().getIgnoredRemovedOnDisk());
        deploymentScriptResultAsserter.assertResult("20230220_WILL_BE_EXECUTED", getScriptExecutionResultDAO().getIgnoredRemovedOnDisk());
        Assert.assertTrue("The last script must succeed", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }
}
