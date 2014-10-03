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

/**
 * Describes the contract of the core service.
 * 
 * @author arobirosa
 * 
 */
public interface DeploymentScriptService {
    /**
     * It runs the deployment scripts during an update running system.
     * 
     * @param context
     *            Required
     * @param runInitScripts
     *            Required. Do we run the INIT or the UPDATE scripts?
     * @return boolean True if there was an error.
     */
    boolean runDeploymentScripts(UpdatingSystemExtensionContext context, boolean runInitScripts);
}
