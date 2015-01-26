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
import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.order.daos.TaxDao;
import junit.framework.Assert;
import org.areco.ecommerce.deploymentscripts.ant.AntDeploymentScriptsStarter;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.core.TenantDetector;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

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

        @Resource
        private TaxDao taxDao;

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
                final List<TaxModel> foundTaxes = this.taxDao.findTaxesByCode("dummySqlScriptTax");
                Assert.assertTrue("The tax wasn't deleted.", foundTaxes.isEmpty());
        }

        /**
         * Inserts aren't allow because we cannot generate the PK using SQL code. Hybris uses a proprietary format.
         */
        @Test
        public void testScriptsWithInsert() {
                assertSqlScript("insert", "20141004_SQL_SCRIPT_INSERT", false);
        }

        @Test
        public void testScriptWithWrongQuery() {
                assertSqlScript("wrong-query", "20141004_SQL_SCRIPT_WRONG_QUERY", false);
        }

}
