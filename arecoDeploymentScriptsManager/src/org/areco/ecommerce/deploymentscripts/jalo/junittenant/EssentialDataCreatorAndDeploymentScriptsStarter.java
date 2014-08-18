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
package org.areco.ecommerce.deploymentscripts.jalo.junittenant;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupCollector;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.JspContext;
import de.hybris.platform.util.Utilities;

import java.io.StringWriter;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.systemsetup.ExtensionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.stereotype.Service;


/**
 * It creates the essential data for the tenant junit. The command "ant yunitinit" doesn't run the essential data
 * creation step. Due to this, no deployment scripts are run.
 * 
 * TODO This class uses Jalo and a workaround to trigger the essential data creation. We must find a cleaner way to do
 * this.
 * 
 * @author arobirosa
 * 
 */
@Service("essentialDataCreatorAndDeploymentScriptsStarter")
@Scope("tenant")
public class EssentialDataCreatorAndDeploymentScriptsStarter
{
	private static final Logger LOG = Logger.getLogger(EssentialDataCreatorAndDeploymentScriptsStarter.class);

	private static EssentialDataCreatorAndDeploymentScriptsStarter INSTANCE = null;

	@Autowired
	private ExtensionHelper extensionHelper;

	@Autowired
	private SystemSetupCollector systemSetupCollector;

	public static synchronized EssentialDataCreatorAndDeploymentScriptsStarter getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = Registry.getApplicationContext().getBean(EssentialDataCreatorAndDeploymentScriptsStarter.class);
		}
		return INSTANCE;
	}

	public void runInJunitTenant()
	{
		if (!Boolean.parseBoolean(Config.getParameter("deploymentscripts.init.junittenant.createessentialdata")))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("The essential data won't be created and the deployment scripts won't be run.");
			}
			return;
		}
		if (LOG.isInfoEnabled())
		{
			LOG.info("Creating the essential data in junit tenant.");
		}
		Utilities.setJUnitTenant();

		try
		{
			createEssentialDataForAllExtensions();
		}
		catch (final Exception e)
		{
			//We log here to see the stacktrace. The beanshell code of the ant task yunit logs the exception partially.
			LOG.error("There was an error creating the essential data: " + e.getLocalizedMessage(), e);
			return;
		}
		if (LOG.isInfoEnabled())
		{
			LOG.info("The essential data was successfully created in junit tenant.");
		}
	}

	@SuppressWarnings("deprecation")
	private void createEssentialDataForAllExtensions() throws Exception
	{
		//To import the encodings.
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);

		final JspContext jspc = createDummyJspContext();

		for (final String extensionName : this.extensionHelper.getExtensionNames())
		{
			final Extension anExtension = ExtensionManager.getInstance().getExtension(extensionName);
			//Old Jalo method
			anExtension.createEssentialData(Collections.EMPTY_MAP, jspc);
			//New mechanism with annotations.
			final SystemSetupContext ctx = new SystemSetupContext(Collections.EMPTY_MAP, SystemSetup.Type.ESSENTIAL,
					SystemSetup.Process.INIT, extensionName);
			ctx.setJspContext(jspc);
			this.systemSetupCollector.executeMethods(ctx);
		}
	}

	private JspContext createDummyJspContext()
	{
		final JspWriter out = new MockJspWriter(new StringWriter());
		final MockHttpServletRequest request = new MockHttpServletRequest();
		final HttpServletResponse response = new MockHttpServletResponse();
		final JspContext jspc = new JspContext(out, request, response);
		return jspc;
	}
}
