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
package org.areco.ecommerce.deploymentscripts.impex.impl;

import junit.framework.Assert;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStarter;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * It checks if impex scripts with medias are correctly imported.
 * 
 * @author arobirosa
 * 
 */
// PMD doesn't see the assert in the called methods.
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
public class ImportMediaTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test";

    @Resource
    private DeploymentScriptStarter deploymentScriptStarter;

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Test
    public void testLibraryHeaderImport() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "import-medias", null);
        final boolean wereThereErrors = this.deploymentScriptStarter.runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        deploymentScriptResultAsserter.assertSuccessfulResult("20150315_IMPORT_HEADER_LIBRARY");
    }
}
