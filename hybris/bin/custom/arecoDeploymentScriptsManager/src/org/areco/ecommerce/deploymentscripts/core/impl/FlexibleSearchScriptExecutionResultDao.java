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

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDao;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation which looks for the instances using flexible search.
 *
 * @author Antonio Robirosa <mailto:areco.manager@areko.consulting>
 */
public class FlexibleSearchScriptExecutionResultDao implements ScriptExecutionResultDao {
    private static final Logger LOG = LoggerFactory.getLogger(FlexibleSearchScriptExecutionResultDao.class);

    private static final String SUCCESS = "SUCCESS";

    private static final String SUCCESS_MULTIPLE_RUNS = "SUCCESS_MULTIPLE_RUNS";

    private static final String ERROR = "ERROR";

    private static final String IGNORED_NOT_FOR_THIS_ENVIRONMENT = "IGNORED_NOT_FOR_THIS_ENVIRONMENT";

    private static final String IGNORED_NOT_FOR_THIS_TENANT = "IGNORED_NOT_FOR_THIS_TENANT";

    private static final String IGNORED_REMOVED_ON_DISK = "IGNORED_REMOVED_ON_DISK";

    private static final String WILL_BE_EXECUTED = "WILL_BE_EXECUTED";

    private static final int NUMBER_OF_RESULT_INSTANCES = 7;

    private Map<String, ScriptExecutionResultModel> resultsByCode;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private TypeService typeService;

    /**
     * Prepares this DAO.
     */
    @Override
    public void initialize() {
        this.resultsByCode = this.getInstances();
    }

    /*
     * This method is used to know if the default configuration of the Areco Deployments Script Manager Extension was imported.
     */
    @Override
    public boolean theInitialResultsWereImported() {
        if (!(existsTheHybrisType())) {
            return false;
        }

        return this.getResultsByCode().size() == NUMBER_OF_RESULT_INSTANCES;
    }

    private boolean existsTheHybrisType() {
        try {
            return this.typeService.getComposedTypeForClass(ScriptExecutionResultModel.class) != null;
        } catch (final UnknownIdentifierException uie) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("The composed type wasn't found.", uie);
            }
            return false;
        }
    }

    /*
     * { @InheritDoc }
     */
    @Override
    @NonNull
    public ScriptExecutionResultModel getErrorResult() {
        return this.getResult(ERROR);
    }

    /*
     * { @InheritDoc }
     */
    @Override
    public ScriptExecutionResultModel getSuccessResult() {
        return this.getResult(SUCCESS);
    }

    /*
     * { @InheritDoc }
     */
    @Override
    public ScriptExecutionResultModel getSuccessMultipleRunsResult() {
        return this.getResult(SUCCESS_MULTIPLE_RUNS);
    }

    @NonNull
    private ScriptExecutionResultModel getResult(final String aCode) {
        final ScriptExecutionResultModel aResult = this.getResultsByCode().get(aCode);
        if (aResult == null) {
            throw new IllegalArgumentException("Unable to find the script execution result with the code '" + aCode + "'.");
        }
        return aResult;
    }

    private Map<String, ScriptExecutionResultModel> getInstances() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading the results.");
        }
        final Map<String, ScriptExecutionResultModel> instances = new ConcurrentHashMap<>();

        final String queryBuilder = "SELECT {r." + ScriptExecutionResultModel.PK + "}" + " FROM {" + ScriptExecutionResultModel._TYPECODE
                + " as r " + "} ";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder);
        final SearchResult<ScriptExecutionResultModel> searchResult = this.flexibleSearchService.search(query);
        if (searchResult.getCount() > 0) {
            for (final ScriptExecutionResultModel aResult : searchResult.getResult()) {
                instances.put(aResult.getName(), aResult);
            }
        }
        return instances;
    }

    private Map<String, ScriptExecutionResultModel> getResultsByCode() {
        if (this.resultsByCode == null) {
            this.initialize();
        }
        return this.resultsByCode;
    }

    /*
     * { @InheritDoc }
     */
    @Override
    public ScriptExecutionResultModel getIgnoredOtherEnvironmentResult() {
        return this.getResult(IGNORED_NOT_FOR_THIS_ENVIRONMENT);
    }

    /*
     * { @InheritDoc }
     */
    @Override
    public ScriptExecutionResultModel getIgnoredOtherTenantResult() {
        return this.getResult(IGNORED_NOT_FOR_THIS_TENANT);
    }

    /*
     * { @InheritDoc }
     */
    @Override
    public ScriptExecutionResultModel getIgnoredRemovedOnDisk() {
        return this.getResult(IGNORED_REMOVED_ON_DISK);
    }

    /*
     * { @InheritDoc }
     */
    @Override
    public ScriptExecutionResultModel getWillBeExecuted() {
        return this.getResult(WILL_BE_EXECUTED);
    }
}
