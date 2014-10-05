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

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.areco.ecommerce.deploymentscripts.ant.AntDeploymentScriptsStarter;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Test;

/**
 * Checks that the deployment scripts with sql code are working correctly.
 * 
 * @author arobirosa
 * 
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
        assertSqlScript("update", true);
        final List<TaxModel> foundTaxes = this.taxDao.findTaxesByCode("dummySqlScriptTax");
        Assert.assertEquals("There must be one tax", 1, foundTaxes.size());
        Assert.assertEquals("The imported value of the tax is wrong", 19, foundTaxes.iterator().next().getValue().doubleValue(), 0.001d);
    }

    private void assertSqlScript(final String scriptFolder, final boolean expectedSuccessfulScript) {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, scriptFolder, null);
        this.antDeploymentScriptsStarter.runPendingScripts();
        if (expectedSuccessfulScript) {
            deploymentScriptResultAsserter.assertSuccessfulResult("20141004_SQL_SCRIPT");
        } else {
            deploymentScriptResultAsserter.assertErrorResult("20141004_SQL_SCRIPT");
        }

    }

    @Test
    public void testScriptsWithSelect() {
        assertSqlScript("select", false);
    }

    @Test
    public void testScriptsWithDelete() {
        assertSqlScript("delete", true);
        final List<TaxModel> foundTaxes = this.taxDao.findTaxesByCode("dummySqlScriptTax");
        Assert.assertTrue("The tax wasn't deleted.", foundTaxes.isEmpty());
    }

    /**
     * Inserts aren't allow because we cannot generate the PK using SQL code. Hybris uses a propietary format.
     * 
     */
    @Test
    public void testScriptsWithInsert() {
        assertSqlScript("insert", false);
    }

    @Test
    public void testScriptWithWrongQuery() {
        assertSqlScript("wrong-query", false);
    }

}
