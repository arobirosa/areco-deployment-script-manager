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

import de.hybris.platform.core.threadregistry.OperationInfo;
import de.hybris.platform.core.threadregistry.RegistrableThread;
import de.hybris.platform.core.threadregistry.RevertibleUpdate;
import de.hybris.platform.servicelayer.tenant.TenantService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.areco.ecommerce.deploymentscripts.enums.SystemPhase;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Represents each folder containing the deployment script.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
// Every time the step factory is called, it creates a new instance.
@Scope("prototype")
@Component
public class DeploymentScript {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentScript.class);

    @Resource
    private ScriptExecutionResultDao scriptExecutionResultDao;

    @Resource
    private TenantService tenantService;

    /**
     * This is the encoding used by the scripts.
     */
    public static final String DEFAULT_FILE_ENCODING = "UTF-8";

    private String name;

    private String extensionName;

    private List<DeploymentScriptStep> orderedSteps;

    private SystemPhase phase;

    private DeploymentScriptConfiguration configuration;

    /**
     * Does the actual job.
     *
     * @return Result of execution. Never null.
     */
    @NonNull
    public ScriptResult run() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running {} - Start", this.getLongName());
        }
        final ScriptExecutionResultModel configurationConstraintsCheckResult = this.getConfiguration().reasonToIgnoreExecutionOnThisServer();
        if (configurationConstraintsCheckResult != null) {
            return new ScriptResult(configurationConstraintsCheckResult);
        }

        if (this.getConfiguration().hasLongExecution()) {
            return runOrderedStepsInNonSuspendableThread();
        } else {
            return runOrderedSteps();
        }
    }

    private ScriptResult runOrderedStepsInNonSuspendableThread() {
        RevertibleUpdate revertibleInfo = null;
        try {
            revertibleInfo = registerNonSuspendableThread();
            return runOrderedSteps();
        } finally {
            this.revertOperationInfo(revertibleInfo);
        }
    }

    protected RevertibleUpdate registerNonSuspendableThread() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering this thread as non-suspendable");
        }
        RevertibleUpdate revertibleInfo = null;
        final OperationInfo operationInfo = OperationInfo.builder().withTenant(this.tenantService.getCurrentTenantId())
                .withStatusInfo("Running the areco deployment scripts non suspendable...").asNotSuspendableOperation().build();

        try {
            RegistrableThread.registerThread(operationInfo);
        } catch (final IllegalStateException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Updating the operation because has already been registered. Updating operation info...", e);
            }
            revertibleInfo = OperationInfo.updateThread(operationInfo);
        }

        return revertibleInfo;
    }

    protected void revertOperationInfo(final RevertibleUpdate revertibleInfo) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Unregistering this thread as non-suspendable");
        }
        if (revertibleInfo == null) {
            RegistrableThread.unregisterThread();
        } else {
            revertibleInfo.revert();
        }

    }

    private ScriptResult runOrderedSteps() {
        if (this.getOrderedSteps().isEmpty()) {
            return new ScriptResult(this.scriptExecutionResultDao.getErrorResult(), null,
                    new IllegalStateException("The deployment script " + this.getName() + " doesn't have any impex, sql or beanshell files."));
        }
        for (final DeploymentScriptStep aStep : this.getOrderedSteps()) {
            final ScriptStepResult stepResult = aStep.run();
            if (!stepResult.isSuccessful()) {
                LOG.error("There was an error running {}: {}", aStep.getId(), nonNull(stepResult.getException())
                        ? stepResult.getException().getLocalizedMessage() : "NOT EXCEPTION");
                return new ScriptResult(scriptExecutionResultDao.getErrorResult(), stepResult.getCronJob(), stepResult.getException());
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running {} - Ended successfully", this.getLongName());
        }
        if (this.getConfiguration().runsMultipleTimes()) {
            return new ScriptResult(this.scriptExecutionResultDao.getSuccessMultipleRunsResult());
        } else {
            return new ScriptResult(this.scriptExecutionResultDao.getSuccessResult());
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * ID used to identify the deployment script
     *
     * @return Never null
     */

    public String getLongName() {
        return this.getExtensionName() + ':' + this.getName();
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the extensionName
     */
    public String getExtensionName() {
        return this.extensionName;
    }

    /**
     * @param extensionName the extensionName to set
     */
    public void setExtensionName(final String extensionName) {
        this.extensionName = extensionName;
    }

    /**
     * @return the orderedSteps
     */
    public List<DeploymentScriptStep> getOrderedSteps() {
        if (this.orderedSteps == null) {
            this.orderedSteps = new ArrayList<>();
        }
        return Collections.unmodifiableList(this.orderedSteps);
    }

    /**
     * @param orderedSteps the orderedSteps to set
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The given parameters aren't modified")
    public void setOrderedSteps(final List<DeploymentScriptStep> orderedSteps) {
        this.orderedSteps = orderedSteps;
    }

    /*
     * Calculates the hashcode.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.extensionName == null) ? 0 : this.extensionName.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    /*
     * Compares two objects.
     */
    @Override
    @SuppressWarnings("PMD.CyclomaticComplexity")
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
        final DeploymentScript other = (DeploymentScript) obj;
        if (this.extensionName == null) {
            if (other.extensionName != null) {
                return false;
            }
        } else if (!this.extensionName.equals(other.extensionName)) {
            return false;
        }
        if (this.name == null) {
            return other.name == null;
        } else {
            return this.name.equals(other.name);
        }
    }

    /*
     * Returns a string representation of this object.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DeploymentScript [name="
                + this.name
                + ", extensionName="
                + this.extensionName
                + " , orderedSteps="
                + this.orderedSteps
                + ", phase="
                + this.phase
                + ", configuration="
                + this.configuration
                + "]";
    }

    /**
     * Getter of the phase.
     *
     * @return the phase
     */
    public SystemPhase getPhase() {
        return this.phase;
    }

    /**
     * Setter of the phase
     *
     * @param phase the phase to set
     */
    public void setPhase(final SystemPhase phase) {
        this.phase = phase;
    }

    /**
     * Returns the configuration of this script.
     *
     * @return Never null
     */
    public DeploymentScriptConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * Setter of the configuration
     *
     * @param configuration Required
     */
    public void setConfiguration(final DeploymentScriptConfiguration configuration) {
        this.configuration = configuration;
    }

}
