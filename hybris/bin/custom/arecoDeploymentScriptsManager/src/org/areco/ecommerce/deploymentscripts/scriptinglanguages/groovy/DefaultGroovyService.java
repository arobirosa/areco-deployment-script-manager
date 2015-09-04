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
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageExecutionException;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageService;
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
public class DefaultGroovyService implements ScriptingLanguageService {
    private static final Logger LOG = Logger.getLogger(DefaultGroovyService.class);

  /**
   * Executes the groovy script which must return "OK".
   *
   * @param code Required.
   * @throws ScriptingLanguageExecutionException If there was an error or the script didn't return "OK"
   */
    @Override
    public void executeScript(final String code) throws ScriptingLanguageExecutionException {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("The parameter code cannot be null");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running the code '" + code + "'.");
        }
        //We don't bind any bean.
        final Map<String, Object> emptyContext = new HashMap<String, Object>();

        final Binding binding = new Binding(emptyContext);
        final GroovyShell groovyShell = new GroovyShell(binding);
        try {
            checkSuccessfulResult(groovyShell.evaluate(code));
        } catch (final GroovyRuntimeException e) {
            throw new ScriptingLanguageExecutionException("There was an error executing the code: " + e.getLocalizedMessage(), e);
        }
    }

    private void checkSuccessfulResult(final Object anObject) throws ScriptingLanguageExecutionException {
        if (anObject instanceof String && "OK".equalsIgnoreCase((String) anObject)) {
            return;
        }
        throw new ScriptingLanguageExecutionException("The groovy code didn't return the string 'OK' but '" + anObject + "'. Please check if there was an error.");
    }
}
