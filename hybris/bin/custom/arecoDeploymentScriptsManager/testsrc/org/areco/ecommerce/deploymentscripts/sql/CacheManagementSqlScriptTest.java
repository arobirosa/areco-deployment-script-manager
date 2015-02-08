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

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.HybrisJUnit4ClassRunner;
import de.hybris.platform.testframework.RunListeners;
import de.hybris.platform.testframework.runlistener.LogRunListener;
import de.hybris.platform.testframework.runlistener.PlatformRunListener;
import de.hybris.platform.tx.Transaction;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentEnvironmentDAO;
import org.areco.ecommerce.deploymentscripts.model.DeploymentEnvironmentModel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * It checks that the Hybrs cache is emptied when an sql script is run.
 * <p/>
 * WARNING: With an active cache this test doesn't work.
 * <p/>
 * Created by arobirosa on 24.01.15.
 */
@RunWith(HybrisJUnit4ClassRunner.class)
@RunListeners(
        { LogRunListener.class, PlatformRunListener.class })
public class CacheManagementSqlScriptTest {

        private static final Logger LOG = Logger.getLogger(CacheManagementSqlScriptTest.class);

        private static final String DUMMY_ENVIRONMENT_NAME = "DUMMY_ENVIRONMENT";
        private static final String DUMMY_ENVIRONMENT_DESCRIPTION = "Please remove this environment. It was used for an integration test.";
        private static final String UPDATED_SUBFIX = " UPDATED";

        //Resource annotation don't work because this test isn't a subclass of serviclelayertest.
        private ModelService modelService;

        private DeploymentEnvironmentDAO flexibleSearchDeploymentEnvironmentDAO;

        private SqlScriptService jaloSqlScriptService;

        private Set<String> dummyEnvironmentsNames;

        @Before
        public void checkNoTransactionsAndRemoveOldData() {
                modelService = Registry.getApplicationContext().getBean("modelService", ModelService.class);
                flexibleSearchDeploymentEnvironmentDAO = Registry.getApplicationContext().getBean(DeploymentEnvironmentDAO.class);
                jaloSqlScriptService = Registry.getApplicationContext().getBean(SqlScriptService.class);

                Assert.assertFalse("This test must be run without transactions", Transaction.current().isRunning());
                dummyEnvironmentsNames = new HashSet<String>();
                dummyEnvironmentsNames.add(DUMMY_ENVIRONMENT_NAME);

                this.removeDummyDeploymentEnvironment();
        }

        @Test
        public void emptyCacheAfterSqlScript() throws SQLException {
                createDummyEnvironment();
                updateDescriptionWithSQLScript();
                assertUpdatedEnvironment();
        }

        private void updateDescriptionWithSQLScript() throws SQLException {
                // There is only one row in junit_arenvironmentlp with the name of the environment.
                // Due to this there isn't any need to filter the language.
                int numberOfAffectedRows = jaloSqlScriptService.runDeleteOrUpdateStatement(
                        "update {table_prefix}arenvironmentlp"
                                + " set p_description = '" + DUMMY_ENVIRONMENT_DESCRIPTION + UPDATED_SUBFIX + "'"
                                + " where itempk = (select e.pk "
                                + "    from {table_prefix}arenvironment e "
                                + "    where e.p_name = '" + DUMMY_ENVIRONMENT_NAME + "')");
                Assert.assertEquals("The must be one updated row.", 1, numberOfAffectedRows);
        }

        private void assertUpdatedEnvironment() {
                Set<DeploymentEnvironmentModel> dummyEnvironments = this.flexibleSearchDeploymentEnvironmentDAO.loadEnvironments(this.dummyEnvironmentsNames);
                Assert.assertEquals("There must be only one dummy environment", 1, dummyEnvironments.size());
                Assert.assertEquals("The description must have been updated", DUMMY_ENVIRONMENT_DESCRIPTION + UPDATED_SUBFIX,
                        dummyEnvironments.iterator().next().getDescription());
        }

        private DeploymentEnvironmentModel createDummyEnvironment() {
                DeploymentEnvironmentModel dummyEnvironment = modelService.create(DeploymentEnvironmentModel.class);
                dummyEnvironment.setName(DUMMY_ENVIRONMENT_NAME);
                dummyEnvironment.setDescription(DUMMY_ENVIRONMENT_DESCRIPTION);
                modelService.save(dummyEnvironment);
                if (LOG.isDebugEnabled()) {
                        LOG.debug("The deployment environment " + dummyEnvironment + " was saved.");
                }
                return dummyEnvironment;
        }

        @After
        public void removeTestData() {
                this.removeDummyDeploymentEnvironment();
        }

        private void removeDummyDeploymentEnvironment() {
                try {
                        for (DeploymentEnvironmentModel anEnvironment : this.flexibleSearchDeploymentEnvironmentDAO
                                .loadEnvironments(this.dummyEnvironmentsNames)) {
                                if (LOG.isDebugEnabled()) {
                                        LOG.debug("Removing the dummy environment with name "
                                                + anEnvironment.getName() + " and description <"
                                                + anEnvironment.getDescription() + ">");
                                }
                                this.modelService.remove(anEnvironment);
                        }
                } catch (IllegalStateException e) {
                        if (LOG.isDebugEnabled()) {
                                LOG.debug("The dummy environment wasn't found.", e);
                        }
                }
        }

}
