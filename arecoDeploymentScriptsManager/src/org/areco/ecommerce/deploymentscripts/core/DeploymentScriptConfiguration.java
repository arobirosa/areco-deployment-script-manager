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

import de.hybris.platform.core.Tenant;

import java.util.List;


/**
 * It defines special properties of the deployment scripts like where they are allowed to run. It is inmutable.
 * 
 * @author arobirosa
 * 
 */
public class DeploymentScriptConfiguration
{
	/* The existent of the tenants is validated during the creation of the configuration. */
	private final List<Tenant> allowedTenants;
	/*
	 * It contains the names of the environment because we validate the existenz of it just before running the script.
	 */
	private final List<String> allowedDeploymentEnvironmentNames;

	public DeploymentScriptConfiguration()
	{
		this(null, null);
	}

	public DeploymentScriptConfiguration(final List<Tenant> someTenants, final List<String> someDeploymentEnvironmentNames)
	{
		this.allowedDeploymentEnvironmentNames = someDeploymentEnvironmentNames;
		this.allowedTenants = someTenants;
	}

	protected List<Tenant> getAllowedTenants()
	{
		return allowedTenants;
	}

	protected List<String> getAllowedDeploymentEnvironmentNames()
	{
		return allowedDeploymentEnvironmentNames;
	}



}
