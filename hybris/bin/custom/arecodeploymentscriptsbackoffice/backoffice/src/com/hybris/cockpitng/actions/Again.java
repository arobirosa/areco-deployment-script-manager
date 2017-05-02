/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package com.hybris.cockpitng.actions;


import org.zkoss.zul.Messagebox;


/**
 * Example of a Cockpit NG action. Shows "Hello {data}!" text according to data bound to the action.
 * If no data available the action will be disabled.
 */
public class Again implements CockpitAction<String, String>
{
  @Override
  public ActionResult<String> perform(final ActionContext<String> ctx)
  {
    ActionResult<String> result = null;
    final String data = ctx.getData();
    if (data == null)
    {
      result = new ActionResult<String>(ActionResult.ERROR);
    }
    else
    {
      result = new ActionResult<String>(ActionResult.SUCCESS, ctx.getLabel("message", new Object[] {data}));
    }
    Messagebox.show(result.getData() + " (" + result.getResultCode() + ")");

    return result;
  }

  @Override
  public boolean canPerform(final ActionContext<String> ctx)
  {
    final Object data = ctx.getData();
    return (data instanceof String) && (!((String) data).isEmpty());
  }

  @Override
  public boolean needsConfirmation(final ActionContext<String> ctx)
  {
    return true;
  }

  @Override
  public String getConfirmationMessage(final ActionContext<String> ctx)
  {
    return ctx.getLabel("confirmation.message");
  }
}
