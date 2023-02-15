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
package org.areco.ecommerce.deploymentscripts.impex;

import org.areco.ecommerce.deploymentscripts.core.ScriptStepResult;
import org.areco.ecommerce.deploymentscripts.core.impl.AbstractSingleFileScriptStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Represents a step of the deployment script were an impex file is imported.
 *
 * @author arobirosa
 */
@Component
// Every time the step factory is called, it creates a new instance.
@Scope("prototype")
public class ImpexImportStep extends AbstractSingleFileScriptStep {
    private static final Logger LOG = LoggerFactory.getLogger(ImpexImportStep.class);

    @Autowired
    private ImpexImportService impexImportService;

    /*
     * { @InheritDoc }
     */
    @Override
    public ScriptStepResult run() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running the step {}", this.getId());
        }
        try {
            return this.impexImportService.importImpexFile(this.getScriptFile());
        } catch (final ImpexImportException cause) {
            return new ScriptStepResult(cause);
        }
    }
}
