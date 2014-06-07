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

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.cronjob.ImpExImportCronJob;
import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionException;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep;


/**
 * Represents a step of the deployment script were an impex file is imported.
 * 
 * @author arobirosa
 * 
 */
public class ImpexImportStep implements DeploymentScriptStep
{
	/**
	 * This is the encoding used by the impex scripts.
	 */
	private static final String DEFAULT_IMPEX_ENCODING = "UTF-8";

	private static final Logger LOG = Logger.getLogger(ImpexImportStep.class);

	private final File impexFile;

	private final ModelService modelService;

	public ImpexImportStep(final File impexFile, final ModelService modelService)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("impexFile", impexFile);
		this.impexFile = impexFile;
		this.modelService = modelService; //TODO This service must be inyected by Spring.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep#getId()
	 */
	@Override
	public String getId()
	{
		return this.impexFile.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep#run()
	 */
	@Override
	public void run() throws DeploymentScriptExecutionException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Running the step " + this.getId());
		}
		InputStream inputStream;
		try
		{
			inputStream = new FileInputStream(this.impexFile);
		}
		catch (final FileNotFoundException e)
		{
			throw new DeploymentScriptExecutionException("There was an error opening the file " + this.impexFile, e);
		}
		inputImpexFile(inputStream);
	}

	/**
	 * @param inputStream
	 * @throws DeploymentScriptExecutionException
	 */
	private void inputImpexFile(final InputStream inputStream) throws DeploymentScriptExecutionException
	{
		//There must be a service for the impex scripts but I couldn't find it.
		final ImpExImportCronJob resultCronJob = ImpExManager.getInstance()
				.importData(inputStream, DEFAULT_IMPEX_ENCODING, true /* We allow code execution */);
		if (resultCronJob == null)
		{
			return; //Everything went ok.
		}
		else
		{
			final ImpExImportCronJobModel resultCronJobModel = this.modelService.get(resultCronJob);
			if (CronJobResult.SUCCESS.equals(resultCronJobModel.getResult()))
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Ignoring the received cronjob " + resultCronJobModel + " because the import was successful.");
				}
				return;
			}
			throw new DeploymentScriptExecutionException("There was an error importing the file " + this.impexFile
					+ ". Please check the cronjob with the code '" + resultCronJobModel.getCode() + "'");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("ImpexImportStep [impexFile=");
		builder.append(impexFile);
		builder.append(", getId()=");
		builder.append(getId());
		builder.append("]");
		return builder.toString();
	}


}
