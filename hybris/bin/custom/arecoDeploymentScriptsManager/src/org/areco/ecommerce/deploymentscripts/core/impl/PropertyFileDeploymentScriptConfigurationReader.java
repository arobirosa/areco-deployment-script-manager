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

import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.tenant.MockTenant;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptConfigurationException;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptConfigurationReader;
import org.areco.ecommerce.deploymentscripts.core.TenantDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/**
 * It reads the configuration contained in a property file with the extension conf in the folder of the script.
 *
 * @author arobirosa
 */
// The configuration of this bean is in the spring application context.
public abstract class PropertyFileDeploymentScriptConfigurationReader implements DeploymentScriptConfigurationReader {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyFileDeploymentScriptConfigurationReader.class);
    /**
     * Allowed environments
     */
    private static final String RUN_ONLY_ON_ENVIRONMENTS_PROPERTY = "runonlyonenvironments";
    /**
     * Allowed tenants
     */
    private static final String RUN_ONLY_ON_TENANTS_PROPERTY = "runonlyontenants";
    /**
     * Flag indicating if the deployment script runs once or multiple times
     */
    private static final String RUN_MULTIPLE_TIMES_PROPERTY = "runmultipletimes";
    /**
     * Flag indicating if the deployment script runs once or multiple times
     */
    private static final String HAS_LONG_EXECUTION_PROPERTY = "hasLongExecution";
    /**
     * Separator of tenant and environment names.
     */
    private static final String VALUES_SEPARATOR = ",";

    /**
     * Extension of the configuration files
     */
    private static final String PROPERTY_FILE_EXTENSION_CONF = ".conf";

    @Autowired
    private TenantDetector tenantDetector;

    /*
     * { @InheritDoc }
     */
    @Override
    public PropertyFileDeploymentScriptConfiguration loadConfiguration(final File deploymentScriptFolder) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reading configuration from the directory {}", deploymentScriptFolder);
        }
        ServicesUtil.validateParameterNotNullStandardMessage("deploymentScriptFolder", deploymentScriptFolder);
        if (!deploymentScriptFolder.exists()) {
            throw new IllegalStateException("The folder " + deploymentScriptFolder + " doesn't exist.");
        }

        final File configurationFile = this.findConfigurationFile(deploymentScriptFolder);
        if (configurationFile == null) {
            return this.createConfiguration(); // Default configuration
        }
        return this.createConfigurationFrom(configurationFile);
    }

    private PropertyFileDeploymentScriptConfiguration createConfigurationFrom(final File configurationFile) {
        final Properties properties = new Properties();

        try (final InputStream configurationFileStream = Files.newInputStream(Paths.get(configurationFile.toURI()))) {
            properties.load(configurationFileStream);
        } catch (final IOException e) {
            throw new DeploymentScriptConfigurationException(e);
        }
        final Set<Tenant> tenants = getAllowedTenants(properties);
        final Set<String> environmentNames = getAllowedDeploymentEnvironments(properties);
        final PropertyFileDeploymentScriptConfiguration newConfiguration = this.createConfiguration();
        newConfiguration.setAllowedTenants(tenants);
        newConfiguration.setAllowedDeploymentEnvironmentNames(environmentNames);
        newConfiguration.setRunMultipleTimes(getRunMultipleTimes(properties));
        newConfiguration.setHasLongExecution(hasLongExecution(properties));
        return newConfiguration;
    }

    private boolean hasLongExecution(final Properties properties) {
        final String flagValue = properties.getProperty(HAS_LONG_EXECUTION_PROPERTY);
        return BooleanUtils.toBoolean(flagValue);
    }

    private boolean getRunMultipleTimes(final Properties properties) {
        final String flagValue = properties.getProperty(RUN_MULTIPLE_TIMES_PROPERTY);
        return BooleanUtils.toBoolean(flagValue);
    }

    private Set<String> getAllowedDeploymentEnvironments(final Properties properties) {
        final String environmentsList = properties.getProperty(RUN_ONLY_ON_ENVIRONMENTS_PROPERTY);
        if (environmentsList == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(environmentsList.split(VALUES_SEPARATOR)));
    }

    private Set<Tenant> getAllowedTenants(final Properties properties) {
        final String tenantList = properties.getProperty(RUN_ONLY_ON_TENANTS_PROPERTY);
        if (tenantList == null) {
            return Collections.emptySet();
        }
        return convertTenants(tenantList);
    }

    private Set<Tenant> convertTenants(final String tenantNamesList) {
        final Set<Tenant> tenants = new HashSet<>();
        for (final String aTenantName : tenantNamesList.split(VALUES_SEPARATOR)) {
            Tenant foundTenant = this.tenantDetector.getTenantByID(aTenantName);
            if (foundTenant == null && ArecoDeploymentScriptsManagerConstants.JUNIT_TENANT_ID.equals(aTenantName)
                    && this.tenantDetector.areWeInATestSystemWithOneSingleTenant()) {
                foundTenant = this.tenantDetector.getCurrentTenant();
            }
            if (foundTenant == null) {
                throw new DeploymentScriptConfigurationException("Unable to find the tenant with the ID '" + aTenantName + "'.");
            }
            // In systems with only one tenant, this tenant is the junit. The master tenant must be ignored.
            if (ArecoDeploymentScriptsManagerConstants.MASTER_TENANT_ID.equals(aTenantName) && this.tenantDetector.areWeInATestSystemWithOneSingleTenant()) {
                tenants.add(new MockTenant("unexistentMaster"));
            } else {
                tenants.add(foundTenant);
            }
        }
        return tenants;
    }

    private File findConfigurationFile(final File deploymentScriptFolder) {
        /*
         * { @InheritDoc }
         */
        final File[] configurationFiles = deploymentScriptFolder.listFiles(
                pathname -> pathname.getName().toLowerCase(Locale.getDefault()).endsWith(PROPERTY_FILE_EXTENSION_CONF));
        if (LOG.isTraceEnabled()) {
            LOG.trace("Found configuration files: {}", Arrays.toString(configurationFiles));
        }
        if (configurationFiles == null || configurationFiles.length == 0) {
            return null;
        } else if (configurationFiles.length == 1) {
            return configurationFiles[0];
        }
        throw new DeploymentScriptConfigurationException("The folder " + deploymentScriptFolder.getAbsolutePath()
                + " contains multiply configuration files. Please leave only one.");

    }

    // Used by Spring to create new instances.
    protected abstract PropertyFileDeploymentScriptConfiguration createConfiguration();
}
