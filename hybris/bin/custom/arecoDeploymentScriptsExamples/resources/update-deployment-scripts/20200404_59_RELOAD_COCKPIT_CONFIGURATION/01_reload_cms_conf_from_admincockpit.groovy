/**
 * Copyright 2020 Antonio Robirosa

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
package org.areco.ecommerce.deploymentscripts.beanshell.util

import de.hybris.platform.cockpit.systemsetup.CockpitImportConfig
import de.hybris.platform.core.Registry
import de.hybris.platform.core.initialization.SystemSetupContext
import org.apache.log4j.Logger
/**
 * It imports again all the cockpit configuration of an extension. This class could be used in the beanshell scripts.
 *
 * @author arobirosa
 *
 */
class CockpitConfigurationReloader {

    private static final Logger LOG = Logger.getLogger(CockpitConfigurationReloader.class);

    /**
     * It imports again the cockpit configuration of the given extension.
     *
     * @param anExtensionName
     *            Required.
     */
    void reloadCockpitConfiguration(final String anExtensionName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reloading the cockpit configuration of the extension '" + anExtensionName + "'.");
        }
        CockpitImportConfig importer = Registry.getApplicationContext().getBean(CockpitImportConfig.class);



        // We use the context to store the name of the extension.
        final SystemSetupContext context = new SystemSetupContext(null, null, null, anExtensionName);
        importer.importCockpitConfig(context);
    }

}

final String extensionName = "arecoDeploymentScriptsManager";

try {
    CockpitConfigurationReloader reloader = new CockpitConfigurationReloader()
    reloader.reloadCockpitConfiguration(extensionName);
} catch (Exception e) {
    System.err.println("There was an error:");
    e.printStackTrace(System.err);
    return "Error, please see the console of the app server";
}
return "OK"; //We muss return 'ok'
