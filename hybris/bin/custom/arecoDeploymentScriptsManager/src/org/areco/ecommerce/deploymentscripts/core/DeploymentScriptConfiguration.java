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

import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;

/**
 * It represents the definition of the properties of a deployment script
 *
 * @author arobirosa
 */
public interface DeploymentScriptConfiguration {

    /**
     * Checks if this script is allowed to run in this server.
     *
     * @return null if it is allowed to run. Otherwise it returns the execution result.
     */
    ScriptExecutionResultModel reasonToIgnoreExecutionOnThisServer();

    /**
     * Returns if this script runs once or multiple times by every essential or project data creation step
     *
     * @return true if this areco must be executed multiple times
     */
    boolean runsMultipleTimes();

    /**
     * Returns if this script executes for many seconds. If yes, the runner will wait until the execution is finished for many minutes.
     *
     * @return true if this areco must run for longer than 30 seconds
     */
    boolean hasLongExecution();
}
