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

import com.enterprisedt.util.debug.Logger;
import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.order.daos.TaxDao;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * It checks that the sqlScriptService is using the cache correctly. This test doesn't use transaction becauses Hybris only clears the cache at the end of a
 * transaction.
 *
 * @author arobirosa
 */
public class CacheManagementDuringUpdateTest extends ServicelayerTest {

        private static final Logger LOG = Logger.getLogger(CacheManagementDuringUpdateTest.class);

        public static final String TAX_CODE = "dummyCacheManagementTax";

        @Resource
        private SqlScriptService jaloSqlScriptService;

        @Resource
        private TaxDao taxDao;

        @Before
        public void failIfInsideATransaction() {
                Assert.assertFalse("This test must be run without transactions", Transaction.current().isRunning());
        }

        @Before
        public void dummyTaxMustExists() {
                Assert.assertEquals("There must be one dummy tax", 1, taxDao.findTaxesByCode(TAX_CODE).size());
        }

        @Test
        public void testUpdatedObjectAfterSql() throws SQLException, InterruptedException {
                int numberOfAffectedRows = jaloSqlScriptService.runDeleteOrUpdateStatement("update junit_taxes set value = 11 where code = '" + TAX_CODE + "'");
                Assert.assertEquals("The must be one updated row.", 1, numberOfAffectedRows);
        }

}
