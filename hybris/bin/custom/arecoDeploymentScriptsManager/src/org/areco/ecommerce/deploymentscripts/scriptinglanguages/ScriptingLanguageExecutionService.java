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

import java.io.File;

/**
 * Executes the script files.
 *
 * @author arobirosa
 */
public interface ScriptingLanguageExecutionService {

    /**
     * Runs the given script file. The extension indicates what language it is
     *
     * @param scriptFile Required
     * @return
     */

    void executeScript(File scriptFile) throws ScriptingLanguageExecutionException;
}
