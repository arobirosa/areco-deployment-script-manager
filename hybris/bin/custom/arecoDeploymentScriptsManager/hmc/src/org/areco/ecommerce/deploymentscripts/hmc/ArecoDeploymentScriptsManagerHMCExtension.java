/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package org.areco.ecommerce.deploymentscripts.hmc;

import de.hybris.platform.hmc.AbstractEditorMenuChip;
import de.hybris.platform.hmc.AbstractExplorerMenuTreeNodeChip;
import de.hybris.platform.hmc.EditorTabChip;
import de.hybris.platform.hmc.extension.HMCExtension;
import de.hybris.platform.hmc.extension.MenuEntrySlotEntry;
import de.hybris.platform.hmc.generic.ClipChip;
import de.hybris.platform.hmc.generic.ToolbarActionChip;
import de.hybris.platform.hmc.webchips.Chip;
import de.hybris.platform.hmc.webchips.DisplayState;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


/**
 * Provides necessary meta information about the arecoDeploymentScriptsManager hmc extension.
 * 
 * 
 * @version ExtGen v4.1
 */
public class ArecoDeploymentScriptsManagerHMCExtension extends HMCExtension
{
	/** Edit the local|project.properties to change logging behavior (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ArecoDeploymentScriptsManagerHMCExtension.class.getName());

	/** Path to the resource bundles. */
	public final static String RESOURCE_PATH = "org.areco.ecommerce.deploymentscripts.hmc.locales";


	/**
	 * @see HMCExtension#getTreeNodeChips(de.hybris.platform.hmc.webchips.DisplayState,
	 *      de.hybris.platform.hmc.webchips.Chip)
	 */
	@Override
	public List<AbstractExplorerMenuTreeNodeChip> getTreeNodeChips(final DisplayState displayState, final Chip parent)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @see HMCExtension#getMenuEntrySlotEntries(de.hybris.platform.hmc.webchips.DisplayState,
	 *      de.hybris.platform.hmc.webchips.Chip)
	 */
	@Override
	public List<MenuEntrySlotEntry> getMenuEntrySlotEntries(final DisplayState displayState, final Chip parent)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @see HMCExtension#getSectionChips(de.hybris.platform.hmc.webchips.DisplayState,
	 *      de.hybris.platform.hmc.generic.ClipChip)
	 */
	@Override
	public List<ClipChip> getSectionChips(final DisplayState displayState, final ClipChip parent)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * Used by hmc.
	 */
	@Override
	public List<EditorTabChip> getEditorTabChips(final DisplayState displayState, final AbstractEditorMenuChip parent)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @see HMCExtension#getToolbarActionChips(de.hybris.platform.hmc.webchips.DisplayState,
	 *      de.hybris.platform.hmc.webchips.Chip)
	 */
	@Override
	public List<ToolbarActionChip> getToolbarActionChips(final DisplayState displayState, final Chip parent)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * Returns the resource bundle.
	 */
	@Override
	public ResourceBundle getLocalizeResourceBundle(final Locale locale)
	{
		return null;
	}

	/**
	 * Returns the path to the resource bundle.
	 */
	@Override
	public String getResourcePath()
	{
		return RESOURCE_PATH;
	}
}
