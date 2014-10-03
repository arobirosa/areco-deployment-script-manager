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
package org.areco.ecommerce.deploymentscripts.testhelper;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.junit.Assert;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This DAO provides services to the tests.
 * 
 * @author arobirosa
 * 
 */
@Component
@Scope("tenant")
public final class DeploymentScriptResultAsserter {

    private static final Logger LOG = Logger.getLogger(DeploymentScriptResultAsserter.class);

    @Resource
    private FlexibleSearchService flexibleSearchService;

    public void assertResult(final String deploymentScriptName, final ScriptExecutionResultModel expectedResult) {
        ServicesUtil.validateParameterNotNullStandardMessage("deploymentScriptName", deploymentScriptName);
        ServicesUtil.validateParameterNotNullStandardMessage("expectedResult", expectedResult);
        final ScriptExecutionModel executionOfTheScript = getDeploymentScriptExecution(deploymentScriptName);
        Assert.assertEquals("The deployment script " + deploymentScriptName + " has the wrong result. Expected: " + expectedResult.getName() + " Actual: "
                + executionOfTheScript.getResult().getName(), expectedResult, executionOfTheScript.getResult());
    }

    /**
     * Looks for the execution of the given script. It ignores the extension.
     * 
     * @param deploymentScriptName
     *            Required
     * @return Never null
     */
    private ScriptExecutionModel getDeploymentScriptExecution(final String deploymentScriptName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting the execution of the script " + deploymentScriptName);
        }
        final StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT {es.").append(ScriptExecutionModel.PK).append("}").append(" FROM {").append(ScriptExecutionModel._TYPECODE)
                .append(" as es ").append(" } ").append("WHERE ").append(" {es.").append(ScriptExecutionModel.SCRIPTNAME).append("} = ?")
                .append(ScriptExecutionModel.SCRIPTNAME);

        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(ScriptExecutionModel.SCRIPTNAME, deploymentScriptName);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Executing the query: '" + queryBuilder.toString() + "' with the parameters " + queryParams);
        }

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString(), queryParams);

        return this.flexibleSearchService.searchUnique(query);
    }

}
