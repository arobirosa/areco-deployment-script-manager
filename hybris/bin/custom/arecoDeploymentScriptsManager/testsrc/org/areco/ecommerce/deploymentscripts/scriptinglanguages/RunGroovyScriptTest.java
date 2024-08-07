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
package org.areco.ecommerce.deploymentscripts.scriptinglanguages;

import de.hybris.bootstrap.annotations.IntegrationTest;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * It checks that the groovy scripts are working correctly.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
@IntegrationTest
public class RunGroovyScriptTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test";

    @Test
    public void testReturnOK() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "groovy-scripts");
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        getDeploymentScriptResultAsserter().assertResult("2015902_TICKET_GROOVY", this.getScriptExecutionResultDao().getSuccessResult());
    }

    @Test
    public void testScriptWithException() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "groovy-scrips-with-exceptions");
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.assertTrue("There were no errors", wereThereErrors);
        getDeploymentScriptResultAsserter().assertErrorResult("20160325_GROOVY_EXCEPTION");
    }


}
