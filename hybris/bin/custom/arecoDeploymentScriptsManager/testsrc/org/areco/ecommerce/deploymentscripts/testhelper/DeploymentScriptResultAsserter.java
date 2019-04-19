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

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDAO;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.junit.Assert;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Resource
    private ScriptExecutionResultDAO flexibleSearchScriptExecutionResultDao;

    /**
     * It checks is the given deployment script has the expected result.
     * 
     * @param deploymentScriptName
     *            Required
     * @param expectedResult
     *            Required
     */
    public ScriptExecutionModel assertResult(final String deploymentScriptName, final ScriptExecutionResultModel expectedResult) {
        ServicesUtil.validateParameterNotNullStandardMessage("deploymentScriptName", deploymentScriptName);
        ServicesUtil.validateParameterNotNullStandardMessage("expectedResult", expectedResult);
        final ScriptExecutionModel executionOfTheScript = getDeploymentScriptExecution(deploymentScriptName);
        Assert.assertEquals("The deployment script " + deploymentScriptName + " has the wrong result. Expected: " + expectedResult.getName() + " Actual: "
                + executionOfTheScript.getResult().getName(), expectedResult, executionOfTheScript.getResult());
        return executionOfTheScript;
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
        logExecutedScriptsInTheDatabase();
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

        try {
            return this.flexibleSearchService.searchUnique(query);
        } catch (ModelNotFoundException uie) {
            LOG.fatal("There was an error: ", uie);
            Assert.fail("The script '" + deploymentScriptName + "' wasn't executed.");
            return null; //To make the compiler happy
        }
    }

    private void logExecutedScriptsInTheDatabase() {
      final StringBuilder queryBuilder = new StringBuilder();

      queryBuilder.append("SELECT {es.").append(ScriptExecutionModel.PK).append("}").append(" FROM {").append(ScriptExecutionModel._TYPECODE)
        .append(" as es ").append(" } ");

      LOG.trace("Executing the query: '" + queryBuilder.toString());

      final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());

      SearchResult<ScriptExecutionModel> result = this.flexibleSearchService.search(query);
      LOG.trace("Number of executions: " + result.getCount());
      for (ScriptExecutionModel anExecution : result.getResult()) {
        LOG.trace("Executed '" + anExecution.getScriptName() + "'");
      }
    }

    /**
     * Checks if the given script was run and it was successful.
     * 
     * @param deploymentScriptName
     *            Required
     */
    public void assertSuccessfulResult(final String deploymentScriptName) {
        ServicesUtil.validateParameterNotNullStandardMessage("deploymentScriptName", deploymentScriptName);
        this.assertResult(deploymentScriptName, flexibleSearchScriptExecutionResultDao.getSuccessResult());
    }

    /**
     * Checks if the given script was run and there was an error.
     * 
     * @param deploymentScriptName
     *            Required
     */
    public void assertErrorResult(final String deploymentScriptName) {
        ServicesUtil.validateParameterNotNullStandardMessage("deploymentScriptName", deploymentScriptName);
        this.assertResult(deploymentScriptName, flexibleSearchScriptExecutionResultDao.getErrorResult());
    }

  /**
   * Checks if the given script was run and there was an error. The stacktrace must be the expected one.
   *
   * @param deploymentScriptName
   *            Required
   * @param pathFileExpectedStacktracePattern
   *            Required
   */
  public void assertErrorResultWithPattern(final String deploymentScriptName, final String pathFileExpectedStacktracePattern) throws IOException {
    ServicesUtil.validateParameterNotNullStandardMessage("pathFileExpectedStacktracePattern", pathFileExpectedStacktracePattern);
    assertErrorResult(deploymentScriptName);

    InputStream expectedPatternStream = DeploymentScriptResultAsserter.class.getResourceAsStream(pathFileExpectedStacktracePattern);
    Assert.assertNotNull("The file " + pathFileExpectedStacktracePattern + " with the expected stacktrace wasn't found", expectedPatternStream);

    final String loadedPattern = IOUtils.toString(expectedPatternStream).replaceAll(System.getProperty("line.separator"), "");
    final Pattern compiledStacktracePattern = Pattern.compile(loadedPattern, Pattern.DOTALL);
    ScriptExecutionModel executionOfTheScript = this.assertResult(deploymentScriptName, flexibleSearchScriptExecutionResultDao.getErrorResult());

    final Matcher stacktraceMatcher = compiledStacktracePattern.matcher(executionOfTheScript.getStacktrace());

    Assert.assertTrue(String.format("The stacktrace don't contain the expected pattern. Current stacktrace: %s",executionOfTheScript.getStacktrace()), stacktraceMatcher.matches());
  }
}
