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

import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;

import java.util.List;

/**
 * @author arobirosa
 */
public interface ScriptExecutionDao {
    /**
     * It returns all the scripts which were executed and then don't need to be executed again.
     *
     * @param extensionName Required.
     * @return Never null
     */
    List<ScriptExecutionModel> getSuccessfullyExecutedScripts(String extensionName);

    /**
     * Check if the last executed deployment scripts was successful.
     *
     * @return true if the last deployment script was successful.
     */
    boolean wasLastScriptSuccessful();

}
