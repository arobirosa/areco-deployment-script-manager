/**
 * Copyright 2017 Antonio Robirosa
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.backoffice.jalo;

/**
 * This is the extension manager of the Ybackoffice extension.
 */
public class ArecodeploymentscriptsbackofficeManager extends GeneratedArecodeploymentscriptsbackofficeManager {
    /*
     * Some important tips for development:
     *
     * Do NEVER use the default constructor of manager's or items. => If you want to do something whenever the manger is created
     * use the init() or destroy() methods described below
     *
     * Do NEVER use STATIC fields in your manager or items! => If you want to cache anything in a "static" way, use an instance
     * variable in your manager, the manager is created only once in the lifetime of a "deployment" or tenant.
     */

}
