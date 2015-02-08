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
import de.hybris.platform.order.daos.TaxDao;
import junit.framework.Assert;
import org.areco.ecommerce.deploymentscripts.ant.AntDeploymentScriptsStarter;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Test;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * Checks that the deployment scripts with sql code are working correctly.
 * <p/>
 * WARNING: Models cache the values of their attributes and the method modelService.refresh(model) doesn't work inside an integration test.
 * Because of this jalo items are used in this test.
 *
 * @author arobirosa
 */
@IntegrationTest
public class SqlScriptsTest extends AbstractWithConfigurationRestorationTest {

    private static final String RESOURCES_FOLDER = "/resources/test/sql-deployment-scripts";

    private static final String DUMMY_TAX_CODE = "dummySqlScriptTax";

    private static final String DUMMY_NUMBER_SERIES = "TEST_SQL_SCRIPT_INSERT";

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Resource
    private AntDeploymentScriptsStarter antDeploymentScriptsStarter;

    @Resource
    private SqlScriptService jaloSqlScriptService;

    @Resource
    private TaxDao taxDao;

    @Test
    public void testScriptsWithUpdate() {
        assertSqlScript("update", "20141004_SQL_SCRIPT_UPDATE", true);
        //A beanshell script inside the script already checks if the tax was not updated.
        //Inside an Integration test, the changes of the  script cannot be seen.
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
    public void testScriptsWithInsert() throws SQLException {
        removeDummyNumberSeries();
        try {
            assertSqlScript("insert", "20141004_SQL_SCRIPT_INSERT", true);
            NumberSeries numberSeries = NumberSeriesManager.getInstance().getNumberSeries(DUMMY_NUMBER_SERIES);
            Assert.assertEquals("The current value of the series is wrong.", 1000, numberSeries.getCurrentNumber());
        } finally {
            //The number series aren't removed when the test finishs.
            removeDummyNumberSeries();
        }
    }

    private void removeDummyNumberSeries() throws SQLException {
        this.jaloSqlScriptService.runDeleteOrUpdateStatement(
                "DELETE FROM {table_prefix}numberseries where serieskey = '" + DUMMY_NUMBER_SERIES + "'");
    }

    @Test
    public void testScriptWithWrongQuery() {
        assertSqlScript("wrong-query", "20141004_SQL_SCRIPT_WRONG_QUERY", false);
    }

}
