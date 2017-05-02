/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package org.areco.ecommerce.deploymentscripts.backoffice.widgets;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;

import org.areco.ecommerce.deploymentscripts.backoffice.services.ArecodeploymentscriptsbackofficeService;

import com.hybris.cockpitng.util.DefaultWidgetController;


public class ArecodeploymentscriptsbackofficeController extends DefaultWidgetController
{
	private static final long serialVersionUID = 1L;
	private Label label;

	@WireVariable
	private ArecodeploymentscriptsbackofficeService arecodeploymentscriptsbackofficeService;

	@Override
	public void initialize(final Component comp)
	{
		super.initialize(comp);
		label.setValue(arecodeploymentscriptsbackofficeService.getHello() + " ArecodeploymentscriptsbackofficeController");
	}
}
