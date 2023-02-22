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
package org.areco.ecommerce.deploymentscripts.core.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep;

import java.io.File;
import java.io.IOException;

/**
 * It represents a step which only uses one single file.
 *
 * @author Antonio Robirosa <mailto:areco.manager@areko.consulting>
 */
public abstract class AbstractSingleFileScriptStep implements DeploymentScriptStep {

    private File scriptFile;

    @Override
    public String getId() {
        if (this.scriptFile == null) {
            return null;
        }
        return this.scriptFile.getName();
    }

    protected String loadFileContent() throws IOException {
        final String sqlStatement = FileUtils.readFileToString(this.getScriptFile(), DeploymentScript.DEFAULT_FILE_ENCODING);
        if (StringUtils.isBlank(sqlStatement)) {
            throw new IllegalStateException("The file " + this.getScriptFile() + " is empty.");
        }
        return sqlStatement;
    }

    public File getScriptFile() {
        return this.scriptFile;
    }

    public void setScriptFile(final File scriptFile) {
        this.scriptFile = scriptFile;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [scriptFile=" + this.scriptFile + "]";
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.scriptFile == null) ? 0 : this.scriptFile.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractSingleFileScriptStep other = (AbstractSingleFileScriptStep) obj;
        if (this.scriptFile == null) {
            return other.scriptFile == null;
        } else {
            return this.scriptFile.equals(other.scriptFile);
        }
    }

}
