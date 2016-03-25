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
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "update-deployment-scripts");
        Assert.assertTrue("The database has failed scripts from other test", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }

    @Test
    public void testTwoShortScripts() {
        antDeploymentScriptsStarter.runPendingScripts();
        deploymentScriptResultAsserter.assertSuccessfulResult("20150512_PENDING_SCRIPT_CORRECT");
        deploymentScriptResultAsserter.assertErrorResult("20150513_PENDING_SCRIPT_WRONG");
        Assert.assertFalse("The last script must fail", arecoDeploymentScriptService.wasLastScriptSuccessful());
    }
}
