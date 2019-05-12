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
import de.hybris.platform.core.Registry;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.core.TenantDetector;
import org.areco.ecommerce.deploymentscripts.core.impl.RegistryTenantDetector;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.fest.util.Collections;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * It checks if the essentialDataCreator triggers the essential data creation and runs the scripts. Hybris cannot change the tenant when we are inside a
 * transaction, because we need transactions, this test only works in single tenant environments.
 * 
 * @author arobirosa
 * 
 */
@IntegrationTest
public class DataCreatorAndDeploymentScriptStarterTest extends AbstractWithConfigurationRestorationTest {

    private static final String RESOURCES_FOLDER = "/resources/test/essential-data-creator";

    private static final Logger LOG = Logger.getLogger(DataCreatorAndDeploymentScriptStarterTest.class);

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Resource
    private DataCreatorAndDeploymentScriptsStarter dataCreatorAndDeploymentScriptsStarter;

    @Resource
    private AntDeploymentScriptsStarter antDeploymentScriptsStarter;

    @Resource
    private EssentialDataCreationDetector essentialDataCreationDetector;

    @Resource
    private TenantDetector registryTenantDetector;

    @Test
    public void testRunScripts() {
        // Hybris cannot change the tenant when we are inside a transaction, because we need transactions -import the essential data-, this test only works in
        // single tenant environments.
        if (!registryTenantDetector.areWeInATestSystemWithOneSingleTenant()) {
            this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "update-scripts", "init-scripts");
            this.dataCreatorAndDeploymentScriptsStarter.runInJunitTenant();
            Assert.assertTrue("There were errors running the deployment scripts", antDeploymentScriptsStarter.wasLastScriptSuccessful());
            deploymentScriptResultAsserter.assertSuccessfulResult("20141005_RELOAD_CMS_CONF");
            deploymentScriptResultAsserter.assertSuccessfulResult("20141004_RELOAD_CMS_CONF");
            Assert.assertTrue("The creation of the essential data wasn't triggered.", essentialDataCreationDetector.isWasEssentialDataCreated());
            Assert.assertTrue("The creation of the project data wasn't triggered.", essentialDataCreationDetector.isWasProjectDataCreated());
        } else {
            LOG.error("Ignoring this test because it only has one tenant.");
        }
    }

}
