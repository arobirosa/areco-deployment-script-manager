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
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants;
import org.areco.ecommerce.deploymentscripts.core.TenantDetector;
import org.fest.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * It uses the Hybris registry to find out how many tenants are in this environment.
 *
 * @author arobirosa
 */
@Scope("tenant")
@Component
public class RegistryTenantDetector implements TenantDetector {

    private static final Logger LOG = LoggerFactory.getLogger(RegistryTenantDetector.class);

    /**
     * In systems with only one tenant where were initialize with "ant clean all yunitinit yunit", the current tenant is the junit tenant. It is unfortunately
     * named "master".
     *
     * @return true if we are in a system with only one tenant.
     */
    @Override
    public boolean areWeInATestSystemWithOneSingleTenant() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("IDs of the slave tenants: {}", Collections.format(Registry.getMasterTenant().getSlaveTenantIDs()));
        }
        return !(Registry.getMasterTenant().getSlaveTenantIDs().contains(
                ArecoDeploymentScriptsManagerConstants.JUNIT_TENANT_ID));
    }

    @Override
    public Tenant getCurrentTenant() {
        return Registry.getCurrentTenant();
    }

    @Override
    public Tenant getTenantByID(final String anID) {
        ServicesUtil.validateParameterNotNullStandardMessage("anID", anID);
        return Registry.getTenantByID(anID);
    }

}
