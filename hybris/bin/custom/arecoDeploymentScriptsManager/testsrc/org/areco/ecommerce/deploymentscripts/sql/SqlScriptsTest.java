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
import de.hybris.platform.jalo.numberseries.NumberSeries;
import de.hybris.platform.jalo.numberseries.NumberSeriesManager;
import de.hybris.platform.jalo.order.price.Tax;
import de.hybris.platform.order.daos.TaxDao;
import de.hybris.platform.servicelayer.model.ModelService;
import junit.framework.Assert;
import org.areco.ecommerce.deploymentscripts.ant.AntDeploymentScriptsStarter;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * Checks that the deployment scripts with sql code are working correctly.
 *
 * WARNING: Models cache the values of their attributes and the method modelService.refresh(model) doesn't work inside an integration test.
 * Because of this jalo items are used in this test.
 *
 * @author arobirosa
 */
@IntegrationTest
public class SqlScriptsTest extends AbstractWithConfigurationRestorationTest {

        private static final String RESOURCES_FOLDER = "/resources/test/sql-deployment-scripts";

        private static final String DUMMY_TAX_CODE = "dummySqlScriptTax";

        @Resource
        private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

        @Resource
        private AntDeploymentScriptsStarter antDeploymentScriptsStarter;

        @Resource
        private TaxDao taxDao;

    @Resource
    private ModelService modelService;

        @Test
        public void testScriptsWithUpdate() {
                assertSqlScript("update", "20141004_SQL_SCRIPT_UPDATE", true);
                List<TaxModel> foundTaxes = taxDao.findTaxesByCode(DUMMY_TAX_CODE);
                Assert.assertEquals("There must be one dummy tax.", 1, foundTaxes.size());
                modelService.refresh(foundTaxes.get(0));
                Tax jaloTax = this.modelService.getSource(foundTaxes.get(0));
                Assert.assertEquals("The tax percent is wrong", 19, jaloTax.getValue());
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
            List<TaxModel> foundTaxes = taxDao.findTaxesByCode(DUMMY_TAX_CODE);
            Assert.assertEquals("The dummy tax wasn't removed", 0, foundTaxes.size());
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
