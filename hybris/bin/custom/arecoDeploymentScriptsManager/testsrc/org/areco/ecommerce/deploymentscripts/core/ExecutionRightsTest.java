package org.areco.ecommerce.deploymentscripts.core;

import de.hybris.bootstrap.annotations.IntegrationTest;
import org.areco.ecommerce.deploymentscripts.ant.AntDeploymentScriptsStarter;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

@IntegrationTest
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // The assert statements are inside a private method.
public class ExecutionRightsTest extends AbstractWithConfigurationRestorationTest {

        private static final String RESOURCES_FOLDER = "/resources/test/execution-rights";

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
        public void testAdminExecutionRights() {
                antDeploymentScriptsStarter.runPendingScripts();
                deploymentScriptResultAsserter.assertSuccessfulResult("20190807_54_EXECUTION_RIGHTS");
        }
}
