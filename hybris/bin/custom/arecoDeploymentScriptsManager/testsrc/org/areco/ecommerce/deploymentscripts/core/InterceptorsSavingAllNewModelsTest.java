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
package org.areco.ecommerce.deploymentscripts.core;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.BruteForceLoginAttemptsModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorRegistry;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.impl.DefaultInterceptorRegistry;
import de.hybris.platform.util.AppendSpringConfiguration;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Makes sure that the bug which occurs when removing CMSComponents is solved. Please read the class CMSAbstractComponentRemoveInterceptor which calls
 * ctx.getModelService().saveAll();
 *
 * @author arobirosa
 */
@IntegrationTest
@AppendSpringConfiguration("/test/arecoDeploymentScriptsManager-spring-test.xml")
public class InterceptorsSavingAllNewModelsTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test/interceptors-saving-new-models";

    @Resource
    private InterceptorRegistry interceptorRegistry;

    @Resource
    private BruteForceLoginAttemptsTestingRemoveInterceptor bruteForceLoginAttemptsTestingRemoveInterceptor;

    @Test
    public void testSavingAllNewModels() {
        // Given
        Assert.assertNotNull("The bean with the test interceptor was loaded from the test application context",
                bruteForceLoginAttemptsTestingRemoveInterceptor);
        ((DefaultInterceptorRegistry) interceptorRegistry).registerInterceptor(BruteForceLoginAttemptsModel._TYPECODE,
                this.bruteForceLoginAttemptsTestingRemoveInterceptor, Collections.emptyList());
        final Collection<RemoveInterceptor> removeInterceptors = interceptorRegistry.getRemoveInterceptors(BruteForceLoginAttemptsModel._TYPECODE);
        final Optional<RemoveInterceptor> foundTestInterceptor = removeInterceptors.stream().filter(i -> BruteForceLoginAttemptsTestingRemoveInterceptor.class.equals(i.getClass())).findAny();
        Assert.assertTrue("The test interceptor was not loaded", foundTestInterceptor.isPresent());

        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "update-deployment-scripts");
        this.getDeploymentConfigurationSetter().setEnvironment("DEV");

        // When
        final boolean wereThereErrors = this.getDeploymentScriptStarter().runAllPendingScripts();

        // Then
        Assert.assertTrue("There were not errors", wereThereErrors);
        getDeploymentScriptResultAsserter().assertErrorResult("20230217_SAVE_ALL_NEW_MODELS");
    }

}
