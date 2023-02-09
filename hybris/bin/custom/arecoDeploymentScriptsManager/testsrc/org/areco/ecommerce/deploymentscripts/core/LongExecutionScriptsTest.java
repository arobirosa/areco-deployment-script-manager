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

import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * It checks that run multiple time flag of the script configuration is working correctly.
 *
 * @author arobirosa
 */
@IntegrationTest
@SuppressWarnings({"PMD.TooManyMethods", "PMD.JUnitTestsShouldIncludeAssert"})
//It a test with many cases. The assert statements are inside a private method.
public class LongExecutionScriptsTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test/long-execution";
    public static final String LONG_EXECUTION_SCRIPT = "20230209_11_LONG_EXECUTION_SCRIPT";
    public static final String SHORT_EXECUTION_SCRIPT = "20230209_11_SHORT_EXECUTION_SCRIPT";

    @Test
    public void testLongExecutingScript() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "long");
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter()
                .assertResult(LONG_EXECUTION_SCRIPT, this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        getDeploymentScriptResultAsserter().assertNumberOfResults(LONG_EXECUTION_SCRIPT, 1);
    }

    @Test
    public void testLongExecutionScriptWithError() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "long-with-error");
        getDeploymentScriptStarter().runAllPendingScripts();
        getDeploymentScriptResultAsserter()
                .assertResult("20230209_11_LONG_EXECUTION_SCRIPT_ERROR", this.getFlexibleSearchScriptExecutionResultDao().getErrorResult());
    }

    @Test
    public void testRunMixedScripts() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "mixed-scripts");
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter()
                .assertResult(LONG_EXECUTION_SCRIPT, this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        getDeploymentScriptResultAsserter().assertResult(SHORT_EXECUTION_SCRIPT, this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
    }

    @Test
    public void testShortExecutingScript() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "short");
        runAndAssertNoErrors();
        getDeploymentScriptResultAsserter()
                .assertResult(SHORT_EXECUTION_SCRIPT, this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
        getDeploymentScriptResultAsserter().assertNumberOfResults(SHORT_EXECUTION_SCRIPT, 1);
    }
    
    private void runAndAssertNoErrors() {
        Assert.assertFalse("There were errors", this.getDeploymentScriptStarter().runAllPendingScripts());
    }


}
