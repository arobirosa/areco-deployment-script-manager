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

import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * It runs any pending UPDATE deployment scripts when the ant target "runDeploymentScripts" is called.
 *
 * WARNING: The pending scripts must not require changes of the Hybris Type System. You would have to run them during the Update Running System process if this
 * is the case.
 *
 * @author arobirosa
 *
 */
@Service("antDeploymentScriptsStarter")
@Scope("tenant")
public class AntDeploymentScriptsStarter {
    private static final Logger LOG = Logger.getLogger(AntDeploymentScriptsStarter.class);

    @Autowired
    private DeploymentScriptStarter deploymentScriptStarter;

    @Autowired
    private UserService userService;

    /**
     * Run any deployment script which wasn't run yet. The method is called by the ant script.
     *
     * @return 0 if everything went ok.
     */
    public int runPendingScripts() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Running any pending UPDATE deployment scripts.");
        }
        this.userService.setCurrentUser(this.userService.getAdminUser());

        final boolean wasThereAnError = this.deploymentScriptStarter.runAllPendingScripts();
        if (wasThereAnError) {
            LOG.error("There was an error running the deployment scripts. Please check the console.");
            return 1; // Error
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("All pending scripts were run successfully.");
        }
        return 0;
    }

    /**
     * Check if the last executed deployment scripts was successful.
     *
     * @return true if the last deployment script was successful.
     */
    public boolean wasLastScriptSuccessful() {
        return this.deploymentScriptStarter.wasLastScriptSuccessful();
    }
}
