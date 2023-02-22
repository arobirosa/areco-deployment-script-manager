/**
 * Copyright 2017 Antonio Robirosa
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
package org.areco.ecommerce.deploymentscripts.scriptinglanguages;

import de.hybris.bootstrap.annotations.IntegrationTest;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * It checks if the beanshell service handles the returned codes from the script correctly.
 * <p>
 *
 * @author Antonio Robirosa <mailto:areco.manager@areko.consulting>
 */
@IntegrationTest
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") //This test asserts using the asserter class
public class ScriptingLanguageReturnedCodeValidationTest extends AbstractWithConfigurationRestorationTest {

    private static final String RESOURCES_FOLDER = "/resources/test";

    @Test
    public void testReturnOK() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "scripts-returning-ok");
        final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        getDeploymentScriptResultAsserter()
                .assertResult("20190807_54_GROOVY_RETURN_OK", this.getScriptExecutionResultDao().getSuccessResult());
    }

    @Test
    public void testReturnAnotherValue() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "scripts-returning-nok");
        final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.assertTrue("There were no errors", wereThereErrors);
        getDeploymentScriptResultAsserter().assertErrorResult("20190807_54_GROOVY_RETURN_NOK");
    }
}
