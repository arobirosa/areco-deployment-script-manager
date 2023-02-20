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

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionDao;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDAO;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the dao.
 *
 * @author arobirosa
 */
@Repository
@Scope("tenant")
public class FlexibleSearchScriptExecutionDao implements ScriptExecutionDao {

    private static final Logger LOG = LoggerFactory.getLogger(FlexibleSearchScriptExecutionDao.class);

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private ScriptExecutionResultDAO scriptExecutionResultDAO;

    /*
     * { @InheritDoc }
     */
    @Override
    public List<ScriptExecutionModel> getSuccessfullyExecutedScripts(final String extensionName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting the executed scripts of the extension {}", extensionName);
        }
        final StringBuilder queryBuilder = new StringBuilder(84);

        queryBuilder.append("SELECT {es.").append(ScriptExecutionModel.PK).append("}").append(" FROM {").append(ScriptExecutionModel._TYPECODE)
                .append(" as es ").append("JOIN ").append(ScriptExecutionResultModel._TYPECODE).append(" as r ").append("ON {es.")
                .append(ScriptExecutionModel.RESULT).append("} = {r.").append(ScriptExecutionResultModel.PK).append("} AND {r.")
                .append(ScriptExecutionResultModel.CANBERUNNEDAGAIN).append("} = ?").append(ScriptExecutionResultModel.CANBERUNNEDAGAIN).append(" } ")
                .append("WHERE ").append(" {es.").append(ScriptExecutionModel.EXTENSIONNAME).append("} = ?").append(ScriptExecutionModel.EXTENSIONNAME);

        final Map<String, Object> queryParams = new ConcurrentHashMap<>();
        queryParams.put(ScriptExecutionResultModel.CANBERUNNEDAGAIN, Boolean.FALSE);
        queryParams.put(ScriptExecutionModel.EXTENSIONNAME, extensionName);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Executing the query: '{}' with the parameters {}", queryBuilder, queryParams);
        }

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString(), queryParams);

        final SearchResult<ScriptExecutionModel> result = this.flexibleSearchService.search(query);
        return result.getResult();
    }

    /**
     * Check if the last executed deployment scripts was successful.
     *
     * @return true if the last deployment script was successful.
     */
    @Override
    public boolean wasLastScriptSuccessful() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Checking if the last deployment script was successful");
        }
        final StringBuilder queryBuilder = new StringBuilder(36);

        // The creation time is unreliable because on fast machines two items can have the same creation time.
        queryBuilder.append("SELECT {").append(ScriptExecutionModel.PK).append("}").append(" FROM {").append(ScriptExecutionModel._TYPECODE)
                .append("} ORDER BY {pk} DESC");

        final Map<String, Object> queryParams = new ConcurrentHashMap<>();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Executing the query: '{}'.", queryBuilder);
        }
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString(), queryParams);
        query.setCount(1); // The first range must have one element.
        query.setNeedTotal(false);

        final SearchResult<ScriptExecutionModel> result = this.flexibleSearchService.search(query);
        if (result.getCount() == 0) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("No script was run. Returning true.");
            }
            return true;
        }
        final ScriptExecutionModel lastScript = result.getResult().iterator().next();
        final boolean hadErrors = this.scriptExecutionResultDAO.getErrorResult().equals(lastScript.getResult());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Had the last script errors? {}", hadErrors);
        }
        return !hadErrors;
    }

    @Override
    public ScriptExecutionModel getLastExecution(final String extensionName, final String scriptName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Looking for the last execution of the script from extension {} with the name {}", extensionName, scriptName);
        }
        if (StringUtils.isBlank(extensionName)) {
            throw new IllegalArgumentException("The parameter extensionName is blank");
        }
        if (StringUtils.isBlank(scriptName)) {
            throw new IllegalArgumentException("The parameter scriptName is blank");
        }

        final StringBuilder queryBuilder = new StringBuilder(36);

        // The creation time is unreliable because on fast machines two items can have the same creation time.
        queryBuilder.append("SELECT {e.").append(ScriptExecutionModel.PK).append("}").append(" FROM {").append(ScriptExecutionModel._TYPECODE)
                .append("as e} WHERE {e.").append(ScriptExecutionModel.EXTENSIONNAME).append("} ?").append(ScriptExecutionModel.EXTENSIONNAME)
                .append("as e} WHERE {e.").append(ScriptExecutionModel.SCRIPTNAME).append("} ?").append(ScriptExecutionModel.SCRIPTNAME)
                .append("} ORDER BY {pk} DESC");

        if (LOG.isTraceEnabled()) {
            LOG.trace("Executing the query: '{}'.", queryBuilder);
        }
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
        query.setCount(1); // The first range must have one element.
        query.setNeedTotal(false);
        query.addQueryParameter(ScriptExecutionModel.EXTENSIONNAME, extensionName);
        query.addQueryParameter(ScriptExecutionModel.SCRIPTNAME, scriptName);

        final SearchResult<ScriptExecutionModel> result = this.flexibleSearchService.search(query);
        if (result.getCount() == 0) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("No script was run. Returning null.");
            }
            return null;
        }
        return result.getResult().iterator().next();
    }
}
