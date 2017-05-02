package org.areco.ecommerce.deploymentscripts.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.actions.impl.DefaultActionRenderer;
import org.zkoss.zul.Messagebox;

/**
 * Created by arobirosa on 28.04.17.
 */
public class MyAction extends DefaultActionRenderer<String, String> implements CockpitAction<String, String>
{
        @Override
        public ActionResult<String> perform(final ActionContext<String> ctx)
        {
                ActionResult<String> result = null;
                final String data = ctx.getData();
                if (data != null)
                {
                        result = new ActionResult<String>(ActionResult.SUCCESS, ctx.getLabel("message", new Object[] { data }));
                }
                else
                {
                        result = new ActionResult<String>(ActionResult.ERROR);
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
