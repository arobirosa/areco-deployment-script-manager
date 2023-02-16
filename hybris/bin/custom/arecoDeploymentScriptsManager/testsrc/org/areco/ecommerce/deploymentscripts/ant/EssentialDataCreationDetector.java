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
package org.areco.ecommerce.deploymentscripts.ant;

import de.hybris.platform.core.initialization.SystemSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * It is used by the tests to check if the essential data creation was started. The tests are created without using Spring, so the systemsetup annotation
 * doesn't work.
 *
 * @author arobirosa
 */
@Scope("tenant")
@Component
@SystemSetup(extension = "ALL_EXTENSIONS")
public class EssentialDataCreationDetector {
    private static final Logger LOG = LoggerFactory.getLogger(EssentialDataCreationDetector.class);

    private boolean wasEssentialDataCreated = false;
    private boolean wasProjectDataCreated = false;

    /**
     * This method must be triggered during the creation of the essential data.
     */
    @SuppressWarnings("unused")
    @SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.ALL)
    public void createDummyEssentialData() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("The essential data creation was triggered.");
        }
        this.wasEssentialDataCreated = true;
    }

    /**
     * Was the essential data triggered.
     *
     * @return true if the essential data was triggered.
     */
    public boolean isWasEssentialDataCreated() {
        return this.wasEssentialDataCreated;
    }

    /**
     * This method must be triggered during the creation of the project data.
     */
    @SuppressWarnings("unused")
    @SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL)
    public void createDummyProjectData() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("The project data creation was triggered.");
        }
        this.wasProjectDataCreated = true;
    }

    /**
     * Was the project data triggered.
     *
     * @return true if the project data was triggered.
     */
    public boolean isWasProjectDataCreated() {
        return this.wasProjectDataCreated;
    }
}
