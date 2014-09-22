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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.util.JspContext;
import de.hybris.platform.util.localization.Localization;

import java.io.StringWriter;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockJspWriter;


/**
 * It checks that the result of the execution of the scripts is log in HAC.
 * 
 * @author arobirosa
 * 
 */
@IntegrationTest
public class UpdatingSystemExtensionContextLoggingTest extends AbstractWithConfigurationRestorationTest
{

	@Resource
	private DeploymentScriptStarter deploymentScriptStarter;

	@Resource
	private ScriptExecutionResultDAO flexibleSearchScriptExecutionResultDao;

	@Before
	public void setResourcesFolder()
	{
		this.getDeploymentConfigurationSetter().setTestFolders("/resources/test", null, null);
	}

	@Test
	public void testHacLogging()
	{
		final String expectedMessage = Localization.getLocalizedString("updatingsystemextensioncontext.loggingformat", new String[]
		{ "20140610_TICKET_USE_BEANSHELL_TO_RELOAD_CMS_CONF",
				flexibleSearchScriptExecutionResultDao.getSuccessResult().getDescription() });

		final SystemSetupContext hybrisContext = new SystemSetupContext(null, SystemSetup.Type.ESSENTIAL,
				SystemSetup.Process.UPDATE, ArecoDeploymentScriptsManagerConstants.EXTENSIONNAME);
		final StringWriter loggingCollector = new StringWriter();
		hybrisContext.setJspContext(new JspContext(new MockJspWriter(loggingCollector), null, null));
		deploymentScriptStarter.runUpdateDeploymentScripts(hybrisContext);

		final StringBuffer loggingBuffer = loggingCollector.getBuffer();
		Assert.assertTrue("Something must have been logged.", loggingBuffer.length() > 0);
		Assert.assertTrue("The log must contain the expected message", loggingBuffer.toString().contains(expectedMessage));
	}

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	//This test is successful if no exception is thrown.
	public void testNoErrorsWhenCalledWithoutJspContext()
	{
		final SystemSetupContext hybrisContext = new SystemSetupContext(null, SystemSetup.Type.ESSENTIAL,
				SystemSetup.Process.UPDATE, ArecoDeploymentScriptsManagerConstants.EXTENSIONNAME);
		hybrisContext.setJspContext(null);//We don't have a JSP Context
		deploymentScriptStarter.runUpdateDeploymentScripts(hybrisContext);
	}
}
