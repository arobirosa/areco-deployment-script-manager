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
package org.areco.ecommerce.deploymentscripts.testhelper;

import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.Config;

import org.areco.ecommerce.deploymentscripts.core.impl.ArecoDeploymentScriptFinder;
import org.areco.ecommerce.deploymentscripts.core.impl.FlexibleSearchDeploymentEnvironmentDAO;
import org.areco.ecommerce.deploymentscripts.impex.impl.LocalizedImpexImportService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * It modifies the configuration of the properties during a test and restore them at the end of it.
 * 
 * @author arobirosa
 * 
 */
@Component
@Scope("tenant")
public class DeploymentConfigurationSetter {

    private boolean oldConfigurationWasSaved = false;

    private String oldResourcesFolder = null;

    private String oldUpdateScriptsFolder = null;

    private String oldInitScriptsFolder = null;

    private String oldEnvironmentName = null;

    private String oldImpexLocaleCode = null;

    /**
     * Save the current configuration and sets the folders to the given values.
     * 
     * @param testResourcesFolder
     *            Required
     * @param testUpdateScriptsFolder
     *            Optional
     * @param testInitScriptsFolder
     *            Optional
     */

    public void setTestFolders(final String testResourcesFolder, final String testUpdateScriptsFolder, final String testInitScriptsFolder) {
        ServicesUtil.validateParameterNotNullStandardMessage("testResourcesFolder", testResourcesFolder);
        this.saveCurrentFolders();

        Config.setParameter(ArecoDeploymentScriptFinder.RESOURCES_FOLDER_CONF, testResourcesFolder);

        if (testUpdateScriptsFolder != null) {
            Config.setParameter(ArecoDeploymentScriptFinder.UPDATE_SCRIPTS_FOLDER_CONF, testUpdateScriptsFolder);
        }
        if (testInitScriptsFolder != null) {
            Config.setParameter(ArecoDeploymentScriptFinder.INIT_SCRIPTS_FOLDER_CONF, testInitScriptsFolder);
        }
    }

    /**
     * This method may be called at the beginning of the test. It isn't mandatory.
     */
    public void saveCurrentFolders() {
        if (!this.oldConfigurationWasSaved) {
            this.oldResourcesFolder = Config.getParameter(ArecoDeploymentScriptFinder.RESOURCES_FOLDER_CONF);
            this.oldUpdateScriptsFolder = Config.getParameter(ArecoDeploymentScriptFinder.UPDATE_SCRIPTS_FOLDER_CONF);
            this.oldInitScriptsFolder = Config.getParameter(ArecoDeploymentScriptFinder.INIT_SCRIPTS_FOLDER_CONF);
            this.oldEnvironmentName = Config.getParameter(FlexibleSearchDeploymentEnvironmentDAO.CURRENT_ENVIRONMENT_CONF);
            this.oldImpexLocaleCode = Config.getParameter(LocalizedImpexImportService.IMPEX_LOCALE_CONF);
            this.oldConfigurationWasSaved = true;
        }

    }

    /**
     * Restores the original configuration.
     */
    public void restoreOldFolders() {
        Config.setParameter(ArecoDeploymentScriptFinder.RESOURCES_FOLDER_CONF, oldResourcesFolder);
        Config.setParameter(ArecoDeploymentScriptFinder.UPDATE_SCRIPTS_FOLDER_CONF, oldUpdateScriptsFolder);
        Config.setParameter(ArecoDeploymentScriptFinder.INIT_SCRIPTS_FOLDER_CONF, oldInitScriptsFolder);
        Config.setParameter(FlexibleSearchDeploymentEnvironmentDAO.CURRENT_ENVIRONMENT_CONF, oldEnvironmentName);
        Config.setParameter(LocalizedImpexImportService.IMPEX_LOCALE_CONF, oldImpexLocaleCode);
    }

    /**
     * Sets the name of the current environment.
     * 
     * @param currentEnvironmentName
     *            Can be null.
     */
    public void setEnvironment(final String currentEnvironmentName) {
        Config.setParameter(FlexibleSearchDeploymentEnvironmentDAO.CURRENT_ENVIRONMENT_CONF, currentEnvironmentName);
    }

    /**
     * Sets the code of the impex locale.
     * 
     * @param impexLocaleCode
     *            Required
     */
    public void setImpexLocaleCode(final String impexLocaleCode) {
        Config.setParameter(LocalizedImpexImportService.IMPEX_LOCALE_CONF, impexLocaleCode);
    }
}
