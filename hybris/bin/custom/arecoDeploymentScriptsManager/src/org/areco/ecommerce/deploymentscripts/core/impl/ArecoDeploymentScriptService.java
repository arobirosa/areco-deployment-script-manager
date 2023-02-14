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
package org.areco.ecommerce.deploymentscripts.core.impl;

import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptFinder;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptRunner;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptService;
import org.areco.ecommerce.deploymentscripts.core.InitialConfigurationImporter;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionDao;
import org.areco.ecommerce.deploymentscripts.core.UpdatingSystemExtensionContext;
import org.areco.ecommerce.deploymentscripts.systemsetup.ExtensionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of the deployment script service.
 *
 * @author arobirosa
 */
@Service
@Scope("tenant")
public class ArecoDeploymentScriptService implements DeploymentScriptService {
    private static final Logger LOG = LoggerFactory.getLogger(ArecoDeploymentScriptService.class);

    @Autowired
    private DeploymentScriptFinder finder;

    @Autowired
    private DeploymentScriptRunner runner;

    @Autowired
    private InitialConfigurationImporter initialConfigurationImporter;

    @Autowired
    private ScriptExecutionDao scriptExecutionDao;

    @Autowired
    private ExtensionHelper extensionHelper;

    /*
     * (non-Javadoc)
     *
     * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptService#runUpdateDeploymentScripts(de.hybris.platform
     * .core.initialization.SystemSetupContext)
     */
    @Override
    public boolean runDeploymentScripts(final UpdatingSystemExtensionContext context, final boolean runInitScripts) {
        ServicesUtil.validateParameterNotNullStandardMessage("context", context);
        if (!this.extensionHelper.isDeploymentManagerExtensionTurnedOn()) {
            return false;
        }
        this.initialConfigurationImporter.importConfigurationIfRequired(context);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Looking for pending update scripts in the extension " + context.getExtensionName());
        }
        final List<DeploymentScript> scriptsToBeRun = this.finder.getPendingScripts(context.getExtensionName(), context.getProcess(), runInitScripts);
        if (scriptsToBeRun.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("There aren't any pending " + (runInitScripts ? "INIT" : "UPDATE") + " deployment scripts in the extension "
                        + context.getExtensionName());
            }
            return false;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running update scripts of the extension " + context.getExtensionName());
        }
        final boolean wasThereAnError = this.runner.run(context, scriptsToBeRun);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Finished running update scripts of the extension " + context.getExtensionName());
        }
        return wasThereAnError;
    }

    /**
     * Check if the last executed deployment scripts was successful.
     *
     * @return true if the last deployment script was successful.
     */
    @Override
    public boolean wasLastScriptSuccessful() {
        if (!this.extensionHelper.isDeploymentManagerExtensionTurnedOn()) {
            return true;
        }
        return this.scriptExecutionDao.wasLastScriptSuccessful();
    }

}
