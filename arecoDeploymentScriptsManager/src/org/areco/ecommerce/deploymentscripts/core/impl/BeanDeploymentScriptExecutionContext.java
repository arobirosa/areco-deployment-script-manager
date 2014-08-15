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

import java.util.Set;

import org.areco.ecommerce.deploymentscripts.core.DeploymentEnvironmentDAO;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionContext;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDAO;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Implementation which is a Spring Bean
 * 
 * @author arobirosa
 * 
 */
@Component
@Scope("tenant")
public class BeanDeploymentScriptExecutionContext implements DeploymentScriptExecutionContext
{
	@Autowired
	private ScriptExecutionResultDAO scriptExecutionResultDAO;

	@Autowired
	private DeploymentEnvironmentDAO deploymentEnvironmentDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionContext#getSuccessResult()
	 */
	@Override
	public ScriptExecutionResultModel getSuccessResult()
	{
		return this.scriptExecutionResultDAO.getSuccessResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionContext#getIgnoredOtherEnvironmentResult()
	 */
	@Override
	public ScriptExecutionResultModel getIgnoredOtherEnvironmentResult()
	{
		return this.scriptExecutionResultDAO.getIgnoredOtherEnvironmentResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionContext#getIgnoredOtherTenantResult()
	 */
	@Override
	public ScriptExecutionResultModel getIgnoredOtherTenantResult()
	{
		return this.scriptExecutionResultDAO.getIgnoredOtherTenantResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionContext#getCurrentTenant()
	 */
	@Override
	public Tenant getCurrentTenant()
	{
		return Registry.getCurrentTenant();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionContext#isCurrentEnvironmentIn(java.util.Set)
	 */
	@Override
	public boolean isCurrentEnvironmentIn(final Set<String> allowedDeploymentEnvironmentNames)
	{
		return this.deploymentEnvironmentDAO.loadEnvironments(allowedDeploymentEnvironmentNames).contains(
				this.deploymentEnvironmentDAO.getCurrent());
	}

}
