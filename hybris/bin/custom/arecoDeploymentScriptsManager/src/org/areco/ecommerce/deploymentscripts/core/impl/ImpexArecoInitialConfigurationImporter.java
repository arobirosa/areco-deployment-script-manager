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

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants;
import org.areco.ecommerce.deploymentscripts.core.ArecoInitialConfigurationImporter;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDao;
import org.areco.ecommerce.deploymentscripts.core.UpdatingSystemExtensionContext;
import org.areco.ecommerce.deploymentscripts.impex.ImpexImportService;
import org.areco.ecommerce.deploymentscripts.systemsetup.ExtensionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;

/**
 * This implementation uses an impex script to create the initial objects.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
public class ImpexArecoInitialConfigurationImporter implements ArecoInitialConfigurationImporter {

    private static final Logger LOG = LoggerFactory.getLogger(ImpexArecoInitialConfigurationImporter.class);

    private static final String RESOURCES_FOLDER = "/resources";

    private static final String INITIAL_CONFIGURATION_FILE = "initial-configuration.impex";

    private static final String INITIAL_CONFIGURATION_FILE_GERMAN = "initial-configuration-de.impex";

    @Resource
    private ScriptExecutionResultDao scriptExecutionResultDao;

    @Autowired
    private ImpexImportService impexImportService;

    @Autowired
    private ExtensionHelper extensionHelper;

    @Autowired
    private CommonI18NService commonI18NService;

    /*
     * { @InheritDoc }
     */
    @Override
    public void importConfigurationIfRequired(final UpdatingSystemExtensionContext context) {
        ServicesUtil.validateParameterNotNullStandardMessage("context", context);
        if (!this.extensionHelper.isFirstExtension(context)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("This is not the first extension. Quitting without checking if the initial " + " configuration was imported.");
            }
            return;
        }
        if (this.scriptExecutionResultDao.theInitialResultsWereImported()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("The initial configuration was already imported.");
            }
            return;
        }
        importConfiguration();
    }

    private void importConfiguration() {
        this.importConfiguration(INITIAL_CONFIGURATION_FILE);

        try {
            if (this.commonI18NService.getLanguage("de") != null) {
                this.importConfiguration(INITIAL_CONFIGURATION_FILE_GERMAN);
            }
        } catch (final UnknownIdentifierException e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("The German language isn't installed.", e);
            }
        }

        // Reload of the results.
        this.scriptExecutionResultDao.initialize();

        if (LOG.isDebugEnabled()) {
            LOG.debug("The initial configuration was successfully imported.");
        }
    }

    private void importConfiguration(final String initialConfigurationFileGerman) {
        final ExtensionInfo extension = ConfigUtil.getPlatformConfig(ArecoDeploymentScriptService.class).getExtensionInfo(
                ArecoDeploymentScriptsManagerConstants.EXTENSIONNAME);
        final File configurationFile = new File(extension.getExtensionDirectory() + RESOURCES_FOLDER, initialConfigurationFileGerman);
        this.impexImportService.importImpexFile(configurationFile);
    }

}
