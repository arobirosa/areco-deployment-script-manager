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
package org.areco.ecommerce.deploymentscripts.ant;

import static org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants.*;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.order.daos.TaxDao;
import junit.framework.Assert;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.core.TenantDetector;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;


/**
 * It checks if the essentialDataCreator triggers the essential data creation and runs the scripts. Hybris cannot change the tenant when we are inside a
 * transaction, because we need transactions, this test only works in single tenant environments.
 * 
 * @author arobirosa
 * 
 */
@IntegrationTest
public class DataCreatorAndDeploymentScriptStarterTest extends AbstractWithConfigurationRestorationTest {

    private static final String CREATOR_RESOURCES_FOLDER = "/resources/test/essential-data-creator";

    private static final String ORDER_TEST_RESOURCES_FOLDER = "/resources/test/run-order";

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Resource
    private DataCreatorAndDeploymentScriptsStarter dataCreatorAndDeploymentScriptsStarter;

    @Resource
    private AntDeploymentScriptsStarter antDeploymentScriptsStarter;

    @Resource
    private EssentialDataCreationDetector essentialDataCreationDetector;

    @Resource
    private TenantDetector registryTenantDetector;

    @Resource
    private TaxDao taxDao;

    @Before
    public void skipIfJunitTenantDoesNotExist() {
        Assume.assumeFalse("Hybris cannot change the tenant when we are inside a transaction, because we need transactions to import the essential data, this test only works in environments with a separated junit tenant", registryTenantDetector.areWeInATestSystemWithOneSingleTenant());
    }


    @Test
    public void testRunDataCreationSteps() {
        this.getDeploymentConfigurationSetter().setTestFolders(CREATOR_RESOURCES_FOLDER, DEFAULT_UPDATE_SCRIPTS_FOLDER, DEFAULT_INIT_SCRIPTS_FOLDER);
        this.dataCreatorAndDeploymentScriptsStarter.runInJunitTenant();
        Assert.assertTrue("There were errors running the deployment scripts", antDeploymentScriptsStarter.wasLastScriptSuccessful());
        deploymentScriptResultAsserter.assertSuccessfulResult("20141005_RELOAD_CMS_CONF");
        deploymentScriptResultAsserter.assertSuccessfulResult("20141004_RELOAD_CMS_CONF");
        Assert.assertTrue("The creation of the essential data wasn't triggered.", essentialDataCreationDetector.isWasEssentialDataCreated());
        Assert.assertTrue("The creation of the project data wasn't triggered.", essentialDataCreationDetector.isWasProjectDataCreated());
    }

    @Test
    public void testInitAndUpdateScriptsOrder() {
        this.getDeploymentConfigurationSetter().setTestFolders(ORDER_TEST_RESOURCES_FOLDER, DEFAULT_UPDATE_SCRIPTS_FOLDER, DEFAULT_INIT_SCRIPTS_FOLDER);
        this.dataCreatorAndDeploymentScriptsStarter.runInJunitTenant();
        Assert.assertTrue("The update script was run before the init script", antDeploymentScriptsStarter.wasLastScriptSuccessful());
        deploymentScriptResultAsserter.assertSuccessfulResult("20190512_INSERT_TAX");
        deploymentScriptResultAsserter.assertSuccessfulResult("20190512_UPDATE_TAX");
        List<TaxModel> foundTaxes = taxDao.findTaxesByCode("dummyRunOrderTax");
        Assert.assertFalse("The test tax don't exist", foundTaxes.isEmpty());
        Assert.assertEquals("Many test taxes exist", 1, foundTaxes.size());
        Assert.assertEquals("The tax wasn't updated", 19d, foundTaxes.get(0).getValue());
    }


}
