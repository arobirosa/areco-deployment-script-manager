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
package org.areco.ecommerce.deploymentscripts.groovy.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.groovy.GroovyExecutionException;
import org.areco.ecommerce.deploymentscripts.groovy.GroovyService;
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
public class DefaultGroovyService implements GroovyService {
    private static final Logger LOG = Logger.getLogger(DefaultGroovyService.class);

    /*
     * It compiles and executes the given Groovy code.
     * 
     * @see org.areco.ecommerce.deploymentscripts.groovy.GroovyShellService#executeScript(java.lang.String)
     */
    @Override
    public void executeScript(final String code) throws GroovyExecutionException {
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
            throw new GroovyExecutionException("There was an error executing the groovy code: " + e.getLocalizedMessage(), e);
        }
    }

    private void checkSuccessfulResult(final Object anObject) throws GroovyExecutionException {
        if (anObject instanceof String && "OK".equalsIgnoreCase((String) anObject)) {
            return;
        }
        throw new GroovyExecutionException("The groovy code didn't return the string 'OK' but '" + anObject + "'. Please check if there was an error.");
    }
}
