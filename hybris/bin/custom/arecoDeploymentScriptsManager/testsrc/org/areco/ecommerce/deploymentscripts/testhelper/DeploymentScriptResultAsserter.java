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
package org.areco.ecommerce.deploymentscripts.testhelper;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDao;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This DAO provides services to the tests.
 *
 * @author arobirosa
 */
@Component
@Scope("tenant")
public final class DeploymentScriptResultAsserter {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentScriptResultAsserter.class);

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Resource
    private ScriptExecutionResultDao scriptExecutionResultDao;

    /**
     * It checks is the given deployment script has the expected result.
     *
     * @param deploymentScriptName Required
     * @param expectedResult       Required
     */
    public ScriptExecutionModel assertResult(final String deploymentScriptName, final ScriptExecutionResultModel expectedResult) {
        isScriptNameParameterValid(deploymentScriptName);
        ServicesUtil.validateParameterNotNullStandardMessage("expectedResult", expectedResult);
        final ScriptExecutionModel executionOfTheScript = getDeploymentScriptExecution(deploymentScriptName);
        Assert.assertEquals("The deployment script " + deploymentScriptName + " has the wrong result. Expected: " + expectedResult.getName() + " Actual: "
                + executionOfTheScript.getResult().getName(), expectedResult, executionOfTheScript.getResult());
        return executionOfTheScript;
    }

    private void isScriptNameParameterValid(final String deploymentScriptName) {
        if (StringUtils.isBlank(deploymentScriptName)) {
            throw new IllegalArgumentException("The parameter deploymentScriptName is blank");
        }
    }

    /**
     * Looks for the execution of the given script. It ignores the extension.
     *
     * @param deploymentScriptName Required
     * @return Never null
     */
    private ScriptExecutionModel getDeploymentScriptExecution(final String deploymentScriptName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting the execution of the script {}", deploymentScriptName);
        }
        final List<ScriptExecutionModel> foundExecutions = getAllDeploymentScriptExecutions(deploymentScriptName);
        if (CollectionUtils.isEmpty(foundExecutions)) {
            Assert.fail("The script '" + deploymentScriptName + "' wasn't executed.");
            return null;
        } else if (foundExecutions.size() == 1) {
            return foundExecutions.get(0);
        } else {
            Assert.fail(String.format("The script '%s' was executed %d times.", deploymentScriptName, foundExecutions.size()));
            return null;
        }
    }

    /**
     * Looks for all the executions of the given script. It ignores the extension.
     *
     * @param deploymentScriptName Required
     * @return Never null
     */
    private List<ScriptExecutionModel> getAllDeploymentScriptExecutions(final String deploymentScriptName) {
        logExecutedScriptsInTheDatabase();
        final StringBuilder queryBuilder = new StringBuilder(45);

        queryBuilder.append("SELECT {es.").append(ScriptExecutionModel.PK).append("}").append(" FROM {").append(ScriptExecutionModel._TYPECODE)
                .append(" as es ").append(" } ").append("WHERE ").append(" {es.").append(ScriptExecutionModel.SCRIPTNAME).append("} = ?")
                .append(ScriptExecutionModel.SCRIPTNAME);

        final Map<String, Object> queryParams = new ConcurrentHashMap<>();
        queryParams.put(ScriptExecutionModel.SCRIPTNAME, deploymentScriptName);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Executing the query: '{}' with the parameters {}", queryBuilder, queryParams);
        }

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString(), queryParams);

        final SearchResult<ScriptExecutionModel> result = this.flexibleSearchService.search(query);
        return result.getResult();
    }

    private void logExecutedScriptsInTheDatabase() {
        final StringBuilder queryBuilder = new StringBuilder(29);

        queryBuilder.append("SELECT {es.").append(ScriptExecutionModel.PK).append("}").append(" FROM {").append(ScriptExecutionModel._TYPECODE)
                .append(" as es ").append(" } ");

        LOG.trace("Executing the query: '{}", queryBuilder);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());

        final SearchResult<ScriptExecutionModel> result = this.flexibleSearchService.search(query);
        LOG.trace("Number of executions: {}", result.getCount());
        for (final ScriptExecutionModel anExecution : result.getResult()) {
            LOG.trace("Executed '{}'", anExecution.getScriptName());
        }
    }

    /**
     * Checks if the given script was run and it was successful.
     *
     * @param deploymentScriptName Required
     */
    public void assertSuccessfulResult(final String deploymentScriptName) {
        isScriptNameParameterValid(deploymentScriptName);
        this.assertResult(deploymentScriptName, this.scriptExecutionResultDao.getSuccessResult());
    }

    /**
     * Checks if the given script was run and there was an error.
     *
     * @param deploymentScriptName Required
     */
    public void assertErrorResult(final String deploymentScriptName) {
        isScriptNameParameterValid(deploymentScriptName);
        this.assertResult(deploymentScriptName, this.scriptExecutionResultDao.getErrorResult());
    }

    /**
     * Checks if the given script was run and there was an error. The stacktrace must be the expected one.
     *
     * @param deploymentScriptName              Required
     * @param pathFileExpectedStacktracePattern Required
     */
    public void assertErrorResultWithPattern(final String deploymentScriptName, final String pathFileExpectedStacktracePattern) throws IOException {
        ServicesUtil.validateParameterNotNullStandardMessage("pathFileExpectedStacktracePattern", pathFileExpectedStacktracePattern);
        assertErrorResult(deploymentScriptName);

        final String loadedPattern;
        try (final InputStream expectedPatternStream = DeploymentScriptResultAsserter.class.getResourceAsStream(pathFileExpectedStacktracePattern)) {
            Assert.assertNotNull("The file " + pathFileExpectedStacktracePattern + " with the expected stacktrace wasn't found", expectedPatternStream);

            loadedPattern = IOUtils.toString(expectedPatternStream, Charset.forName(DeploymentScript.DEFAULT_FILE_ENCODING)).
                    replaceAll(System.getProperty("line.separator"), "");
        }

        final Pattern compiledStacktracePattern = Pattern.compile(loadedPattern, Pattern.DOTALL);
        final ScriptExecutionModel executionOfTheScript = this.assertResult(deploymentScriptName, this.scriptExecutionResultDao.getErrorResult());

        final Matcher stacktraceMatcher = compiledStacktracePattern.matcher(executionOfTheScript.getFullStacktrace());

        Assert.assertTrue(String.format("The stacktrace don't contain the expected pattern. Current stacktrace: %s",
                executionOfTheScript.getFullStacktrace()), stacktraceMatcher.matches());
    }

    /**
     * Checks if the script was ran the expected number of times
     *
     * @param deploymentScriptName       Required
     * @param expectedNumberOfExecutions Required
     */
    public void assertNumberOfResults(final String deploymentScriptName, final int expectedNumberOfExecutions) {
        isScriptNameParameterValid(deploymentScriptName);
        Assert.assertEquals(String.format("The number of executions of %s is incorrect", deploymentScriptName), expectedNumberOfExecutions,
                getAllDeploymentScriptExecutions(deploymentScriptName).size());
    }
}
