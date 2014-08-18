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

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionException;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptRunner;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDAO;
import org.areco.ecommerce.deploymentscripts.core.UpdatingSystemExtensionContext;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * Default script runner.
 * 
 * @author arobirosa
 * 
 */
@Service
@Scope("tenant")
public class ArecoDeploymentScriptsRunner implements DeploymentScriptRunner
{

	private static final Logger LOG = Logger.getLogger(ArecoDeploymentScriptsRunner.class);

	@Autowired
	ModelService modelService;

	@Autowired
	//We inject by name because Spring can't see the generic parameters.
	@Qualifier("deploymentScript2ExecutionConverter")
	Converter<DeploymentScript, ScriptExecutionModel> scriptConverter;

	@Autowired
	private ScriptExecutionResultDAO scriptExecutionResultDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptRunner#run(java.util.List)
	 */
	@Override
	public boolean run(final UpdatingSystemExtensionContext updatingSystemContext, final List<DeploymentScript> scriptsToBeRun)
	{
		for (final DeploymentScript aScript : scriptsToBeRun)
		{
			final ScriptExecutionModel scriptExecution = this.scriptConverter.convert(aScript);

			try
			{
				final ScriptExecutionResultModel scriptResult = aScript.run();
				scriptExecution.setResult(scriptResult);
			}
			catch (final DeploymentScriptExecutionException e)
			{
				LOG.error("There was an error running " + aScript.getLongName() + ':' + e.getLocalizedMessage(), e);
				scriptExecution.setResult(this.scriptExecutionResultDao.getErrorResult());
				this.saveAndLogScriptExecution(updatingSystemContext, scriptExecution);
				return true;//We stop after the first error.
			}
			this.saveAndLogScriptExecution(updatingSystemContext, scriptExecution);
		}
		return false; //Everything when successfully
	}

	private void saveAndLogScriptExecution(final UpdatingSystemExtensionContext context, final ScriptExecutionModel scriptExecution)
	{
		modelService.save(scriptExecution);
		context.logScriptExecutionResult(scriptExecution);
	}
}
