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
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentConfigurationSetter;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * It restores the configuration of the manager, for example, the folders after the test.
 *
 * @author arobirosa
 */
@IntegrationTest
@Ignore
public abstract class AbstractWithConfigurationRestorationTest extends ServicelayerTransactionalTest {
    /*
     * Logger of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractWithConfigurationRestorationTest.class);

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;
    @Resource
    private DeploymentScriptStarter deploymentScriptStarter;

    @Resource
    private ScriptExecutionResultDao scriptExecutionResultDao;

    @Resource
    private DeploymentConfigurationSetter deploymentConfigurationSetter;

    @Before
    public void saveOldFolders() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Saving current folders");
        }
        this.deploymentConfigurationSetter.saveCurrentFolders();
    }

    @After
    public void restoreOldFolders() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Restoring old folders");
        }
        // We don't want to affect other tests
        this.deploymentConfigurationSetter.restoreOldFolders();
    }

    @After
    public void resetErrorFlag() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Clearing error flag.");
        }
        this.deploymentScriptStarter.clearErrorFlag();
    }

    protected DeploymentConfigurationSetter getDeploymentConfigurationSetter() {
        return this.deploymentConfigurationSetter;
    }

    protected DeploymentScriptResultAsserter getDeploymentScriptResultAsserter() {
        return this.deploymentScriptResultAsserter;
    }

    protected DeploymentScriptStarter getDeploymentScriptStarter() {
        return this.deploymentScriptStarter;
    }

    protected ScriptExecutionResultDao getScriptExecutionResultDao() {
        return this.scriptExecutionResultDao;
    }
}
