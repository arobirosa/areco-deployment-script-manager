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
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.lang.StringUtils;
import org.areco.ecommerce.deploymentscripts.core.DeploymentEnvironmentDAO;
import org.areco.ecommerce.deploymentscripts.model.DeploymentEnvironmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * This implementation uses Flexible Search.
 *
 * @author arobirosa
 */
@Repository
@Scope("tenant")
public class FlexibleSearchDeploymentEnvironmentDAO implements DeploymentEnvironmentDAO {

    public static final String CURRENT_ENVIRONMENT_CONF = "deploymentscripts.environment.current";
    public static final String UNCONFIGURATED_CURRENT_ENVIRONMENT_ERROR_MESSAGE =
            "Please set in the file local.properties the name of the current deployemnt environment." + " The property "
                    + CURRENT_ENVIRONMENT_CONF + " is empty.";

    private static final Logger LOG = LoggerFactory.getLogger(FlexibleSearchDeploymentEnvironmentDAO.class);

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<DeploymentEnvironmentModel> loadEnvironments(final Set<String> environmentNames) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading the environments: {}", environmentNames);
        }
        ServicesUtil.validateParameterNotNullStandardMessage("environmentNames", environmentNames);
        if (environmentNames.isEmpty()) {
            throw new IllegalArgumentException("The parameter environmentNames cannot be empty.");
        }
        final Set<String> normalizedEnvironmentNames = new HashSet<>();
        for (final String givenEnvironmentName : environmentNames) {
            normalizedEnvironmentNames.add(givenEnvironmentName.trim().toUpperCase(Locale.getDefault()));
        }

        final String queryBuilder = "SELECT {r." + DeploymentEnvironmentModel.PK + "}"
                + " FROM {" + DeploymentEnvironmentModel._TYPECODE
                + " as r " + "} " + " WHERE "
                + " UPPER({" + DeploymentEnvironmentModel.NAME + "}) " + " IN "
                + '(' + '?' + DeploymentEnvironmentModel.NAME + ')';

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder);
        query.addQueryParameter(DeploymentEnvironmentModel.NAME, normalizedEnvironmentNames);
        final SearchResult<DeploymentEnvironmentModel> searchResult = this.flexibleSearchService.search(query);
        if (environmentNames.size() != searchResult.getCount()) {
            throw new IllegalStateException("Some environments don't exist. Please check that these names are valid: " + environmentNames);
        }
        return new HashSet<>(searchResult.getResult());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DeploymentEnvironmentModel getCurrent() {
        final String currentEnvironmentName = this.configurationService.getConfiguration().getString(CURRENT_ENVIRONMENT_CONF);
        if (StringUtils.isBlank(currentEnvironmentName)) {
            throw new IllegalStateException(UNCONFIGURATED_CURRENT_ENVIRONMENT_ERROR_MESSAGE);
        }
        final Set<String> names = new HashSet<>();
        names.add(currentEnvironmentName);
        // We return the first environment
        return this.loadEnvironments(names).iterator().next();
    }
}
