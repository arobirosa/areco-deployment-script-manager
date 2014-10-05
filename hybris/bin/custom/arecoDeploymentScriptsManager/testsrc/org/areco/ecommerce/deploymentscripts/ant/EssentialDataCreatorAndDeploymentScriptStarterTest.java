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
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Test;

/**
 * It checks if the essentialDataCreator triggers the essential data creation and runs the scripts.
 * 
 * @author arobirosa
 * 
 */
@IntegrationTest
public class EssentialDataCreatorAndDeploymentScriptStarterTest extends AbstractWithConfigurationRestorationTest {

    private static final String RESOURCES_FOLDER = "/resources/test/essential-data-creator";

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Resource
    private EssentialDataCreatorAndDeploymentScriptsStarter essentialDataCreatorAndDeploymentScriptsStarter;

    @Resource
    private AntDeploymentScriptsStarter antDeploymentScriptsStarter;

    @Resource
    private EssentialDataCreationDetector essentialDataCreationDetector;

    @Test
    public void testRunScripts() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "scripts", null);
        this.essentialDataCreatorAndDeploymentScriptsStarter.runInJunitTenant();
        Assert.assertTrue("There were errors running the deployment scripts", antDeploymentScriptsStarter.wasLastScriptSuccessful());
        deploymentScriptResultAsserter.assertSuccessfulResult("20141004_RELOAD_CMS_CONF");
        Assert.assertTrue("The creation of the essential data wasn't triggered.", essentialDataCreationDetector.isWasEssentialDataCreated());
    }

}
