/**
 * Copyright 2014 Antonio Robirosa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.core;
import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * It checks that run multiple time flag of the script configuration is working correctly.
 *
 * @author arobirosa
 */
@IntegrationTest
@SuppressWarnings({"PMD.TooManyMethods", "PMD.JUnitTestsShouldIncludeAssert"}) //It a test with many cases. The assert statements are inside a private method.
public class RunMultipleTimesTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test/run-multiple-times";

    @Test
    public void testRunMultipleTimesScript() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "multiple-times");
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20200410_4_RUN_MULTIPLE_TIMES", this.getFlexibleSearchScriptExecutionResultDao().getSuccessMultipleRunsResult());
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertNumberOfResults("20200410_4_RUN_MULTIPLE_TIMES", 2);
    }

    @Test
    public void testRunMultipleTimesScriptWithError() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "multiple-times-error");
        getDeploymentScriptStarter().runAllPendingScripts();
        getDeploymentScriptResultAsserter().assertResult("20200410_4_RUN_MULTIPLE_TIMES_ERROR", this.getFlexibleSearchScriptExecutionResultDao().getErrorResult());
        getDeploymentScriptStarter().runAllPendingScripts();
        getDeploymentScriptResultAsserter().assertNumberOfResults("20200410_4_RUN_MULTIPLE_TIMES_ERROR", 2);
    }

    @Test
    public void testRunMixedScript() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "mixed-scripts");
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20200410_4_RUN_MULTIPLE_TIMES", this.getFlexibleSearchScriptExecutionResultDao().getSuccessMultipleRunsResult());
        getDeploymentScriptResultAsserter().assertResult("20200410_4_RUN_ONCE_WITHOUT_CONF", this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertNumberOfResults("20200410_4_RUN_MULTIPLE_TIMES", 2);
        getDeploymentScriptResultAsserter().assertNumberOfResults("20200410_4_RUN_ONCE_WITHOUT_CONF", 1);
    }

    @Test
    public void testRunOnceWithoutConfigurationScript() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "once-without-conf");
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20200410_4_RUN_ONCE_WITHOUT_CONF", this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20200410_4_RUN_ONCE_WITHOUT_CONF", this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        getDeploymentScriptResultAsserter().assertNumberOfResults("20200410_4_RUN_ONCE_WITHOUT_CONF", 1);
    }

    @Test
    public void testRunOnceWithConfigurationScript() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "once-with-conf");
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20200410_4_RUN_ONCE_WITH_CONF", this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter().assertResult("20200410_4_RUN_ONCE_WITH_CONF", this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        getDeploymentScriptResultAsserter().assertNumberOfResults("20200410_4_RUN_ONCE_WITH_CONF", 1);
    }

    private void runAndAssertNoErrors() {
        Assert.assertFalse("There were errors", this.getDeploymentScriptStarter().runAllPendingScripts());
    }


}
