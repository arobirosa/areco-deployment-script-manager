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
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.order.daos.TaxDao;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * It checks that the sqlScriptService is using the cache correctly. This test doesn't use transaction becauses Hybris only clears the cache at the end of a
 * transaction.
 * 
 * @author arobirosa
 * 
 */
@IntegrationTest
public class CacheManagementInSqlServiceTest extends ServicelayerTest {

    private static final String TAX_CODE = "dummyCacheManagementTax";

    @Resource
    private ModelService modelService;

    @Resource
    private SqlScriptService jaloSqlScriptService;

    @Resource
    private TaxDao taxDao;

    @Before
    public void createTestTax() {
        final TaxModel dummyTax = modelService.create(TaxModel.class);
        dummyTax.setCode(TAX_CODE);
        dummyTax.setValue(Double.valueOf(33d));
        modelService.save(dummyTax);
    }

    @Test
    public void testUpdatedObjectAfterSql() throws SQLException {
        jaloSqlScriptService.runDeleteOrUpdateStatement("update taxes set value = 11 where code = '" + TAX_CODE + "'");
        Registry.getCurrentTenant().getCache().clear();
        final List<TaxModel> taxes = taxDao.findTaxesByCode(TAX_CODE);
        Assert.assertEquals("The must be one test tax.", 1, taxes.size());
        Assert.assertEquals("The value of the tax wasn't updated", 11d, taxes.iterator().next().getValue().doubleValue(), 0.01d);
    }
}
