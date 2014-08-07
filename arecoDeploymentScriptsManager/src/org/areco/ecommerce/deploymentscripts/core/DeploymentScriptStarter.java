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

import de.hybris.platform.constants.CoreConstants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.util.JspContext;

import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.systemsetup.ExtensionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockJspWriter;
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

	@Autowired
	private ExtensionHelper extensionHelper;

	private boolean wasThereAnError = false;

	/**
	 * @return the wasThereAnError
	 */
	private boolean isWasThereAnError()
	{
		return wasThereAnError;
	}

	/**
	 * @param wasThereAnError
	 *           the wasThereAnError to set
	 */
	private void setWasThereAnError(final boolean wasThereAnError)
	{
		this.wasThereAnError = wasThereAnError;
	}

	/**
	 * This method is called by every extension during the update or init process.
	 * 
	 * We hook the essential data proceess. Due to this the deployment scripts could be run using "ant updatessystem".
	 * 
	 * @param context
	 *           Required
	 */

	@SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.ALL)
	public void runUpdateDeploymentScripts(final SystemSetupContext context)
	{
		this.runDeploymentScripts(context, false);
	}


	public void runDeploymentScripts(final SystemSetupContext context, final boolean runInitScripts)
	{
		if (this.extensionHelper.isFirstExtension(context))
		{
			this.setWasThereAnError(false);
			if (LOG.isTraceEnabled())
			{
				LOG.trace("The error flag was cleared");
			}
		}

		if (this.isWasThereAnError())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("There was an error running the deployment scripts of the previous extensions. "
						+ "Due to this the deployment scripts of the extension " + context.getExtensionName() + " will be ignored.");
			}
			return;
		}
		runDeploymentScriptsAndHandleErrors(context, runInitScripts);
	}

	private void runDeploymentScriptsAndHandleErrors(final SystemSetupContext context, final boolean runInitScripts)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Running the deployment scripts of the extension: " + context.getExtensionName());
		}

		try
		{
			this.setWasThereAnError(this.deploymentScriptService.runDeploymentScripts(context, runInitScripts));
		}
		catch (final RuntimeException re)
		{
			this.setWasThereAnError(true);
			//We improve the error logging in case of a runtime exception.
			LOG.error("There was an error running the deployment scripts: " + re.getLocalizedMessage(), re);
			throw re;
		}
	}

	/**
	 * Runs all the pending UPDATE deployment scripts.
	 * 
	 * @return boolean if there was an error.
	 */

	public boolean runAllPendingScripts()
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Running all pending update deployment scripts.");
		}
		return this.runAllPendingScripts(false);
	}

	private boolean runAllPendingScripts(final boolean runInitScripts)
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Running all deployment scripts. RunInitScripts? " + runInitScripts);
		}
		final JspContext aJspContext = new JspContext(new MockJspWriter(new StringWriter()), null, null);
		for (final String extensionName : Registry.getMasterTenant().getTenantSpecificExtensionNames())
		{
			final SystemSetupContext aContext = new SystemSetupContext(null, SystemSetup.Type.ESSENTIAL, SystemSetup.Process.UPDATE,
					extensionName);
			aContext.setJspContext(aJspContext);
			this.runDeploymentScripts(aContext, runInitScripts);
		}
		return this.isWasThereAnError();
	}

	/**
	 * This method is only called once during the initialization of the core extension. It runs all the INIT deployment
	 * scripts sequentially.
	 * 
	 * @param context
	 *           Required. Describes the current update system process.
	 */
	@SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.INIT, extension = CoreConstants.EXTENSIONNAME)
	public void runInitDeploymentScripts(final SystemSetupContext context)
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Running all INIT deployment scripts.");
		}
		this.runAllPendingScripts(true);
	}
}
