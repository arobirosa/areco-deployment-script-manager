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

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * It takes a deployment script and returns an unsaved ScriptExecutionModel.
 * 
 * @author arobirosa
 * 
 */
@Scope("tenant")
@Component("deploymentScript2ExecutionConverter")
public class DeploymentScript2ExecutionConverter implements Converter<DeploymentScript, ScriptExecutionModel>
{
	private static final Logger LOG = Logger.getLogger(DeploymentScript2ExecutionConverter.class);

	@Autowired
	ModelService modelService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public ScriptExecutionModel convert(final DeploymentScript source) throws ConversionException
	{
		return this.convert(source, (ScriptExecutionModel) this.modelService.create(ScriptExecutionModel.class));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public ScriptExecutionModel convert(final DeploymentScript source, final ScriptExecutionModel execution)
			throws ConversionException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("execution", execution);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Creating an script execution model from the deployment script " + source);
		}
		execution.setExtensionName(source.getExtensionName());
		execution.setScriptName(source.getName());
		execution.setResult(null); //The caller must set the result before saving the execution.
		execution.setPhase(source.getPhase());

		return execution;
	}

}
