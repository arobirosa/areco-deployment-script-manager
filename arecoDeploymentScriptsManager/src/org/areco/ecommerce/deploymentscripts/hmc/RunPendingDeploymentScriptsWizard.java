package org.areco.ecommerce.deploymentscripts.hmc;

import de.hybris.platform.core.Registry;
import de.hybris.platform.hmc.jalo.VetoException;
import de.hybris.platform.hmc.jalo.WizardEditorContext;
import de.hybris.platform.util.localization.Localization;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStarter;


public class RunPendingDeploymentScriptsWizard extends GeneratedRunPendingDeploymentScriptsWizard
{
	private final static Logger LOG = Logger.getLogger(RunPendingDeploymentScriptsWizard.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.hmc.jalo.WizardBusinessItem#start(de.hybris.platform.hmc.jalo.WizardEditorContext)
	 */
	@Override
	public void start(final WizardEditorContext ctx) throws VetoException
	{
		super.start(ctx);
		if (LOG.isInfoEnabled())
		{
			LOG.info("Starting wizard.");
		}
		ctx.hideButton(NEXT_BUTTON);
		ctx.hideButton(BACK_BUTTON);
		final DeploymentScriptStarter starter = Registry.getApplicationContext().getBean(DeploymentScriptStarter.class);
		final boolean thereWasAnError = starter.runAllPendingScripts();
		if (thereWasAnError)
		{
			ctx.showErrorTab(Localization.getLocalizedString("runpendingdeploymentscriptswizard.message.therewasanerror"));
		}
		else
		{
			ctx.showSummaryTab(Localization.getLocalizedString("runpendingdeploymentscriptswizard.message.noerrors"));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.hmc.jalo.WizardBusinessItem#initialize(de.hybris.platform.hmc.jalo.WizardEditorContext)
	 */
	@Override
	public void initialize(final WizardEditorContext ctx)
	{
		super.initialize(ctx);
		ctx.enableButton(START_BUTTON);
	}



}
