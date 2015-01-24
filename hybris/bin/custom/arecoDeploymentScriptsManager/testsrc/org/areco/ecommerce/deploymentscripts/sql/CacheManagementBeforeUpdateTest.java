package org.areco.ecommerce.deploymentscripts.sql;

import com.enterprisedt.util.debug.Logger;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.order.daos.TaxDao;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.HybrisJUnit4ClassRunner;
import de.hybris.platform.testframework.RunListeners;
import de.hybris.platform.testframework.runlistener.LogRunListener;
import de.hybris.platform.testframework.runlistener.PlatformRunListener;
import de.hybris.platform.tx.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import java.util.List;

/**
 * Checks if the first part of the test update the dummy tax row.
 * <p/>
 * Created by arobirosa on 23.01.15.
 */
@RunWith(HybrisJUnit4ClassRunner.class)
@RunListeners(
        { LogRunListener.class, PlatformRunListener.class })
public class CacheManagementBeforeUpdateTest extends AbstractResourceAutowiringTest {

        private static final Logger LOG = Logger.getLogger(CacheManagementBeforeUpdateTest.class);

        @Resource
        private ModelService modelService;

        @Resource
        private TaxDao taxDao;

        @Before
        public void removeDummyTaxRow() {
                Assert.assertFalse("This test must be run without transactions", Transaction.current().isRunning());
                final List<TaxModel> taxes = taxDao.findTaxesByCode(CacheManagementDuringUpdateTest.TAX_CODE);
                for (TaxModel aDummyTax : taxes) {
                        this.modelService.remove(aDummyTax);
                }
        }

        @Test
        public void createTestTax() {
                //We use the new constructor to prevent the deletion of the tax, when the
                //test finishes.
                TaxModel dummyTax = new TaxModel(); //modelService.create(TaxModel.class);
                dummyTax.setCode(CacheManagementDuringUpdateTest.TAX_CODE);
                dummyTax.setValue(Double.valueOf(33d));
                modelService.save(dummyTax);
                if (LOG.isDebugEnabled()) {
                        LOG.debug("New tax instance: " + dummyTax);
                }
        }
}
