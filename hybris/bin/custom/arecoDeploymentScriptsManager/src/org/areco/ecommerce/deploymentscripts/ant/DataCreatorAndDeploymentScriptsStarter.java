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
package org.areco.ecommerce.deploymentscripts.ant;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupCollector;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.JspContext;
import org.areco.ecommerce.deploymentscripts.systemsetup.ExtensionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import java.io.StringWriter;
import java.util.Collections;

/**
 * It creates the essential and project data for the tenant junit. The command "ant yunitinit" doesn't run the essential data creation step.
 * Due to this, no deployment scripts are run.
 * <p>
 * TODO This class uses Jalo and a workaround to trigger the essential data creation. We must find a cleaner way to do this.
 * TODO Because this class uses jalo, it is difficult to test
 *
 * @author arobirosa
 */
@Service("dataCreatorAndDeploymentScriptsStarter")
@Scope("tenant")
@SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
// We keep the local variable jspc because we only want to create a context once. The warning suppression only works at this point.
public class DataCreatorAndDeploymentScriptsStarter {

    private static final String JUNIT_TENANT_CREATEESSENTIALDATA_CONF = "deploymentscripts.init.junittenant.createessentialdata";
    private static final String JUNIT_TENANT_CREATEPROJECTDATA_CONF = "deploymentscripts.init.junittenant.createprojectdata";

    private static final Logger LOG = LoggerFactory.getLogger(DataCreatorAndDeploymentScriptsStarter.class);

    @Autowired
    private ExtensionHelper extensionHelper;

    @Autowired
    private SystemSetupCollector systemSetupCollector;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Creates the essential and project data. This triggers the runs of the deployment scripts in the junit tenant.
     */
    public void runInJunitTenant() {
        if (this.extensionHelper.isDeploymentManagerExtensionTurnedOff()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("The essential and project data won't be created and the deployment scripts won't be run.");
            }
            return;
        }
        boolean success = true;
        if (Boolean.parseBoolean(this.configurationService.getConfiguration().getString(JUNIT_TENANT_CREATEESSENTIALDATA_CONF))) {
            success = createDataAndLogErrors(SystemSetup.Type.ESSENTIAL);
        }
        if (success && Boolean.parseBoolean(this.configurationService.getConfiguration().getString(JUNIT_TENANT_CREATEPROJECTDATA_CONF))) {
            createDataAndLogErrors(SystemSetup.Type.PROJECT);
        }
    }


    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    // We catch all exceptions because this method is called by ant.
    private boolean createDataAndLogErrors(final SystemSetup.Type aCreationDataType) {
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("Creating the %s data in junit tenant.", aCreationDataType));
        }

        try {
            createDataForAllExtensions(aCreationDataType);
        } catch (final Exception e) {
            // We log here to see the stacktrace. The beanshell code of the ant task yunit logs the exception partially.
            LOG.error(String.format("There was an error creating the %s data: %s", aCreationDataType, e.getLocalizedMessage()), e);
            return false; // There was an error
        }
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("The %s data was successfully created in junit tenant.", aCreationDataType));
        }
        return true; // All went ok.
    }

    @SuppressWarnings({"deprecation", "PMD.SignatureDeclareThrowsException", "unchecked"})
    // The caller of this method must handle any exception, because this class is called by ant, which doesn't
    // show the complete stack trace.
    private void createDataForAllExtensions(final SystemSetup.Type aCreationDataType) throws Exception {
        // To import the encodings.
        new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);

        final JspContext jspc = createDummyJspContext();

        for (final String extensionName : this.extensionHelper.getExtensionNames()) {
            final Extension anExtension = ExtensionManager.getInstance().getExtension(extensionName);
            // Old Jalo method
            anExtension.createEssentialData(Collections.EMPTY_MAP, jspc);
            // New mechanism with annotations.
            final SystemSetupContext ctx = new SystemSetupContext(Collections.EMPTY_MAP, aCreationDataType, SystemSetup.Process.INIT, extensionName);
            ctx.setJspContext(jspc);
            this.systemSetupCollector.executeMethods(ctx);
        }
    }

    private JspContext createDummyJspContext() {
        final JspWriter out = new MockJspWriter(new StringWriter());
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final HttpServletResponse response = new MockHttpServletResponse();
        return new JspContext(out, request, response);
    }
}
