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
package org.areco.ecommerce.deploymentscripts.core.impl;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.Config;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentEnvironmentDAO;
import org.areco.ecommerce.deploymentscripts.model.DeploymentEnvironmentModel;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;


/**
 * This implementation uses Flexible Search.
 * 
 * @author arobirosa
 * 
 */
@Repository
@Scope("tenant")
public class FlexibleSearchDeploymentEnvironmentDAO implements DeploymentEnvironmentDAO
{

	public static final String CURRENT_ENVIRONMENT_CONF = "deploymentscripts.environment.current";

	private static final Logger LOG = Logger.getLogger(FlexibleSearchDeploymentEnvironmentDAO.class);

	@Autowired
	FlexibleSearchService flexibleSearchService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentEnvironmentDAO#loadEnvironments(java.util.Set)
	 */
	@Override
	public Set<DeploymentEnvironmentModel> loadEnvironments(final Set<String> environmentNames)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Loading the environments: " + environmentNames);
		}
		ServicesUtil.validateParameterNotNullStandardMessage("environmentNames", environmentNames);
		if (environmentNames.isEmpty())
		{
			throw new IllegalArgumentException("The parameter environmentNames cannot be empty.");
		}

		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT {r.").append(DeploymentEnvironmentModel.PK).append("}").append(" FROM {")
				.append(DeploymentEnvironmentModel._TYPECODE).append(" as r ").append("} ").append(" WHERE ").append(" {")
				.append(DeploymentEnvironmentModel.NAME).append("} ").append(" IN ").append('(').append('?')
				.append(DeploymentEnvironmentModel.NAME).append(')');

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
		query.addQueryParameter(DeploymentEnvironmentModel.NAME, environmentNames);
		final SearchResult<ScriptExecutionResultModel> searchResult = this.flexibleSearchService.search(query);
		if (environmentNames.size() != searchResult.getCount())
		{
			throw new IllegalStateException("Some environments don't exist. Please check that these names are valid: "
					+ environmentNames);
		}
		return new HashSet(searchResult.getResult());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentEnvironmentDAO#getCurrent()
	 */
	@Override
	public DeploymentEnvironmentModel getCurrent()
	{
		final String currentEnvironmentName = Config.getParameter(CURRENT_ENVIRONMENT_CONF);
		if (currentEnvironmentName == null)
		{
			throw new IllegalStateException(
					"Please set in the file local.properties the name of the current deployemnt environment." + " The property "
							+ CURRENT_ENVIRONMENT_CONF + " is empty.");
		}
		final Set<String> names = new HashSet<String>();
		names.add(currentEnvironmentName);
		//We return the first environment
		return this.loadEnvironments(names).iterator().next();
	}
}
