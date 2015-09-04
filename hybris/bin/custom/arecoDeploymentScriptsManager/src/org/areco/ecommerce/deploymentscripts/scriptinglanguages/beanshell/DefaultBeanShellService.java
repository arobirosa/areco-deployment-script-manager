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
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.AbstractScriptingLanguageService;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageExecutionException;
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
public class DefaultBeanShellService extends AbstractScriptingLanguageService {

  @Override protected Object compileAndExecute(final String code) throws ScriptingLanguageExecutionException {
      final Interpreter beanShellInterpreter = new Interpreter();
      try {
        return beanShellInterpreter.eval(code);
      } catch (final EvalError e) {
        throw new ScriptingLanguageExecutionException("There was an error executing the code: " + e.getLocalizedMessage(), e);
      }
  }
}
