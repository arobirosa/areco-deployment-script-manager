package org.areco.ecommerce.deploymentscripts.core;

import de.hybris.platform.core.model.user.BruteForceLoginAttemptsModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

/**
 * Copyright 2023 Antonio Robirosa
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">...</a>
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BruteForceLoginAttemptsTestingRemoveInterceptor implements RemoveInterceptor<BruteForceLoginAttemptsModel> {
    @Override
    public void onRemove(final BruteForceLoginAttemptsModel model, final InterceptorContext interceptorContext) throws InterceptorException {
        // Trigger the bug caused by de.hybris.platform.cms2.servicelayer.interceptor.impl.CMSAbstractComponentRemoveInterceptor#onRemove
        // Please read https://github.com/arobirosa/areco-deployment-script-manager/issues/23
        interceptorContext.getModelService().saveAll();
    }
}
