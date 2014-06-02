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

import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.List;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptFinder;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptRunner;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptService;
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

	@Autowired
	private DeploymentScriptFinder finder;

	@Autowired
	private DeploymentScriptRunner runner;

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
		final List<DeploymentScript> scriptsToBeRun = this.finder.getPendingScripts(context.getExtensionName());
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

}
