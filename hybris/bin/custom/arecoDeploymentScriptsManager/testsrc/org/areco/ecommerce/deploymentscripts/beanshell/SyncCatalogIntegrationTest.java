package org.areco.ecommerce.deploymentscripts.beanshell;

import junit.framework.Assert;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.junit.Test;
import de.hybris.bootstrap.annotations.IntegrationTest;

/**
 * It checks that the groovy scripts are working correctly.
 *
 * @author arobirosa
 */
@IntegrationTest
public class SyncCatalogIntegrationTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test";

    @Test
    public void testSyncCatalogOK() {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "synchronization_of_catalogs");
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");
        final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        getDeploymentScriptResultAsserter().assertResult("14112018_Ticket49", this.getFlexibleSearchScriptExecutionResultDao().getSuccessResult());
    }



}
