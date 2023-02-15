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
package org.areco.ecommerce.deploymentscripts.core.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptRunner;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDAO;
import org.areco.ecommerce.deploymentscripts.core.ScriptResult;
import org.areco.ecommerce.deploymentscripts.core.UpdatingSystemExtensionContext;
import org.areco.ecommerce.deploymentscripts.exceptions.DeploymentScriptExecutionException;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Default script runner.
 *
 * @author arobirosa
 */
@Service
@Scope("tenant")
public class ArecoDeploymentScriptsRunner implements DeploymentScriptRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ArecoDeploymentScriptsRunner.class);

    private static final String MAXIMUM_CAUSE_STACK_TRACE_CONF_KEY = "deploymentscripts.stacktrace.maximumlength";

    @Resource
    private ConfigurationService configurationService;

    @Autowired
    private ModelService modelService;

    @Autowired
    // We inject by name because Spring can't see the generic parameters.
    @Qualifier("deploymentScript2ExecutionConverter")
    private Converter<DeploymentScript, ScriptExecutionModel> scriptConverter;

    @Autowired
    private ScriptExecutionResultDAO scriptExecutionResultDao;

    /*
     * (non-Javadoc)
     *
     * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptRunner#run(java.util.List)
     */
    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "The DAO and the script run method never return null")
    public boolean run(final UpdatingSystemExtensionContext updatingSystemContext, final List<DeploymentScript> scriptsToBeRun) {
        for (final DeploymentScript aScript : scriptsToBeRun) {
            final ScriptExecutionModel scriptExecution = this.scriptConverter.convert(aScript);

            try {
                final ScriptResult scriptResult = aScript.run();
                scriptExecution.setResult(scriptResult.getStatus());
                scriptExecution.setFirstFailedCronjob(scriptResult.getCronJob());
                scriptExecution.setFullStacktrace(getCauseShortStackTrace(scriptResult.getException()));
            } catch (final DeploymentScriptExecutionException e) {
                LOG.error("There was an error running {}:{}", aScript.getLongName(), e.getLocalizedMessage(), e);

                scriptExecution.setResult(this.scriptExecutionResultDao.getErrorResult());
                scriptExecution.setFullStacktrace(getCauseShortStackTrace(e));
            }
            this.saveAndLogScriptExecution(updatingSystemContext, scriptExecution);
            if (this.scriptExecutionResultDao.getErrorResult().equals(scriptExecution.getResult())) {
                return true; // We stop after the first error.
            }
        }
        return false; // Everything when successfully
    }

    private void saveAndLogScriptExecution(final UpdatingSystemExtensionContext context, final ScriptExecutionModel scriptExecution) {
        this.modelService.save(scriptExecution);
        context.logScriptExecutionResult(scriptExecution);
    }

    public String getCauseShortStackTrace(final Throwable exception) {
        if (isNull(exception)) {
            return null;
        }
        String output = this.getCauseFullStackTrace(exception);
        final int maximumLength = this.configurationService.getConfiguration()
                .getInt(MAXIMUM_CAUSE_STACK_TRACE_CONF_KEY, 0);
        if (maximumLength > 0 && output.length() > maximumLength) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Returning the first {} bytes of the stack trace", maximumLength);
            }
            output = output.substring(0, maximumLength - 1);
        }
        return output;
    }

    private String getCauseFullStackTrace(final Throwable exception) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        if (exception.getCause() == null) {
            exception.printStackTrace(printWriter);
        } else {
            exception.getCause()
                    .printStackTrace(printWriter);
        }
        return stringWriter.toString();
    }
}
