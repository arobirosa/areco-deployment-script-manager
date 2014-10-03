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
package org.areco.ecommerce.deploymentscripts.core;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentConfigurationSetter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

/**
 * It restores the configuration of the manager, for example, the folders after the test.
 * 
 * @author arobirosa
 * 
 */
@IntegrationTest
@Ignore
public abstract class AbstractWithConfigurationRestorationTest extends ServicelayerTransactionalTest {

    private final DeploymentConfigurationSetter deploymentConfigurationSetter = new DeploymentConfigurationSetter();

    /**
     * Save the settings.
     */
    @Before
    public void saveOldFolders() {
        deploymentConfigurationSetter.saveCurrentFolders();
    }

    /**
     * Restore the settings.
     */
    @After
    public void restoreOldFolders() {
        // We don't want to affect other tests
        this.deploymentConfigurationSetter.restoreOldFolders();
    }

    protected DeploymentConfigurationSetter getDeploymentConfigurationSetter() {
        return deploymentConfigurationSetter;
    }
}
