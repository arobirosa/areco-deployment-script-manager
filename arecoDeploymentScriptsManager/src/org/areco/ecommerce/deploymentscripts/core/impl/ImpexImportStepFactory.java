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
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStepFactory;


/**
 * Creates a new impexDeploymentScriptStep if the given file is a impex script.
 * 
 * @author arobirosa
 * 
 */
//The configuration of this bean is in the spring application context.
public abstract class ImpexImportStepFactory implements DeploymentScriptStepFactory
{
	private static final Logger LOG = Logger.getLogger(ImpexImportStepFactory.class);

	/* (non-Javadoc)
	 * @see org.areco.ecommerce.deploymentscripts.core.impl.DeploymentScriptStepFactory#create(java.io.File)
	 */
	@Override
	public ImpexImportStep create(final File aFile)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Trying to create a impex import step from the file " + aFile);
		}
		ServicesUtil.validateParameterNotNullStandardMessage("aFile", aFile);
		if (aFile.getName().toLowerCase().endsWith(".impex"))
		{
			final ImpexImportStep aStep = this.createStep();
			aStep.setImpexFile(aFile);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Created step: " + aStep);
			}
			return aStep;
		}
		else
		{
			return null;
		}
	}

	protected abstract ImpexImportStep createStep();
}
