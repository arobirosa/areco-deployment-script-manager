/*
 * [y] hybris Platform
 * 
 * Copyright (c) 2000-2016 SAP SE
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information of SAP 
 * Hybris ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with the 
 * terms of the license agreement you entered into with SAP Hybris.
 */
package org.areco.ecommerce.deploymentscripts.hmc.setup;

import static org.areco.ecommerce.deploymentscripts.hmc.constants.ArecodeploymentscriptshmcConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import org.areco.ecommerce.deploymentscripts.hmc.constants.ArecodeploymentscriptshmcConstants;
import org.areco.ecommerce.deploymentscripts.hmc.service.ArecodeploymentscriptshmcService;


@SystemSetup(extension = ArecodeploymentscriptshmcConstants.EXTENSIONNAME)
public class ArecodeploymentscriptshmcSystemSetup
{
	private final ArecodeploymentscriptshmcService arecodeploymentscriptshmcService;

	public ArecodeploymentscriptshmcSystemSetup(final ArecodeploymentscriptshmcService arecodeploymentscriptshmcService)
	{
		this.arecodeploymentscriptshmcService = arecodeploymentscriptshmcService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		arecodeploymentscriptshmcService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return ArecodeploymentscriptshmcSystemSetup.class.getResourceAsStream("/arecodeploymentscriptshmc/sap-hybris-platform.png");
	}
}
