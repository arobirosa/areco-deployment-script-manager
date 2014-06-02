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
package org.areco.ecommerce.deploymentscripts.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionDao;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;


/**
 * Default implementatio of the dao.
 * 
 * @author arobirosa
 * 
 */
@Repository
@Scope("tenant")
public class FlexibleSearchScriptExecutionDao implements ScriptExecutionDao
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.areco.ecommerce.deploymentscripts.core.ScriptExecutionDao#getSuccessfullyExecutedScripts(java.lang.String)
	 */
	@Override
	public List<ScriptExecutionModel> getSuccessfullyExecutedScripts(final String extensionName)
	{
		// YTODO Auto-generated method stub
		return new ArrayList<ScriptExecutionModel>();
	}
}
