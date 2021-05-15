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
package org.areco.ecommerce.deploymentscripts.systemsetup;

import de.hybris.platform.constants.CoreConstants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants;
import org.areco.ecommerce.deploymentscripts.core.UpdatingSystemExtensionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Contains auxiliary methods related with the Hybris extensions.
 * <p/>
 * TODO Find a clear responsibility for this class.
 *
 * @author arobirosa
 */
@Scope("tenant")
@Component
public class ExtensionHelper {

    private static final Logger LOG = Logger.getLogger(ExtensionHelper.class);

    /**
     * Returns a boolean indicating if the current extension is the first one which is run during an update running system.
     *
     * @param context Required.
     * @return boolean True if it is the first one.
     */

    public boolean isFirstExtension(final UpdatingSystemExtensionContext context) {
        ServicesUtil.validateParameterNotNullStandardMessage("context", context);
        // There must be a better way to find out which one is the first extension
        return CoreConstants.EXTENSIONNAME.equalsIgnoreCase(context.getExtensionName());
    }

    /**
     * List of extension names in compilation order.
     *
     * @return List of names. Never null
     */
    public List<String> getExtensionNames() {
        return Registry.getMasterTenant().getTenantSpecificExtensionNames();
    }

    /**
     * Returns if the deployment manager extension is active on the current tenant.
     *
     * @return boolean
     */
    public boolean isDeploymentManagerExtensionTurnedOn() {

        final boolean isActive = Registry.getCurrentTenant().getTenantSpecificExtensionNames()
                .contains(ArecoDeploymentScriptsManagerConstants.EXTENSIONNAME);
        if (LOG.isInfoEnabled()) {
            LOG.info("Is the Areco Deployment Manager extension turned on in the tenant " + Registry.getCurrentTenant() + ':' + isActive);
        }
        return isActive;
    }
}
