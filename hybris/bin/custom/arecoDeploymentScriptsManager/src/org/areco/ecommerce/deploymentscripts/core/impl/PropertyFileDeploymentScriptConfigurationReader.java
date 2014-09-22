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

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptConfigurationException;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptConfigurationReader;


/**
 * It reads the configuration contained in a property file with the extension conf in the folder of the script.
 * 
 * @author arobirosa
 * 
 */
//The configuration of this bean is in the spring application context.
public abstract class PropertyFileDeploymentScriptConfigurationReader implements DeploymentScriptConfigurationReader
{

	private static final Logger LOG = Logger.getLogger(PropertyFileDeploymentScriptConfigurationReader.class);
	/**
	 * Allowed environments
	 */
	private static final String RUN_ONLY_ON_ENVIRONMENTS_PROPERTY = "runonlyonenvironments";
	/**
	 * Allowed tenants
	 */
	private static final String RUN_ONLY_ON_TENANTS_PROPERTY = "runonlyontenants";
	/**
	 * Separator of tenant and environment names.
	 */
	private static final String VALUES_SEPARATOR = ",";

	/**
	 * Extension of the configuration files
	 */
	private static final String PROPERTY_FILE_EXTENSION_CONF = ".conf";

	/*
	 * { @InheritDoc }
	 */
	@Override
	public PropertyFileDeploymentScriptConfiguration loadConfiguration(final File deploymentScriptFolder)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Reading configuration from the directory " + deploymentScriptFolder);
		}
		ServicesUtil.validateParameterNotNullStandardMessage("deploymentScriptFolder", deploymentScriptFolder);

		final File configurationFile = this.findConfigurationFile(deploymentScriptFolder);
		if (configurationFile == null)
		{
			return this.createConfiguration(); //Default configuration
		}
		return this.createConfigurationFrom(configurationFile);
	}

	private PropertyFileDeploymentScriptConfiguration createConfigurationFrom(final File configurationFile)
	{
		final Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(configurationFile));
		}
		catch (final FileNotFoundException e)
		{
			throw new DeploymentScriptConfigurationException(e);
		}
		catch (final IOException e)
		{
			throw new DeploymentScriptConfigurationException(e);
		}
		final Set<Tenant> tenants = getAllowedTenants(properties);
		final Set<String> environmentNames = getAllowedDeploymentEnvironments(properties);
		final PropertyFileDeploymentScriptConfiguration newConfiguration = this.createConfiguration();
		newConfiguration.setAllowedTenants(tenants);
		newConfiguration.setAllowedDeploymentEnvironmentNames(environmentNames);
		return newConfiguration;
	}

	private Set<String> getAllowedDeploymentEnvironments(final Properties properties)
	{
		final String environmentsList = properties.getProperty(RUN_ONLY_ON_ENVIRONMENTS_PROPERTY);
		if (environmentsList == null)
		{
			return null;
		}
		return new HashSet(Arrays.asList(environmentsList.split(VALUES_SEPARATOR)));
	}

	private Set<Tenant> getAllowedTenants(final Properties properties)
	{
		final String tenantList = properties.getProperty(RUN_ONLY_ON_TENANTS_PROPERTY);
		if (tenantList == null)
		{
			return null;
		}
		return convertTenants(tenantList);
	}

	private Set<Tenant> convertTenants(final String tenantNamesList)
	{
		final Set<Tenant> tenants = new HashSet<Tenant>();
		for (final String aTenantName : tenantNamesList.split(VALUES_SEPARATOR))
		{
			final Tenant foundTenant = Registry.getTenantByID(aTenantName);
			if (foundTenant == null)
			{
				throw new DeploymentScriptConfigurationException("Unable to find the tenant with the ID '" + aTenantName + "'.");
			}
			tenants.add(foundTenant);
		}
		return tenants;
	}

	private File findConfigurationFile(final File deploymentScriptFolder)
	{
		final File[] configurationFiles = deploymentScriptFolder.listFiles(new FileFilter()
		{
			/*
			 * { @InheritDoc }
			 */
			@Override
			public boolean accept(final File pathname)
			{
				return pathname.getName().toLowerCase().endsWith(PROPERTY_FILE_EXTENSION_CONF);
			}
		});
		if (LOG.isTraceEnabled())
		{
			LOG.trace("Found configuration files: " + Arrays.toString(configurationFiles));
		}
		if (configurationFiles.length == 0)
		{
			return null;
		}
		else if (configurationFiles.length == 1)
		{
			return configurationFiles[0];
		}
		throw new DeploymentScriptConfigurationException("The folder " + deploymentScriptFolder.getAbsolutePath()
				+ " contains multiply configuration files. Please leave only one.");

	}

	//Used by Spring to create new instances.
	protected abstract PropertyFileDeploymentScriptConfiguration createConfiguration();
}
