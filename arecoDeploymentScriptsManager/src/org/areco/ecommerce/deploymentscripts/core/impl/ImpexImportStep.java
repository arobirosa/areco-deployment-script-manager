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

import java.io.File;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionException;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep;
import org.areco.ecommerce.deploymentscripts.impex.ImpexImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Represents a step of the deployment script were an impex file is imported.
 * 
 * @author arobirosa
 * 
 */
@Component
//Every time the step factory is called, it creates a new instance.
@Scope("prototype")
public class ImpexImportStep implements DeploymentScriptStep
{
	private static final Logger LOG = Logger.getLogger(ImpexImportStep.class);

	@Autowired
	private ImpexImportService impexImportService;

	private File impexFile;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep#getId()
	 */
	@Override
	public String getId()
	{
		if (this.getImpexFile() == null)
		{
			return null;
		}
		return this.getImpexFile().getName();
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
			this.impexImportService.importImpexFile(this.getImpexFile());
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
		builder.append(this.getImpexFile());
		builder.append(", getId()=");
		builder.append(getId());
		builder.append("]");
		return builder.toString();
	}

	private File getImpexFile()
	{
		return impexFile;
	}

	public void setImpexFile(final File impexFile)
	{
		this.impexFile = impexFile;
	}

}
