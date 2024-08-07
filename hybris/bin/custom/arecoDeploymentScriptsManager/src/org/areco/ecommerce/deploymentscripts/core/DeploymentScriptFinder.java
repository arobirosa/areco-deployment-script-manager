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

import de.hybris.platform.core.initialization.SystemSetup.Process;

import java.util.List;

/**
 * It looks for the deployment scripts.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
public interface DeploymentScriptFinder {

    /**
     * Returns the list of deployment scripts located in the extension which must be run.
     *
     * @param extensionName  Required
     * @param process        Required. It is the current Hybris process: Update or Init.
     * @param runInitScripts Required. Do we run the init or the update scripts.
     * @return Never null
     */
    List<DeploymentScript> getPendingScripts(String extensionName, Process process, boolean runInitScripts);

    /**
     * Returns the list of names of deployment script directories which exists in the extension. Old and new executed scripts
     * are returned.
     *
     * @param extensionName  Required
     * @param runInitScripts Required. Do we run the init or the update scripts.
     * @return Never null
     */
    List<String> getExistingScriptDirectoryNames(String extensionName, boolean runInitScripts);
}
