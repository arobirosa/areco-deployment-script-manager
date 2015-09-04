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
package org.areco.ecommerce.deploymentscripts.scriptinglanguages.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageExecutionException;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * It runs Bean Shell code and check that it was successful.
 * 
 * @author arobirosa
 * 
 */
@Service
@Scope("tenant")
public class DefaultBeanShellService implements ScriptingLanguageService {
    private static final Logger LOG = Logger.getLogger(DefaultBeanShellService.class);

  /**
   * It executes and compiles the given Bean Shell Script.
   *
   * @param beanShellCode
   * @throws ScriptingLanguageExecutionException
   */
    @Override
    public void executeScript(final String beanShellCode) throws ScriptingLanguageExecutionException {
        if (beanShellCode == null || beanShellCode.trim().isEmpty()) {
            throw new IllegalArgumentException("The parameter beanShellCode cannot be null");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running the code '" + beanShellCode + "'.");
        }

        final Interpreter beanShellInterpreter = new Interpreter();
        try {
            checkSuccessfulResult(beanShellInterpreter.eval(beanShellCode));
        } catch (final EvalError e) {
            throw new ScriptingLanguageExecutionException("There was an error executing the bean shell code: " + e.getLocalizedMessage(), e);
        }
    }

    private void checkSuccessfulResult(final Object anObject) throws ScriptingLanguageExecutionException {
        if (anObject instanceof String && "OK".equalsIgnoreCase((String) anObject)) {
            return;
        }
        throw new ScriptingLanguageExecutionException("The beanShell code didn't return the string 'OK' but '"
                + anObject + "'. Please check if there was an error.");
    }
}
