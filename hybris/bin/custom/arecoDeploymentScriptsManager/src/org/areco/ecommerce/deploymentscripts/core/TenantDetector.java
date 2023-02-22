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

import de.hybris.platform.core.Tenant;

/**
 * It find out how many tenants are in the environment and checks if the integration tests are run in a single tenant system.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
public interface TenantDetector {
    /**
     * In systems with only one tenant where were initialize with "ant clean all yunitinit yunit", the current tenant is the junit tenant. It is unfortunately
     * named "master".
     *
     * @return true if we are in a system with only one tenant.
     */
    boolean areWeInATestSystemWithOneSingleTenant();

    /**
     * Returns the current tenant. This allowed us to mock the tenant in the integration tests.
     *
     * @return Never null.
     */
    Tenant getCurrentTenant();

    /**
     * Returns the tenant with the given ID.
     *
     * @param anID Required
     * @return null if the tenant wasn't found
     */
    Tenant getTenantByID(String anID);
}
