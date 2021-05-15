/**
 * Copyright 2021 Antonio Robirosa
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

package org.areco.ecommerce.deploymentscripts.exceptions;

/*+
 It makes sure that the exception is created in the correct way.
**/

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Objects;

import static java.util.Objects.isNull;

public class DeploymentScriptExecutionExceptionFactory implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    /**
     * Returns a new exception containing the given message
     *
     * @param message Required
     * @param cause   Required
     * @return Never null
     */
    public DeploymentScriptExecutionException newWith(final String message, final Throwable cause) {
        Objects.requireNonNull(message, "The message argument is null");
        Objects.requireNonNull(cause, "The cause argument is null");
        return getApplicationContext().getBean(DeploymentScriptExecutionException.class, message, cause);
    }

    /**
     * Returns a new exception containing the given message
     *
     * @param message Required
     * @return Never null
     */
    public DeploymentScriptExecutionException newWith(final String message) {
        Objects.requireNonNull(message, "The message argument is null");
        return getApplicationContext().getBean(DeploymentScriptExecutionException.class, message);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        if (isNull(this.applicationContext)) {
            throw new IllegalStateException("The application context of DeploymentScriptExecutionExceptionFactory is null");
        }
        return this.applicationContext;
    }
}
