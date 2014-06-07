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
package org.areco.ecommerce.deploymentscripts.impex.impl;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.impex.jalo.ImpExException;
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
import org.areco.ecommerce.deploymentscripts.core.ImpexImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * This default implementation uses the ImpexManager which is deprecated. TODO: Find a service which imports the impex
 * files.
 * 
 * @author arobirosa
 * 
 */
@Scope("tenant")
@Service
public class JaloManagerImpexImportService implements ImpexImportService
{

	private static final Logger LOG = Logger.getLogger(JaloManagerImpexImportService.class);

	/**
	 * This is the encoding used by the impex scripts.
	 */
	private static final String DEFAULT_IMPEX_ENCODING = "UTF-8";

	@Autowired
	ModelService modelService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.ImpexImportService#inputImpexFile(java.io.File)
	 */
	@Override
	public void importImpexFile(final File impexFile) throws ImpExException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("impexFile", impexFile);

		InputStream inputStream;
		try
		{
			inputStream = new FileInputStream(impexFile);
		}
		catch (final FileNotFoundException e)
		{
			throw new ImpExException(e, "Unable to find the file " + impexFile, 0);
		}
		importImpexFile(inputStream);
	}

	private void importImpexFile(final InputStream inputStream) throws ImpExException
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
			throw new ImpExException("There was an error importing the impex file. " + "Please check the cronjob with the code '"
					+ resultCronJobModel.getCode() + "'");
		}
	}

}
