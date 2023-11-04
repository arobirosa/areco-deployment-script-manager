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

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections4.CollectionUtils;
import org.areco.ecommerce.deploymentscripts.core.DeploymentEnvironmentDAO;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptConfiguration;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDao;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Set;

/**
 * It defines special properties of the deployment scripts like where they are allowed to run.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
public class PropertyFileDeploymentScriptConfiguration implements DeploymentScriptConfiguration {
    @Autowired
    private ScriptExecutionResultDao scriptExecutionResultDao;

    @Autowired
    private DeploymentEnvironmentDAO deploymentEnvironmentDAO;

    /* The existent of the tenants is validated during the creation of the configuration. */
    private Set<Tenant> allowedTenants;
    /*
     * It contains the names of the environment because we validate the existenz of it just before running the script.
     */
    private Set<String> allowedDeploymentEnvironmentNames;

    private boolean runMultipleTimes = false;

    private boolean longExecution = false;

    /**
     * Checks if this script is allowed to run in this server.
     *
     * @return null if it is allowed to run. Otherwise it returns the execution result.
     */
    @Override
    public ScriptExecutionResultModel reasonToIgnoreExecutionOnThisServer() {
        if (!this.isAllowedInThisTenant()) {
            return this.scriptExecutionResultDao.getIgnoredOtherTenantResult();
        }
        if (!this.isAllowedInThisDeploymentEnvironment()) {
            return this.scriptExecutionResultDao.getIgnoredOtherEnvironmentResult();
        }
        return null; // We can run this script
    }

    @Override
    public boolean runsMultipleTimes() {
        return this.isRunMultipleTimes();
    }

    @Override
    public boolean hasLongExecution() {
        return this.longExecution;
    }

    public void setHasLongExecution(final boolean hasLongExecution) {
        this.longExecution = hasLongExecution;
    }

    // Visible to subclasses to permit customizations
    protected boolean isAllowedInThisDeploymentEnvironment() {
        if (CollectionUtils.isEmpty(this.getAllowedDeploymentEnvironmentNames())) {
            return true;
        }
        return this.isCurrentEnvironmentIn(this.getAllowedDeploymentEnvironmentNames());
    }

    // Visible to subclasses to permit customizations
    protected boolean isAllowedInThisTenant() {
        if (CollectionUtils.isEmpty(this.getAllowedTenants())) {
            return true;
        }
        return this.getAllowedTenants().contains(this.getCurrentTenant());
    }

    /**
     * Returns the current tenant
     *
     * @return Never null
     */
    private Tenant getCurrentTenant() {
        return Registry.getCurrentTenant();
    }

    /**
     * Determines of the current environment is in the given list of names.
     *
     * @param deploymentEnvironmentNames Required
     * @return true if the current environment is present.
     */
    private boolean isCurrentEnvironmentIn(final Set<String> deploymentEnvironmentNames) {
        return this.deploymentEnvironmentDAO.loadEnvironments(deploymentEnvironmentNames).contains(this.deploymentEnvironmentDAO.getCurrent());
    }

    /**
     * Returns the allowed tenants.
     *
     * @return Never null.
     */
    public Set<Tenant> getAllowedTenants() {
        return this.allowedTenants == null ? Collections.emptySet() : Collections.unmodifiableSet(this.allowedTenants);
    }

    /**
     * Sets the allowed tenants.
     *
     * @param allowedTenants Required
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The given parameters aren't modified")
    public void setAllowedTenants(final Set<Tenant> allowedTenants) {
        this.allowedTenants = allowedTenants;
    }

    /**
     * Getter of the environments
     *
     * @return Never null
     */
    public Set<String> getAllowedDeploymentEnvironmentNames() {
        return this.allowedDeploymentEnvironmentNames == null ? Collections.emptySet() : Collections.unmodifiableSet(this.allowedDeploymentEnvironmentNames);
    }

    /**
     * Setter of the environments
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The given parameters aren't modified")
    public void setAllowedDeploymentEnvironmentNames(final Set<String> allowedDeploymentEnvironmentNames) {
        this.allowedDeploymentEnvironmentNames = allowedDeploymentEnvironmentNames;
    }

    // Visible to subclasses to permit customizations
    public boolean isRunMultipleTimes() {
        return this.runMultipleTimes;
    }

    public void setRunMultipleTimes(final boolean runMultipleTimes) {
        this.runMultipleTimes = runMultipleTimes;
    }
}
