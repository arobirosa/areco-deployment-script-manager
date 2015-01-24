package org.areco.ecommerce.deploymentscripts.sql;

import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.order.daos.TaxDao;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * Checks if the first part of the test update the dummy tax row.
 * <p/>
 * Created by arobirosa on 23.01.15.
 */
public class CacheManagementAfterUpdateTest extends ServicelayerTest {

        @Resource
        private TaxDao taxDao;

        @Resource
        private ModelService modelService;

        @Before
        public void failIfInsideATransaction() {
                Assert.assertFalse("This test must be run without transactions", Transaction.current().isRunning());
        }

        @Test
        public void testUpdatedObjectAfterSql() throws SQLException, InterruptedException {
                final List<TaxModel> taxes = taxDao.findTaxesByCode(CacheManagementDuringUpdateTest.TAX_CODE);
                Assert.assertEquals("The must be one test tax.", 1, taxes.size());
                final TaxModel updatedTax = taxes.iterator().next();
                Assert.assertEquals("The value of the tax wasn't updated", 11d, updatedTax.getValue().doubleValue(), 0.01d);
        }

        @After
        public void removeDummyTaxRow() {
                final List<TaxModel> taxes = taxDao.findTaxesByCode(CacheManagementDuringUpdateTest.TAX_CODE);
                for (TaxModel aDummyTax : taxes) {
                        this.modelService.remove(aDummyTax);
                }
        }
}
