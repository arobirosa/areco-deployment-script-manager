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
import de.hybris.platform.constants.CoreConstants;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.exceptions.ConfigurationException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptFinder;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptRunner;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptService;
import org.areco.ecommerce.deploymentscripts.core.ImpexImportService;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * Default implementation of the deployment script service.
 * 
 * @author arobirosa
 * 
 */
@Service
@Scope("tenant")
public class ArecoDeploymentScriptService implements DeploymentScriptService
{
	private static final Logger LOG = Logger.getLogger(ArecoDeploymentScriptService.class);

	private static final String RESOURCES_FOLDER = "/resources";

	private static final String INITIAL_CONFIGURATION_FILE = "initial-configuration.impex";

	@Autowired
	private DeploymentScriptFinder finder;

	@Autowired
	private DeploymentScriptRunner runner;

	@Autowired
	private ScriptExecutionResultDAO scriptExecutionResultDAO;

	@Autowired
	private ImpexImportService impexImportService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.areco.ecommerce.deploymentscripts.core.DeploymentScriptService#runUpdateDeploymentScripts(de.hybris.platform
	 * .core.initialization.SystemSetupContext)
	 */
	@Override
	public void runUpdateDeploymentScripts(final SystemSetupContext context)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("context", context);
		context.getJspContext().println("Running update scripts of the extension " + context.getExtensionName());

		checkAndImportInitialConfiguratonOfTheExtension(context);

		final List<DeploymentScript> scriptsToBeRun = this.finder.getPendingScripts(context.getExtensionName(),
				context.getProcess());
		if (scriptsToBeRun.isEmpty())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("There aren't any pending deployment scripts in the extension " + context.getExtensionName());
				return;
			}
		}
		this.runner.run(scriptsToBeRun);
		context.getJspContext().println("Finished running update scripts of the extension " + context.getExtensionName());
	}

	/**
	 * It checks if the initial configuration of the extension was imported. If it wasn't, it import it.
	 * 
	 * @param context
	 *           Required
	 */
	private void checkAndImportInitialConfiguratonOfTheExtension(final SystemSetupContext context)
	{
		if (!isFirstExtension(context))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("This is not the first extension. Quitting without checking if the initial "
						+ " configuration was imported.");
			}
		}
		if (this.scriptExecutionResultDAO.theInitialResultsWereImported())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("The initial configuration was already imported.");
			}
			return;
		}

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

	private boolean isFirstExtension(final SystemSetupContext context)
	{
		//There must be a better way to find out which one is the first extension
		return CoreConstants.EXTENSIONNAME.equalsIgnoreCase(context.getExtensionName());
	}

}
