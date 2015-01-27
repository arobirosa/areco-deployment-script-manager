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
package org.areco.ecommerce.deploymentscripts.core.impl;

import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.io.File;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStepFactory;

/**
 * Its subclasses create the step which only require one single file.
 * 
 * @author arobirosa
 * 
 */
public abstract class AbstractSingleFileScriptStepFactory implements DeploymentScriptStepFactory {
    private static final Logger LOG = Logger.getLogger(AbstractSingleFileScriptStepFactory.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.areco.ecommerce.deploymentscripts.core.impl.DeploymentScriptStepFactory#create(java.io.File)
     */
    @Override
    public AbstractSingleFileScriptStep create(final File aFile) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Trying to create a single file script step from the file " + aFile);
        }
        ServicesUtil.validateParameterNotNullStandardMessage("aFile", aFile);
        if (canCreateStepWith(aFile)) {
            final AbstractSingleFileScriptStep aStep = this.createStep();
            aStep.setScriptFile(aFile);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Created step: " + aStep);
            }
            return aStep;
        } else {
            return null;
        }
    }

    protected abstract boolean canCreateStepWith(final File aFile);

    // Used by Spring to create new instances.
    protected abstract AbstractSingleFileScriptStep createStep();
}
