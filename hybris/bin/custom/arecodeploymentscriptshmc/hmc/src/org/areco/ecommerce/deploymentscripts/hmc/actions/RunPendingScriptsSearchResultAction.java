package org.areco.ecommerce.deploymentscripts.hmc.actions;

import de.hybris.platform.core.Registry;
import de.hybris.platform.hmc.util.action.ActionEvent;
import de.hybris.platform.hmc.util.action.ActionResult;
import de.hybris.platform.hmc.util.action.SearchResultAction;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.util.localization.Localization;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStarter;

/**
 * Runs the deployment scripts which weren't run on the current database.
 *
 * Created by arobirosa on 06.05.17.
 */
public class RunPendingScriptsSearchResultAction extends SearchResultAction {

  @Override
  public ActionResult perform(final ActionEvent pActionEvent) throws JaloBusinessException {
    Logger.getLogger(this.getClass()).debug("Running pending scripts");
    final DeploymentScriptStarter starter = Registry.getApplicationContext().getBean(DeploymentScriptStarter.class);
    final boolean thereWasAnError = starter.runAllPendingScripts();
    if (thereWasAnError) {
      return new ActionResult(ActionResult.FAILED,
          Localization.getLocalizedString("runpendingdeploymentscriptsaction.message.therewasanerror"), true);
    } else {
      return new ActionResult(ActionResult.OK,
          Localization.getLocalizedString("runpendingdeploymentscriptsaction.message.noerrors"), true);
    }

  }

  @Override
  public boolean needConfirmation() {
    return false; // There is an bug and Hmc always asks for confirmation
  }

  @Override
  public String getConfirmationMessage() {
    return Localization.getLocalizedString("runpendingdeploymentscriptsaction.message.confirmation");
  }

}
