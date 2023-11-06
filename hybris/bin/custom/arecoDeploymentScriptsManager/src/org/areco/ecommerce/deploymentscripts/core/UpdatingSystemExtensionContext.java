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

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.JspContext;
import de.hybris.platform.util.localization.Localization;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It represents the extension being update or initialize during the update system process. It is an immutable object.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
public class UpdatingSystemExtensionContext {
    private static final Logger LOG = LoggerFactory.getLogger(UpdatingSystemExtensionContext.class);

    private final String extensionName;

    private final SystemSetup.Process process;

    /*
     * It is used to log in HAC.
     */
    private final JspContext jspContext;

    /**
     * Default constructor of this class.
     *
     * @param pExtensionName Required
     * @param pProcess       Required
     * @param pJspContext    Optional
     */
    public UpdatingSystemExtensionContext(final String pExtensionName, final SystemSetup.Process pProcess, final JspContext pJspContext) {
        ServicesUtil.validateParameterNotNullStandardMessage("pExtensionName", pExtensionName);
        ServicesUtil.validateParameterNotNullStandardMessage("pProcess", pProcess);
        this.extensionName = pExtensionName;
        this.process = pProcess;
        this.jspContext = pJspContext;
    }

    /**
     * Simplified constructor.
     *
     * @param pExtensionName Required
     * @param pProcess       Required
     */
    public UpdatingSystemExtensionContext(final String pExtensionName, final SystemSetup.Process pProcess) {
        this(pExtensionName, pProcess, null);
    }

    public String getExtensionName() {
        return this.extensionName;
    }

    public SystemSetup.Process getProcess() {
        return this.process;
    }

    protected JspContext getJspContext() {
        return this.jspContext;
    }

    @Override
    public String toString() {
        return "UpdatingSystemExtensionContext [extensionName="
                + this.extensionName
                + ", process="
                + this.process
                + ", jspContext="
                + this.jspContext
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.extensionName == null) ? 0 : this.extensionName.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UpdatingSystemExtensionContext other = (UpdatingSystemExtensionContext) obj;
        if (this.extensionName == null) {
            return other.extensionName == null;
        } else {
            return this.extensionName.equals(other.extensionName);
        }
    }

    /**
     * It logs the result of the execution of the deployment script.
     *
     * @param scriptExecution Required
     */
    public void logScriptExecutionResult(final ScriptExecutionModel scriptExecution) {
        ServicesUtil.validateParameterNotNullStandardMessage("scriptExecution", scriptExecution);
        if (scriptExecution.getResult() == null) {
            throw new IllegalStateException("The result of the execution must have been set at this point.");
        }
        final String messageToLog = Localization.getLocalizedString("updatingsystemextensioncontext.loggingformat",
                new String[]{scriptExecution.getScriptName(), scriptExecution.getResult().getDescription()});

        if (LOG.isInfoEnabled()) {
            LOG.info(messageToLog);
        }
        if (this.getJspContext() != null) {

            this.getJspContext().println(messageToLog);
        }
    }

    /**
     * It logs the given string with the prefix error.
     *
     * @param errorMessage Required
     */
    public void logError(final String errorMessage) {
        ServicesUtil.validateParameterNotNullStandardMessage("errorMessage", errorMessage);
        LOG.error(errorMessage);
        if (this.getJspContext() != null) {
            this.getJspContext().println("ERROR " + errorMessage);
        }
    }

}
