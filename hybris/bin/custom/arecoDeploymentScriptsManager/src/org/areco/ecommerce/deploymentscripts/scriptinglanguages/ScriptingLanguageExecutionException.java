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
package org.areco.ecommerce.deploymentscripts.scriptinglanguages;

/**
 * There was an error while running BeanShell code. The caller has to manage this exception.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
public class ScriptingLanguageExecutionException extends Exception {

    /**
     * Default constructor with a message and a cause
     *
     * @param message Required
     * @param cause   Can be null
     */
    public ScriptingLanguageExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a message.
     *
     * @param message Required
     */
    public ScriptingLanguageExecutionException(final String message) {
        super(message);
    }

    /**
     * Constructor with a cause
     *
     * @param cause Required
     */
    public ScriptingLanguageExecutionException(final Throwable cause) {
        super(cause);
    }
}
