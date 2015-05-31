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

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.impl.ArecoDeploymentScriptFinder;
import org.areco.ecommerce.deploymentscripts.core.impl.FlexibleSearchDeploymentEnvironmentDAO;
import org.areco.ecommerce.deploymentscripts.impex.impl.LocalizedImpexImportService;
import org.springframework.beans.factory.annotation.Autowired;
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
    /*
         * Logger of this class.
         */
    private static final Logger LOG = Logger.getLogger(DeploymentConfigurationSetter.class);

    private static String NO_INIT_SCRIPTS_FOLDER = "no-init-scripts";

    private boolean oldConfigurationWasSaved = false;

    private String oldResourcesFolder = null;

    private String oldUpdateScriptsFolder = null;

    private String oldInitScriptsFolder = null;

    private String oldEnvironmentName = null;

    private String oldImpexLocaleCode = null;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Save the current configuration and sets the folders to the given values.
     * 
     * @param testResourcesFolder
     *            Mandatory
     * @param testUpdateScriptsFolder
     *            Mandatory
     * @param testInitScriptsFolder
     *            Mandatory
     */

    public void setTestFolders(final String testResourcesFolder, final String testUpdateScriptsFolder, final String testInitScriptsFolder) {
        ServicesUtil.validateParameterNotNullStandardMessage("testResourcesFolder", testResourcesFolder);
        ServicesUtil.validateParameterNotNullStandardMessage("testUpdateScriptsFolder", testUpdateScriptsFolder);
        ServicesUtil.validateParameterNotNullStandardMessage("testInitScriptsFolder", testResourcesFolder);
        this.saveCurrentFolders();

        setConfigurationAndLog(ArecoDeploymentScriptFinder.RESOURCES_FOLDER_CONF, testResourcesFolder);
        setConfigurationAndLog(ArecoDeploymentScriptFinder.UPDATE_SCRIPTS_FOLDER_CONF, testUpdateScriptsFolder);
        setConfigurationAndLog(ArecoDeploymentScriptFinder.INIT_SCRIPTS_FOLDER_CONF, testInitScriptsFolder);
    }

    /**
     * Save the current configuration and sets the folders to the given values.
     *
     * @param testResourcesFolder
     *            Mandatory
     * @param testUpdateScriptsFolder
     *            Mandatory
     */
    public void setTestFolders(final String testResourcesFolder, final String testUpdateScriptsFolder) {
        setTestFolders(testResourcesFolder, testUpdateScriptsFolder, NO_INIT_SCRIPTS_FOLDER);
    }

    private Configuration getConfiguration() {
        return this.configurationService.getConfiguration();
    }

    /**
     * This method may be called at the beginning of the test. It isn't mandatory.
     */
    public void saveCurrentFolders() {
        if (!this.oldConfigurationWasSaved) {
            this.oldResourcesFolder = getConfiguration().getString(ArecoDeploymentScriptFinder.RESOURCES_FOLDER_CONF);
            this.oldUpdateScriptsFolder = getConfiguration().getString(ArecoDeploymentScriptFinder.UPDATE_SCRIPTS_FOLDER_CONF);
            this.oldInitScriptsFolder = getConfiguration().getString(ArecoDeploymentScriptFinder.INIT_SCRIPTS_FOLDER_CONF);
            this.oldEnvironmentName = getConfiguration().getString(FlexibleSearchDeploymentEnvironmentDAO.CURRENT_ENVIRONMENT_CONF);
            this.oldImpexLocaleCode = getConfiguration().getString(LocalizedImpexImportService.IMPEX_LOCALE_CONF);
            this.oldConfigurationWasSaved = true;
        }

    }

    /**
     * Restores the original configuration.
     */
    public void restoreOldFolders() {
        setConfigurationAndLog(ArecoDeploymentScriptFinder.RESOURCES_FOLDER_CONF, oldResourcesFolder);
        setConfigurationAndLog(ArecoDeploymentScriptFinder.UPDATE_SCRIPTS_FOLDER_CONF, oldUpdateScriptsFolder);
        setConfigurationAndLog(ArecoDeploymentScriptFinder.INIT_SCRIPTS_FOLDER_CONF, oldInitScriptsFolder);
        setConfigurationAndLog(FlexibleSearchDeploymentEnvironmentDAO.CURRENT_ENVIRONMENT_CONF, oldEnvironmentName);
        setConfigurationAndLog(LocalizedImpexImportService.IMPEX_LOCALE_CONF, oldImpexLocaleCode);
    }

    /**
     * Sets the name of the current environment.
     * 
     * @param currentEnvironmentName
     *            Can be null.
     */
    public void setEnvironment(final String currentEnvironmentName) {
        setConfigurationAndLog(FlexibleSearchDeploymentEnvironmentDAO.CURRENT_ENVIRONMENT_CONF, currentEnvironmentName);
    }

    /**
     * Sets the code of the impex locale.
     * 
     * @param impexLocaleCode
     *            Required
     */
    public void setImpexLocaleCode(final String impexLocaleCode) {
        setConfigurationAndLog(LocalizedImpexImportService.IMPEX_LOCALE_CONF, impexLocaleCode);
    }

    private void setConfigurationAndLog(final String key, final String value) {
        LOG.debug("Setting the configuration key '" + key + "' with the value '" + value + "'");
        this.getConfiguration().setProperty(key, value);
    }
}
