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
package org.areco.ecommerce.deploymentscripts.sql;

import org.areco.ecommerce.deploymentscripts.core.impl.AbstractSingleFileScriptStepFactory;

import java.io.File;
import java.util.Locale;

/**
 * Creates a new sql step if the given file is a sql7 script.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
// The configuration of this bean is in the spring application context.
public abstract class SqlScriptStepFactory extends AbstractSingleFileScriptStepFactory {
    @Override
    protected boolean canCreateStepWith(final File aFile) {
        return aFile.getName().toLowerCase(Locale.getDefault()).endsWith(".sql");
    }
}
