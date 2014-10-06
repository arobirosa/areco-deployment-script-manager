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
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.order.daos.TaxDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.HybrisJUnit4ClassRunner;
import de.hybris.platform.testframework.RunListeners;
import de.hybris.platform.testframework.runlistener.PlatformRunListener;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.Config;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.enterprisedt.util.debug.Logger;

/**
 * It checks that the sqlScriptService is using the cache correctly. This test doesn't use transaction becauses Hybris only clears the cache at the end of a
 * transaction.
 * 
 * @author arobirosa
 * 
 */
@IntegrationTest
@RunWith(value = HybrisJUnit4ClassRunner.class)
@RunListeners(value = { PlatformRunListener.class })
public class CacheManagementInSqlServiceTest {

    private static final Logger LOG = Logger.getLogger(CacheManagementInSqlServiceTest.class);

    private static final String TAX_CODE = "dummyCacheManagementTax";

    @Resource
    private ModelService modelService;

    @Resource
    private SqlScriptService jaloSqlScriptService;

    @Resource
    private TaxDao taxDao;

    private TaxModel dummyTax;

    @Before
    public void createTestTax() {
        modelService = Registry.getApplicationContext().getBean("defaultModelService", ModelService.class);
        jaloSqlScriptService = Registry.getApplicationContext().getBean(SqlScriptService.class);
        taxDao = Registry.getApplicationContext().getBean(TaxDao.class);
        Assert.assertFalse("This test must be run without transactions", Transaction.current().isRunning());
        Config.setItemCacheIsolation(Boolean.FALSE);
        modelService.disableTransactions();
        dummyTax = modelService.create(TaxModel.class);
        dummyTax.setCode(TAX_CODE);
        dummyTax.setValue(Double.valueOf(33d));
        modelService.save(dummyTax);
        if (LOG.isDebugEnabled()) {
            LOG.debug("New tax instance: " + dummyTax);
        }
    }

    @After
    public void tearDown() {
        Config.setItemCacheIsolation(null);
    }

    @Test
    public void testUpdatedObjectAfterSql() throws SQLException, InterruptedException {
        dummyTax.setValue(14d); // This change is ignored
        jaloSqlScriptService.runDeleteOrUpdateStatement("update taxes set value = 11 where code = '" + TAX_CODE + "'");
        Registry.getCurrentTenant().getCache().clear();
        Thread.sleep(1000);
        final List<TaxModel> taxes = taxDao.findTaxesByCode(TAX_CODE);
        Assert.assertEquals("The must be one test tax.", 1, taxes.size());
        final TaxModel updatedTax = taxes.iterator().next();
        modelService.refresh(updatedTax);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updated tax instance: " + updatedTax);
        }
        Assert.assertEquals("The value of the tax wasn't updated", 11d, updatedTax.getValue().doubleValue(), 0.01d);
    }

    public static boolean intenseChecksActivated() {
        return true;
    }

    @Before
    public void init() throws JaloSystemException {
        Assert.assertTrue(JaloSession.hasCurrentSession());
    }

    public static Language getOrCreateLanguage(final String isoCode) throws JaloSystemException {
        Language ret = null;
        try {
            ret = C2LManager.getInstance().getLanguageByIsoCode(isoCode);
        } catch (final JaloItemNotFoundException localJaloItemNotFoundException) {
            try {
                ret = C2LManager.getInstance().createLanguage(isoCode);
            } catch (final ConsistencyCheckException e1) {
                throw new JaloSystemException(e1);
            }
        }
        return ret;
    }

    public static Currency getOrCreateCurrency(final String isoCode) throws JaloSystemException {
        Currency ret = null;
        try {
            ret = C2LManager.getInstance().getCurrencyByIsoCode(isoCode);
        } catch (final JaloItemNotFoundException localJaloItemNotFoundException) {
            try {
                ret = C2LManager.getInstance().createCurrency(isoCode);
            } catch (final ConsistencyCheckException e1) {
                throw new JaloSystemException(e1);
            }
        }
        return ret;
    }
}
