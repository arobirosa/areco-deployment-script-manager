/**
 * Copyright 2017 Antonio Robirosa

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
package org.areco.ecommerce.deploymentscripts.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.actions.impl.DefaultActionRenderer;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStarter;
import org.zkoss.zul.Messagebox;

import javax.annotation.Resource;

/**
 * This backoffice action runs all deployment scripts which weren't run on the current database.
 *
 * Created by arobirosa on 28.04.17.
 */
public class RunPendingScriptsAction extends DefaultActionRenderer<String, String> implements CockpitAction<String, String> {
  @Resource
  private DeploymentScriptStarter starter;


  @Override
  public ActionResult<String> perform(final ActionContext<String> ctx) {
    final ActionResult<String> result;
    final boolean thereWasAnError = starter.runAllPendingScripts();
    if (thereWasAnError) {
      result = new ActionResult<>(ActionResult.ERROR);
      Messagebox.show(ctx.getLabel("message.therewasanerror"));
    } else {
      result = new ActionResult<>(ActionResult.SUCCESS);
    }
    return result;
  }

  @Override
  public boolean canPerform(final ActionContext<String> ctx) {
    return true; // No external data is required
  }

  @Override
  public boolean needsConfirmation(final ActionContext<String> ctx) {
    return false;
  }
}
