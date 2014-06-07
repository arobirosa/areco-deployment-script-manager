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

import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;


/**
 * It manages the script execution result instances which aren't a Hybris Enumeration because their have behavior.
 * 
 * @author arobirosa
 * 
 */
public interface ScriptExecutionResultDAO
{

	/**
	 * Returns the instance which represents an error.
	 * 
	 * @return ScriptExecutionResultModel Never null.
	 */
	ScriptExecutionResultModel getErrorResult();

	/**
	 * Returns the instance which represents an error.
	 * 
	 * @return ScriptExecutionResultModel Never null.
	 */
	ScriptExecutionResultModel getSuccessResult();

	/**
	 * It checks if the results are already in the database.
	 * 
	 * @return boolean True if they are
	 */
	boolean theInitialResultsWereImported();

	/**
	 * It loads the internal data. This method may be called many times.
	 */
	void initialize();
}
