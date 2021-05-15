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
package org.areco.ecommerce.deploymentscripts.impex;

/**
 * It informs that caller that an error happened during an impex script step. This is a checked exception because the caller has to manage it.
 * It don't inherit from hybris' exception hierarchy because it must be only used inside the Areco deployment manager extension.
 *
 * @author arobirosa
 *
 */
public class ImpexImportException extends Exception {

    /**
     * Default constructor with a message and a cause
     *
     * @param message Optional
     * @param cause Optional
     */
    public ImpexImportException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a message.
     *
     * @param message Optional
     */
    public ImpexImportException(final String message) {
        super(message);
    }

    /**
     * Constructor with a cause
     *
     * @param cause Optional
     */
    public ImpexImportException(final Throwable cause) {
        super(cause);
    }
}
