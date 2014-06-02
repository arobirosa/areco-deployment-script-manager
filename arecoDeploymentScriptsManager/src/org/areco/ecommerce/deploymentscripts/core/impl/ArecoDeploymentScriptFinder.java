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
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptFinder;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionDao;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * Default implementation of the deployment script finder.
 * 
 * @author arobirosa
 * 
 */
@Service
@Scope("tenant")
public class ArecoDeploymentScriptFinder implements DeploymentScriptFinder
{
	private static final Logger LOG = Logger.getLogger(ArecoDeploymentScriptFinder.class);

	private static final String SCRIPTS_FOLDER_CONF = "deploymentscripts.update.folder";

	private static final String RESOURCES_FOLDER = "/resources";

	@Autowired
	private ScriptExecutionDao scriptExecutionDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptFinder#getPendingScripts(java.lang.String)
	 */
	@Override
	public List<DeploymentScript> getPendingScripts(final String extensionName)
	{
		ServicesUtil.validateParameterNotNullStandardMessage(extensionName, extensionName);
		final List<File> pendingScriptsFolders = getScriptsToBeRun(extensionName);
		return getDeploymentScripts(pendingScriptsFolders, extensionName);
	}

	private List<File> getScriptsToBeRun(final String extensionName)
	{
		final List<String> alreadyExecutedScripts = getAlreadyExecutedScripts(extensionName);

		final List<File> pendingScriptsFolders = new ArrayList<File>();
		for (final File foundScriptFolder : getExistentScripts(extensionName))
		{
			if (!alreadyExecutedScripts.contains(foundScriptFolder.getName()))
			{
				pendingScriptsFolders.add(foundScriptFolder);
			}
		}
		return pendingScriptsFolders;
	}

	private List<String> getAlreadyExecutedScripts(final String extensionName)
	{
		final List<String> alreadyExecutedScripts = new ArrayList<String>();
		for (final ScriptExecutionModel alreadyExecutedScript : this.scriptExecutionDao
				.getSuccessfullyExecutedScripts(extensionName))
		{
			alreadyExecutedScripts.add(alreadyExecutedScript.getScriptName());
		}
		return alreadyExecutedScripts;
	}

	/**
	 * @param extensionName
	 * @return
	 */
	private File[] getExistentScripts(final String extensionName)
	{
		final ExtensionInfo extension = ConfigUtil.getPlatformConfig(ArecoDeploymentScriptFinder.class).getExtensionInfo(
				extensionName);
		final String scriptsFolderName = Config.getParameter(SCRIPTS_FOLDER_CONF);

		final File deploymentScriptFolder = new File(extension.getExtensionDirectory() + RESOURCES_FOLDER + File.pathSeparatorChar
				+ scriptsFolderName, "");

		final File[] scriptsFolders = deploymentScriptFolder.listFiles(new FileFilter()
		{

			@Override
			public boolean accept(final File pathname)
			{
				return pathname.isDirectory();
			}
		});
		return scriptsFolders;
	}

	/**
	 * Converts the given folders to deployment script instances.
	 * 
	 * @param pendingScriptsFolders
	 *           Required
	 * @param extensionName
	 *           Required
	 * @return Never null
	 */
	private List<DeploymentScript> getDeploymentScripts(final List<File> pendingScriptsFolders, final String extensionName)
	{
		final List<DeploymentScript> newDeploymentScripts = new ArrayList<DeploymentScript>();

		for (final File pendingScriptsFolder : pendingScriptsFolders)
		{
			final DeploymentScript newScript = createDeploymentScript(pendingScriptsFolder, extensionName);
			if (newScript != null)
			{
				newDeploymentScripts.add(newScript);
			}
		}
		return newDeploymentScripts;
	}

	private DeploymentScript createDeploymentScript(final File pendingScriptsFolder, final String extensionName)
	{
		final List<DeploymentScriptStep> orderedSteps = createOrderedSteps(pendingScriptsFolder);

		if (orderedSteps.isEmpty())
		{
			return null;
		}
		final DeploymentScript newScript = new DeploymentScript();
		newScript.setName(pendingScriptsFolder.getName());
		newScript.setExtensionName(extensionName);
		newScript.setOrderedSteps(orderedSteps);
		return newScript;
	}

	private List<DeploymentScriptStep> createOrderedSteps(final File scriptFolder)
	{
		final List<DeploymentScriptStep> steps = new ArrayList<DeploymentScriptStep>();

		final File[] impexFiles = scriptFolder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(final File dir, final String name)
			{
				return name.toLowerCase().endsWith(".impex");
			}
		});

		for (final File impexFile : impexFiles)
		{
			steps.add(new ImpexImportStep(impexFile));
		}
		return steps;
	}
}
