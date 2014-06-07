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

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * It triggers the execution of the deployment scripts.
 * 
 * @author arobirosa
 * 
 */
@Service
@Scope("tenant")
@SystemSetup(extension = "ALL_EXTENSIONS")
public class DeploymentScriptStarter
{
	private static final Logger LOG = Logger.getLogger(DeploymentScriptStarter.class);

	@Autowired
	private DeploymentScriptService deploymentScriptService;

	//We hook the essential data proceess. Due to this the deployment scripts could be run using
	//"ant updatessystem".
	@SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.ALL)
	public void runUpdateDeploymentScripts(final SystemSetupContext context)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(context.getType().toString() + "Running the deployment scripts of the extension: "
					+ context.getExtensionName());
		}
		try
		{
			this.deploymentScriptService.runUpdateDeploymentScripts(context);
		}
		catch (final RuntimeException re)
		{
			//We improve the error logging in case of a runtime exception.
			LOG.error("There was an error running the deployment scripts: " + re.getLocalizedMessage(), re);
			throw re;
		}
	}
}
