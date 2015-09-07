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
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * It checks that the script configuration including the contraints are working correctly.
 *
 * @author arobirosa
 */
@IntegrationTest
@SuppressWarnings("PMD.TooManyMethods") //It a test with many cases
public class SaveStacktraceAfterErrorTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test/save-stacktrace";

    @Test
    public void testCurrentEnvironment() throws IOException {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "update-deployment-scripts");
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.assertTrue("There weren't any errors", wereThereErrors);
        InputStream expectedStacktraceStream = SaveStacktraceAfterErrorTest.class.getResourceAsStream("/test/save-stacktrace/expected-stackstrace.txt");
        Assert.assertNotNull("The file with the expected stacktrace wasn't found", expectedStacktraceStream);
        getDeploymentScriptResultAsserter().assertErrorResult("20150906_PENDING_SCRIPT_WRONG", IOUtils.toString(expectedStacktraceStream));
    }

}
