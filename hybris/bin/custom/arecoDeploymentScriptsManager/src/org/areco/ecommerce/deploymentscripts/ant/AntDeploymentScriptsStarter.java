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
package org.areco.ecommerce.deploymentscripts.ant;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * It runs any pending UPDATE deployment scripts when the ant target "runDeploymentScripts" is called.
 * <p>
 * WARNING: The pending scripts must not require changes of the Hybris Type System. You would have to run them during the Update Running System process if this
 * is the case.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
@Service("antDeploymentScriptsStarter")
@Scope("tenant")
public class AntDeploymentScriptsStarter {
    public static final String STOP_ANT_ON_ERROR_CONF = "deploymentscripts.stopantonerror";
    
    private static final Logger LOG = LoggerFactory.getLogger(AntDeploymentScriptsStarter.class);

    @Autowired
    private DeploymentScriptStarter deploymentScriptStarter;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Run any deployment script which wasn't run yet. Depending on the configuration, throw an exception to stop the build process if there
     * was an error.
     *
     * @throws DeploymentScriptFailureException if there was an error and the build process must be stopped
     */
    public void runPendingScriptsAndThrowExceptionIfThereWasAnError() {
        if (this.runPendingScripts()) {
            return;
        }
        throwExceptionToStopAntBuildOrLogMessage("There were errors running the deployment scripts. Check the logs");
    }

    private void throwExceptionToStopAntBuildOrLogMessage(final String message) {
        if (configurationService.getConfiguration().getBoolean(STOP_ANT_ON_ERROR_CONF, true)) {
            throw new DeploymentScriptFailureException(message);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("There were errors but the ant process must continue. {}", message);
        }
    }

    /**
     * Run any deployment script which wasn't run yet. The method is called by the ant script.
     *
     * @return true if everything went ok.
     */
    private boolean runPendingScripts() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Running any pending UPDATE deployment scripts.");
        }
        final boolean wasThereAnError = sessionService.executeInLocalView(new SessionExecutionBody() {
            @Override
            public Object execute() {
                return deploymentScriptStarter.runAllPendingScripts();
            }
        }, userService.getAdminUser());

        if (wasThereAnError) {
            LOG.error("There was an error running the deployment scripts. Please check the console.");
            return false; // Error
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("All pending scripts were run successfully.");
        }
        return true;
    }

    /**
     * Check if the last executed deployment script had an error and throw an exception if the ant build must be stopped.
     *
     * @throws DeploymentScriptFailureException if there was an error and the build process must be stopped
     */
    public void stopAntBuildIfTheLastScriptFailed() {
        if (this.deploymentScriptStarter.wasLastScriptSuccessful()) {
            return;
        }
        throwExceptionToStopAntBuildOrLogMessage("The last deployment script failed");
    }
}
