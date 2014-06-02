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

import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.io.File;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep;


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

	public ImpexImportStep(final File impexFile)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("impexFile", impexFile);
		this.impexFile = impexFile;
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
	public void run()
	{
		// YTODO Auto-generated method stub

	}

}
