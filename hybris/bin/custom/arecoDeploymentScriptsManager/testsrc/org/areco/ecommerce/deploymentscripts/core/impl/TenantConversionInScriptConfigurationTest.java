/**
 * Copyright 2014 Antonio Robirosa
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.core.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.tenant.MockTenant;
import org.areco.ecommerce.deploymentscripts.core.TenantDetector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants.JUNIT_TENANT_ID;
import static org.areco.ecommerce.deploymentscripts.constants.ArecoDeploymentScriptsManagerConstants.MASTER_TENANT_ID;

/**
 * It checks that the script configuration reader is handling correctly the conversion of the tenants.
 *
 * @author arobirosa
 *
 */
@UnitTest
public class TenantConversionInScriptConfigurationTest {

    private static final String JUNIT_TENANT_SCRIPT_NAME = "20141004_JUNIT_TENANT";

    private static final String MASTER_TENANT_SCRIPT_NAME = "20141004_MASTER_TENANT";

    private static final String DEPLOYMENT_SCRIPTS_FOLDER = "test/tenant-conversion-script-configuration/";

    @InjectMocks
    private final PropertyFileDeploymentScriptConfigurationReader configurationReader = new PropertyFileDeploymentScriptConfigurationReader() {

        @Override
        protected PropertyFileDeploymentScriptConfiguration createConfiguration() {
            return new PropertyFileDeploymentScriptConfiguration();
        }
    };

    @Mock
    private TenantDetector tenantDetector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testJunitTenantInSingleTenantEnvironment() throws URISyntaxException {
        Mockito.when(tenantDetector.areWeInATestSystemWithOneSingleTenant()).thenReturn(Boolean.TRUE);
        Mockito.when(tenantDetector.getCurrentTenant()).thenReturn(new MockTenant(MASTER_TENANT_ID));
        Mockito.when(tenantDetector.getTenantByID(Mockito.eq(JUNIT_TENANT_ID))).thenReturn(null);
        assertTenantConversion(MASTER_TENANT_ID, JUNIT_TENANT_SCRIPT_NAME);
    }

    @Test
    public void testMasterTenantInSingleTenantEnvironment() throws URISyntaxException {
        Mockito.when(tenantDetector.areWeInATestSystemWithOneSingleTenant()).thenReturn(Boolean.TRUE);
        Mockito.when(tenantDetector.getCurrentTenant()).thenReturn(new MockTenant(MASTER_TENANT_ID));
        Mockito.when(tenantDetector.getTenantByID(Mockito.eq(MASTER_TENANT_ID))).thenReturn(new MockTenant(MASTER_TENANT_ID));
        assertTenantConversion("unexistentMaster", MASTER_TENANT_SCRIPT_NAME);
    }

    @Test
    public void testJunitTenantInMultiTenantEnvironment() throws URISyntaxException {
        Mockito.when(tenantDetector.areWeInATestSystemWithOneSingleTenant()).thenReturn(Boolean.FALSE);
        Mockito.when(tenantDetector.getCurrentTenant()).thenReturn(new MockTenant(JUNIT_TENANT_ID));
        Mockito.when(tenantDetector.getTenantByID(Mockito.eq(JUNIT_TENANT_ID))).thenReturn(new MockTenant(JUNIT_TENANT_ID));
        assertTenantConversion(JUNIT_TENANT_ID, JUNIT_TENANT_SCRIPT_NAME);
    }

    @Test
    public void testMasterTenantInMultiTenantEnvironment() throws URISyntaxException {
        Mockito.when(tenantDetector.areWeInATestSystemWithOneSingleTenant()).thenReturn(Boolean.FALSE);
        Mockito.when(tenantDetector.getCurrentTenant()).thenReturn(new MockTenant(MASTER_TENANT_ID));
        Mockito.when(tenantDetector.getTenantByID(Mockito.eq(MASTER_TENANT_ID))).thenReturn(new MockTenant(MASTER_TENANT_ID));
        assertTenantConversion(MASTER_TENANT_ID, MASTER_TENANT_SCRIPT_NAME);
    }

    private void assertTenantConversion(final String expectedTenantID, final String deploymentScriptNameD) throws URISyntaxException {
        final URL scriptUrl = Thread.currentThread().getContextClassLoader().getResource(DEPLOYMENT_SCRIPTS_FOLDER + deploymentScriptNameD);
        final PropertyFileDeploymentScriptConfiguration actualConfiguration = configurationReader.loadConfiguration(new File(scriptUrl.toURI()));
        Assert.assertEquals("The must be one tenant", 1, actualConfiguration.getAllowedTenants().size());
        Assert.assertEquals("The tenant has the wrong ID", expectedTenantID, actualConfiguration.getAllowedTenants().iterator().next().getTenantID());
    }
}
