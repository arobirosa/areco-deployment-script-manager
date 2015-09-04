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
package org.areco.ecommerce.deploymentscripts.scriptinglanguages.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.AbstractScriptingLanguageService;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageExecutionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * It runs Groovy code and check that it was successful.
 * 
 * @author arobirosa
 * 
 */
@Service
@Scope("tenant")
public class DefaultGroovyService extends AbstractScriptingLanguageService {

  @Override protected Object compileAndExecute(final String code) throws ScriptingLanguageExecutionException {
    //We don't bind any bean.
    final Map<String, Object> emptyContext = new HashMap<String, Object>();

    final Binding binding = new Binding(emptyContext);
    final GroovyShell groovyShell = new GroovyShell(binding);

    try {
      return groovyShell.evaluate(code);
    } catch (final GroovyRuntimeException e) {
      throw new ScriptingLanguageExecutionException("There was an error executing the code: " + e.getLocalizedMessage(), e);
    }
  }
}
