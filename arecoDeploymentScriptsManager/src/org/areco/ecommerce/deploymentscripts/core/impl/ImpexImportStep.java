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

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.io.File;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionException;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep;
import org.areco.ecommerce.deploymentscripts.core.ImpexImportService;


/**
 * Represents a step of the deployment script were an impex file is imported.
 * 
 * @author arobirosa
 * 
 */
public class ImpexImportStep implements DeploymentScriptStep
{
	private static final Logger LOG = Logger.getLogger(ImpexImportStep.class);

	private final File impexFile;

	private final ImpexImportService impexImportService;

	public ImpexImportStep(final File impexFile, final ImpexImportService impexImportService)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("impexFile", impexFile);
		this.impexFile = impexFile;
		this.impexImportService = impexImportService; //TODO This service must be injected by Spring.
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
		try
		{
			this.impexImportService.importImpexFile(this.impexFile);
		}
		catch (final ImpExException cause)
		{
			throw new DeploymentScriptExecutionException("There was an error importing the step " + this.getId(), cause);
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
