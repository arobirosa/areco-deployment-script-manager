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
package org.areco.ecommerce.deploymentscripts.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a checked exception because the caller has to manage it. It usually means setting the execution of the deployment script to some error state.
 *
 * @author arobirosa
 */
public class DeploymentScriptExecutionException extends Exception {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentScriptExecutionException.class);



    /**
     * Default constructor with a message and a cause for the factory
     *
     * @param message
     * @param cause
     */
    DeploymentScriptExecutionException(final String message, final Throwable cause) {
        // package-private access used by the factory
        super(message, cause);
    }

    /**
     * Constructor with a message for the factory.
     *
     * @param message
     */
    DeploymentScriptExecutionException(final String message) {
        // package-private access used by the factory
        super(message);
    }
}
