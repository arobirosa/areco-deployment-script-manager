/**
 * Copyright 2014 Antonio Robirosa

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.core;

import java.io.File;

/**
 * It contains the contract definition of the step factories.
 * 
 * @author arobirosa
 * 
 */
public interface DeploymentScriptStepFactory {
    /**
     * It checks if the given file could be used to create a step. If not, it returns null. If yes, it returns the new step.
     * 
     * @param aFile
     *            Required
     * @return null if the file isn't used by this factory.
     */
    DeploymentScriptStep create(File aFile);

}
