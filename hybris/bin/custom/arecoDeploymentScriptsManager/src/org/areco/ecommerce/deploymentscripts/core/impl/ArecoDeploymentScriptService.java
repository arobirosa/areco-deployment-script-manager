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

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.areco.ecommerce.deploymentscripts.core.ArecoInitialConfigurationImporter;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptFinder;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptRunner;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptService;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionDao;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDao;
import org.areco.ecommerce.deploymentscripts.core.UpdatingSystemExtensionContext;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.areco.ecommerce.deploymentscripts.systemsetup.ExtensionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * Default implementation of the deployment script service.
 *
 * @author arobirosa
 */
public class ArecoDeploymentScriptService implements DeploymentScriptService {
    private static final Logger LOG = LoggerFactory.getLogger(ArecoDeploymentScriptService.class);

    @Resource
    private DeploymentScriptFinder deploymentScriptFinder;

    @Resource
    private DeploymentScriptRunner deploymentScriptRunner;

    @Resource
    private ArecoInitialConfigurationImporter arecoInitialConfigurationImporter;

    @Resource
    private ScriptExecutionDao scriptExecutionDao;

    @Resource
    private ScriptExecutionResultDao scriptExecutionResultDao;

    @Resource
    private ExtensionHelper extensionHelper;

    @Resource
    private ModelService modelService;

    /*
     * (non-Javadoc)
     *
     * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptService#runUpdateDeploymentScripts(de.hybris.platform
     * .core.initialization.SystemSetupContext)
     */
    @Override
    public boolean runDeploymentScripts(final UpdatingSystemExtensionContext context, final boolean runInitScripts) {
        ServicesUtil.validateParameterNotNullStandardMessage("context", context);
        if (this.extensionHelper.isDeploymentManagerExtensionTurnedOff()) {
            return false;
        }
        this.arecoInitialConfigurationImporter.importConfigurationIfRequired(context);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Looking for pending update scripts in the extension {}", context.getExtensionName());
        }
        final List<DeploymentScript> scriptsToBeRun = this.deploymentScriptFinder.getPendingScripts(
                context.getExtensionName(), context.getProcess(), runInitScripts);
        boolean wasThereAnError = false;
        if (scriptsToBeRun.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("There aren't any pending {} deployment scripts in the extension {}", runInitScripts ? "INIT" : "UPDATE", context.getExtensionName());
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Running update scripts of the extension {}", context.getExtensionName());
            }
            wasThereAnError = this.deploymentScriptRunner.run(context, scriptsToBeRun);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Finished running update scripts of the extension {}", context.getExtensionName());
            }
        }
        if (!runInitScripts) {
            updateExecutionsOfScriptRemovedOnDisk(context.getExtensionName());
        }
        return wasThereAnError;
    }

    private void updateExecutionsOfScriptRemovedOnDisk(final String extensionName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Looking for update scripts which were removed on disk in the extension {}", extensionName);
        }
        final List<String> existingUpdateScriptDirectories = deploymentScriptFinder.getExistingScriptDirectoryNames(extensionName, false);
        for (final ScriptExecutionModel pendingExecution : scriptExecutionDao.findErrorOrPendingExecutionsOnMostRecentOrder(extensionName)) {
            if (!existingUpdateScriptDirectories.contains(pendingExecution.getScriptName())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Setting {} as removed on disk", pendingExecution.getScriptName());
                }
                pendingExecution.setResult(scriptExecutionResultDao.getIgnoredRemovedOnDisk());
                this.modelService.save(pendingExecution);
            }
            if (scriptExecutionResultDao.getErrorResult().equals(pendingExecution.getResult())) {
                // Keep the most recent execution with an error of a deployment script which exists on disk.
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Leaving the failed execution of {} untouched", pendingExecution.getScriptName());
                }
                return;
            }
        }
    }

    /**
     * Check if the last executed deployment scripts was successful.
     *
     * @return true if the last deployment script was successful.
     */
    @Override
    public boolean wasLastScriptSuccessful() {
        if (this.extensionHelper.isDeploymentManagerExtensionTurnedOff()) {
            return true;
        }
        return this.scriptExecutionDao.wasLastScriptSuccessful();
    }

}
