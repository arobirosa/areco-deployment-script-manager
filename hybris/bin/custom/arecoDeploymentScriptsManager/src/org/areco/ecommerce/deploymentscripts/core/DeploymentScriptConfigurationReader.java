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

import java.io.File;

/**
 * It is responsible for loading the configuration of the deployment script.
 *
 * @author Antonio Robirosa <mailto:areco.manager@areko.consulting>
 */
public interface DeploymentScriptConfigurationReader {
    /**
     * Reads the folder and returns a configuration for the script.
     *
     * @param deploymentScriptFolder Required.
     * @return Never null
     */
    DeploymentScriptConfiguration loadConfiguration(File deploymentScriptFolder);
}
