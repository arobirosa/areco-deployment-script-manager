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

import de.hybris.platform.core.Initialization;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.JspContext;
import de.hybris.platform.util.Utilities;

import java.io.StringWriter;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockJspWriter;


/**
 * It creates the essential data for the tenant junit. The command "ant yunitinit" doesn't run the essential data
 * creation step. Due to this, no deployment scripts are run.
 * 
 * TODO This class uses Jalo and a work around to trigger the essential data creation. We must find a cleaner way to do
 * this.
 * 
 * @author arobirosa
 * 
 */
public class EssentialDataCreatorAndDeploymentScriptsStarter
{
	private static final Logger LOG = Logger.getLogger(EssentialDataCreatorAndDeploymentScriptsStarter.class);

	private static final EssentialDataCreatorAndDeploymentScriptsStarter INSTANCE = new EssentialDataCreatorAndDeploymentScriptsStarter();

	public static EssentialDataCreatorAndDeploymentScriptsStarter getInstance()
	{
		return INSTANCE;
	}

	public void runInJunitTenant() throws Exception
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

		createEssentialDataForAllExtensions();
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

		//We need dummy objects.
		final JspWriter out = new MockJspWriter(new StringWriter());
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("initmethod", "init"); //We simulate a essential data creation during the initialization.
		final HttpServletResponse response = new MockHttpServletResponse();
		final JspContext jspc = new JspContext(out, request, response);
		Initialization.createEssentialData(jspc);
	}
}
