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
package org.areco.ecommerce.deploymentscripts.beanshell.impl;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.beanshell.BeanShellExecutionException;
import org.areco.ecommerce.deploymentscripts.beanshell.BeanShellService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import bsh.EvalError;
import bsh.Interpreter;


/**
 * It runs Bean Shell code and check that it was successful.
 * 
 * @author arobirosa
 * 
 */
@Service
@Scope("tenant")
public class DefaultBeanShellService implements BeanShellService
{
	private static final Logger LOG = Logger.getLogger(DefaultBeanShellService.class);

	/*
	 * It compiles and executes the given beanshell code.
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.beanshell.BeanShellService#executeScript(java.lang.String)
	 */
	@Override
	public void executeScript(final String beanShellCode) throws BeanShellExecutionException
	{
		if (beanShellCode == null || beanShellCode.trim().isEmpty())
		{
			throw new IllegalArgumentException("The parameter beanShellCode cannot be null");
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Running the code '" + beanShellCode + "'.");
		}

		final Interpreter beanShellInterpreter = new Interpreter();
		try
		{
			checkSuccessfulResult(beanShellInterpreter.eval(beanShellCode));
		}
		catch (final EvalError e)
		{
			throw new BeanShellExecutionException("There was an error executing the bean shell code: " + e.getLocalizedMessage(), e);
		}
	}

	private void checkSuccessfulResult(final Object anObject) throws BeanShellExecutionException
	{
		if (anObject instanceof String && "OK".equalsIgnoreCase((String) anObject))
		{
			return;
		}
		throw new BeanShellExecutionException(
				"The beanShell code didn't return the string 'OK' at the end. Please check if there was an error.");
	}
}
