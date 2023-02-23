/**
 * Copyright 2014 Antonio Robirosa
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.scriptinglanguages.impl;

import org.areco.ecommerce.deploymentscripts.core.ScriptStepResult;
import org.areco.ecommerce.deploymentscripts.core.impl.AbstractSingleFileScriptStep;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageExecutionException;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * It represents a groovy script.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
@Component
// Every time the step factory is called, it creates a new instance.
@Scope("prototype")
public class ScriptingLanguageStep extends AbstractSingleFileScriptStep {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptingLanguageStep.class);

    @Autowired
    private ScriptingLanguageExecutionService scriptingLanguageExecutionService;

    /*
     * Runs the script represented by this step.
     *
     * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep#run()
     */
    @Override
    public ScriptStepResult run() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running the script {}", this.getId());
        }
        try {
            this.scriptingLanguageExecutionService.executeScript(this.getScriptFile());
        } catch (final ScriptingLanguageExecutionException e) {
            return new ScriptStepResult(e);
        }
        return new ScriptStepResult(true);
    }

}
