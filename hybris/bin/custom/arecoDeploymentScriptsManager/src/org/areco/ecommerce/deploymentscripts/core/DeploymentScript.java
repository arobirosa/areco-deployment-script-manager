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

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.enums.SystemPhase;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents each folder containing the deployment script.
 * 
 * @author arobirosa
 * 
 */
// Every time the step factory is called, it creates a new instance.
@Scope("prototype")
@Component
public class DeploymentScript {
    private static final Logger LOG = Logger.getLogger(DeploymentScript.class);

    @Autowired
    private ScriptExecutionResultDAO scriptExecutionResultDAO;

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
     * @throws DeploymentScriptExecutionException
     */
    public ScriptExecutionResultModel run() throws DeploymentScriptExecutionException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running " + this.getLongName() + " - Start");
        }
        final ScriptExecutionResultModel configurationContraintsCheckResult = this.getConfiguration().reasonToIgnoreExecutionOnThisServer();
        if (configurationContraintsCheckResult != null) {
            return configurationContraintsCheckResult;
        }

        if (this.getOrderedSteps().isEmpty()) {
            throw new IllegalStateException("The deployment script " + this.getName() + " doesn't have any impex, sql or beanshell files.");
        }
        for (final DeploymentScriptStep aStep : this.getOrderedSteps()) {
            aStep.run();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running " + this.getLongName() + " - Ended successfully");
        }
        if (this.getConfiguration().runsMultipleTimes()) {
            return this.scriptExecutionResultDAO.getSuccessMultipleRunsResult();
        } else {
            return this.scriptExecutionResultDAO.getSuccessResult();
        }

    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
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
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the extensionName
     */
    public String getExtensionName() {
        return extensionName;
    }

    /**
     * @param extensionName
     *            the extensionName to set
     */
    public void setExtensionName(final String extensionName) {
        this.extensionName = extensionName;
    }

    /**
     * @return the orderedSteps
     */
    public List<DeploymentScriptStep> getOrderedSteps() {
        if (orderedSteps == null) {
            this.orderedSteps = new ArrayList<>();
        }
        return orderedSteps;
    }

    /**
     * @param orderedSteps
     *            the orderedSteps to set
     */
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
        result = prime * result + ((extensionName == null) ? 0 : extensionName.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (extensionName == null) {
            if (other.extensionName != null) {
                return false;
            }
        } else if (!extensionName.equals(other.extensionName)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /*
     * Returns a string representation of this object.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(79);
        builder.append("DeploymentScript [name=");
        builder.append(name);
        builder.append(", extensionName=");
        builder.append(extensionName);
        builder.append(", orderedSteps=");
        builder.append(orderedSteps);
        builder.append(", phase=");
        builder.append(phase);
        builder.append(", configuration=");
        builder.append(configuration);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Getter of the phase.
     * 
     * @return the phase
     */
    public SystemPhase getPhase() {
        return phase;
    }

    /**
     * Setter of the phase
     * 
     * @param phase
     *            the phase to set
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
        return configuration;
    }

    /**
     * Setter of the configuration
     * 
     * @param configuration
     *            Required
     */
    public void setConfiguration(final DeploymentScriptConfiguration configuration) {
        this.configuration = configuration;
    }

}
