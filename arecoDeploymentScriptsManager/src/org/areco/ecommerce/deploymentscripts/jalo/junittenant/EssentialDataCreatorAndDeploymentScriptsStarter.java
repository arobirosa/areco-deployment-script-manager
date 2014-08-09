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
import de.hybris.platform.core.Registry;
import de.hybris.platform.util.JspContext;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
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

	@SuppressWarnings("deprecation")
	public void runInJunitTenant() throws Exception
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Creating the essential data in junit tenant.");
		}
		Registry.setCurrentTenantByID("junit");
		final JspWriter out = new MockJspWriter(new StringWriter());
		final HttpServletRequest request = new MockHttpServletRequest();
		final HttpServletResponse response = new MockHttpServletResponse();
		final JspContext jspc = new JspContext(out, request, response);
		Initialization.createEssentialData(jspc);
		if (LOG.isInfoEnabled())
		{
			LOG.info("The essential data was successfully created in junit tenant.");
		}
	}
}
