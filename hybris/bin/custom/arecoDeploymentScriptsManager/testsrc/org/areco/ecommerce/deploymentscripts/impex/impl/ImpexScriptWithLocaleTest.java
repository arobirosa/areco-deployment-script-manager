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
package org.areco.ecommerce.deploymentscripts.impex.impl;

import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.order.daos.TaxDao;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStarter;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Test;

/**
 * It checks if impex scripts with different locales are correctly imported.
 * 
 * @author arobirosa
 * 
 */
// PMD doesn't see the assert in the private methods.
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
public class ImpexScriptWithLocaleTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test/impex-scripts-with-locale";

    @Resource
    private DeploymentScriptStarter deploymentScriptStarter;

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Resource
    private TaxDao taxDao;

    @Test
    public void testGermanLocale() {
        this.assertValueOfImportedTax("germany", "dummyGermanTax", 4.90d, Locale.GERMAN);
    }

    private void assertValueOfImportedTax(final String directoryCode, final String taxCode, final double expectedTaxValue, final Locale currentLocale) {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, directoryCode, null);
        this.getDeploymentConfigurationSetter().setImpexLocaleCode(currentLocale.toString());
        final boolean wereThereErrors = this.deploymentScriptStarter.runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        deploymentScriptResultAsserter.assertSuccessfulResult("20141003_DUMMY_TAX");
        final List<TaxModel> foundTaxes = this.taxDao.findTaxesByCode(taxCode);
        Assert.assertEquals("There must be one tax", 1, foundTaxes.size());
        Assert.assertEquals("The imported value of the tax is wrong", expectedTaxValue, foundTaxes.iterator().next().getValue().doubleValue(), 0.001d);
    }

    @Test
    public void testAmericanLocale() {
        this.assertValueOfImportedTax("usa", "dummyAmericanTax", 18.342d, Locale.ENGLISH);
    }
}
