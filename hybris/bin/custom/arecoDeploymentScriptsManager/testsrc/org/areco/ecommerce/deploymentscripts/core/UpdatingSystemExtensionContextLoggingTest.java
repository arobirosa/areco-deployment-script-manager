/**
 * Copyright 2014 Antonio Robirosa
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.core;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.JspContext;
import de.hybris.platform.util.localization.Localization;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockJspWriter;

import javax.annotation.Resource;
import java.io.StringWriter;

import static org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStarter.CREATE_DATA_TYPE_CONF;

/**
 * It checks that the result of the execution of the scripts is log in HAC.
 *
 * @author arobirosa
 */
@IntegrationTest
public class UpdatingSystemExtensionContextLoggingTest extends AbstractWithConfigurationRestorationTest {
    /*
     * Logger of this class.
     */
    private static final Logger LOG = Logger.getLogger(UpdatingSystemExtensionContextLoggingTest.class);

    private static final String DEPLOYMENT_SCRIPT_NAME = "20150126_TICKET_USE_BEANSHELL_TO_RELOAD_CMS_CONF";

    @Resource
    private ConfigurationService configurationService;

    @Before
    public void setResourcesFolder() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Setting the resource folder to context-logging.");
        }
        this.getDeploymentConfigurationSetter().setTestFolders("/resources/test/context-logging", "update-deployment-scripts");
    }

    @Test
    public void testHacLogging() {
        final String expectedMessage = Localization.getLocalizedString("updatingsystemextensioncontext.loggingformat", new String[]
                {DEPLOYMENT_SCRIPT_NAME,
                        this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult().getDescription()}) + "<br/>";

        final SystemSetupContext hybrisContext = new SystemSetupContext(null, getConfiguredCreateDataStep(),
                SystemSetup.Process.UPDATE, ArecoDeploymentScriptsManagerConstants.EXTENSIONNAME);
        final StringWriter loggingCollector = new StringWriter();
        hybrisContext.setJspContext(new JspContext(new MockJspWriter(loggingCollector), null, null));
        this.getDeploymentScriptStarter().runUpdateDeploymentScripts(hybrisContext);

        this.getDeploymentScriptResultAsserter().assertResult(DEPLOYMENT_SCRIPT_NAME,
                this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());

        final StringBuffer loggingBuffer = loggingCollector.getBuffer();
        Assert.assertTrue("Something must have been logged.", loggingBuffer.length() > 0);
        Assert.assertEquals("The log must contain the expected message", expectedMessage, loggingBuffer.toString().trim());
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testNoErrorsWhenCalledWithoutJspContext() {
        final SystemSetupContext hybrisContext = new SystemSetupContext(null, getConfiguredCreateDataStep(),
                SystemSetup.Process.UPDATE, ArecoDeploymentScriptsManagerConstants.EXTENSIONNAME);
        hybrisContext.setJspContext(null); // We don't have a JSP Context
        this.getDeploymentScriptStarter().runUpdateDeploymentScripts(hybrisContext);
    }

    private SystemSetup.Type getConfiguredCreateDataStep() {
        return SystemSetup.Type.valueOf(configurationService.getConfiguration().getString(CREATE_DATA_TYPE_CONF));
    }

}
