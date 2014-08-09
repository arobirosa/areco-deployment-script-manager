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
package org.areco.ecommerce.deploymentscripts.core.impl;

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.exceptions.ConfigurationException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.io.File;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants;
import org.areco.ecommerce.deploymentscripts.core.InitialConfigurationImporter;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDAO;
import org.areco.ecommerce.deploymentscripts.core.UpdatingSystemExtensionContext;
import org.areco.ecommerce.deploymentscripts.impex.ImpexImportService;
import org.areco.ecommerce.deploymentscripts.systemsetup.ExtensionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * This implementation uses an impex script to create the initial objects.
 * 
 * @author arobirosa
 * 
 */
@Scope("tenant")
@Service
public class ImpexInitialConfigurationImporter implements InitialConfigurationImporter
{

	private static final Logger LOG = Logger.getLogger(ImpexInitialConfigurationImporter.class);

	private static final String RESOURCES_FOLDER = "/resources";

	private static final String INITIAL_CONFIGURATION_FILE = "initial-configuration.impex";

	@Autowired
	private ScriptExecutionResultDAO scriptExecutionResultDAO;

	@Autowired
	private ImpexImportService impexImportService;

	@Autowired
	private ExtensionHelper extensionHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.areco.ecommerce.deploymentscripts.core.InitialConfigurationImporter#importConfigurationIfRequired(de.hybris
	 * .platform.core.initialization.SystemSetupContext)
	 */
	@Override
	public void importConfigurationIfRequired(final UpdatingSystemExtensionContext context)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("context", context);
		if (!this.extensionHelper.isFirstExtension(context))
		{
			if (LOG.isTraceEnabled())
			{
				LOG.trace("This is not the first extension. Quitting without checking if the initial "
						+ " configuration was imported.");
			}
			return;
		}
		if (this.scriptExecutionResultDAO.theInitialResultsWereImported())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("The initial configuration was already imported.");
			}
			return;
		}
		importConfiguration();
	}

	/**
	 * 
	 */
	private void importConfiguration()
	{
		final ExtensionInfo extension = ConfigUtil.getPlatformConfig(ArecoDeploymentScriptService.class).getExtensionInfo(
				ArecoDeploymentScriptsManagerConstants.EXTENSIONNAME);
		final File configurationFile = new File(extension.getExtensionDirectory() + RESOURCES_FOLDER, INITIAL_CONFIGURATION_FILE);
		try
		{
			this.impexImportService.importImpexFile(configurationFile);
		}
		catch (final ImpExException cause)
		{
			throw new ConfigurationException("There was an error importing the initial configuration of the extension stored in "
					+ configurationFile, cause);
		}

		//Reload of the results.
		this.scriptExecutionResultDAO.initialize();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("The initial configuration was sucessfully imported.");
		}
	}

}
