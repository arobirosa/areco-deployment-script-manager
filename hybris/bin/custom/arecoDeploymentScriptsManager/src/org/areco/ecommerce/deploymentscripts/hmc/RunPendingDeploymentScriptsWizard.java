package org.areco.ecommerce.deploymentscripts.hmc;

import de.hybris.platform.core.Registry;
import de.hybris.platform.hmc.jalo.VetoException;
import de.hybris.platform.hmc.jalo.WizardEditorContext;
import de.hybris.platform.util.localization.Localization;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStarter;

/**
 * This hmc wizard runs all the deployment scripts which weren't executed yet.
 * 
 * @author arobirosa
 * 
 */
public class RunPendingDeploymentScriptsWizard extends GeneratedRunPendingDeploymentScriptsWizard {
    private static final Logger LOG = Logger.getLogger(RunPendingDeploymentScriptsWizard.class);

    /*
     * { @InheritDoc }
     */
    @Override
    public void start(final WizardEditorContext ctx) throws VetoException {
        super.start(ctx);
        if (LOG.isInfoEnabled()) {
            LOG.info("Starting wizard.");
        }
        ctx.hideButton(NEXT_BUTTON);
        ctx.hideButton(BACK_BUTTON);
        final DeploymentScriptStarter starter = Registry.getApplicationContext().getBean(DeploymentScriptStarter.class);
        final boolean thereWasAnError = starter.runAllPendingScripts();
        if (thereWasAnError) {
            ctx.showErrorTab(Localization.getLocalizedString("runpendingdeploymentscriptswizard.message.therewasanerror"));
        } else {
            ctx.showSummaryTab(Localization.getLocalizedString("runpendingdeploymentscriptswizard.message.noerrors"));
        }
    }

    /*
     * { @InheritDoc }
     */
    @Override
    public void initialize(final WizardEditorContext ctx) {
        super.initialize(ctx);
        ctx.enableButton(START_BUTTON);
    }

}
