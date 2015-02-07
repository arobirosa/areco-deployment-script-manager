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
package org.areco.ecommerce.deploymentscripts.sql;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.numberseries.NumberSeries;
import de.hybris.platform.jalo.numberseries.NumberSeriesManager;
import junit.framework.Assert;
import org.areco.ecommerce.deploymentscripts.ant.AntDeploymentScriptsStarter;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Test;
import javax.annotation.Resource;

/**
 * Checks that the deployment scripts with sql code are working correctly.
 *
 * @author arobirosa
 */
@IntegrationTest
public class SqlScriptsTest extends AbstractWithConfigurationRestorationTest {

        private static final String RESOURCES_FOLDER = "/resources/test/sql-deployment-scripts";

        @Resource
        private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

        @Resource
        private AntDeploymentScriptsStarter antDeploymentScriptsStarter;

        @Test
        public void testScriptsWithUpdate() {
                assertSqlScript("update", "20141004_SQL_SCRIPT_UPDATE", true);
                // We don't check if the tax was updated because Hybris only clears the cache when the transaction ends. And to refresh the item didn't work.
        }

        private void assertSqlScript(final String scriptFolder, final String scriptName, final boolean expectedSuccessfulScript) {
                String resourcesLocation = RESOURCES_FOLDER;

                this.getDeploymentConfigurationSetter().setTestFolders(resourcesLocation, scriptFolder, null);
                this.antDeploymentScriptsStarter.runPendingScripts();
                if (expectedSuccessfulScript) {
                        deploymentScriptResultAsserter.assertSuccessfulResult(scriptName);
                } else {
                        deploymentScriptResultAsserter.assertErrorResult(scriptName);
                }

        }

        @Test
        public void testScriptsWithSelect() {
                assertSqlScript("select", "20141004_SQL_SCRIPT_SELECT", false);
        }

        @Test
        public void testScriptsWithDelete() {
                assertSqlScript("delete", "20141004_SQL_SCRIPT_DELETE", true);
                //Due to the cache, we cannot test if the tax was removed from the database.
        }

        /**
         * Inserts are allowed for objects without a PK like number series.
         */
        @Test
        public void testScriptsWithInsert() {
                assertSqlScript("insert", "20141004_SQL_SCRIPT_INSERT", true);
                NumberSeries numberSeries = NumberSeriesManager.getInstance().getNumberSeries("CATEGORY");
                Assert.assertEquals("The current value of the series is wrong.", 1000, numberSeries.getCurrentNumber());
        }

        @Test
        public void testScriptWithWrongQuery() {
                assertSqlScript("wrong-query", "20141004_SQL_SCRIPT_WRONG_QUERY", false);
        }

}
