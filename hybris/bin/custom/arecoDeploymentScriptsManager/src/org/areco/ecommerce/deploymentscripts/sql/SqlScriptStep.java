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

import org.areco.ecommerce.deploymentscripts.core.ScriptStepResult;
import org.areco.ecommerce.deploymentscripts.core.impl.AbstractSingleFileScriptStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Represents an SQL script.
 *
 * @author Antonio Robirosa <mailto:areco.manager@areko.consulting>
 */
@Component
// Every time the step factory is called, it creates a new instance.
@Scope("prototype")
public class SqlScriptStep extends AbstractSingleFileScriptStep {
    private static final Logger LOG = LoggerFactory.getLogger(SqlScriptStep.class);

    @Autowired
    private SqlScriptService sqlScriptService;

    /*
     * { @InheritDoc }
     */
    @Override
    public ScriptStepResult run() {
        final String sqlStatement;
        try {
            sqlStatement = loadFileContent();
        } catch (final IOException | IllegalStateException e) {
            return new ScriptStepResult(e);
        }
        return executeStatement(sqlStatement);
    }

    private ScriptStepResult executeStatement(final String sqlStatement) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running the SQL Statement: '{}'.", sqlStatement);
        }
        final int rows;

        try {
            rows = this.sqlScriptService.runDeleteOrUpdateStatement(sqlStatement);
        } catch (final SQLException e) {
            return new ScriptStepResult(e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("The SQL Script was executed successfully. {} rows were affected.", rows);
        }
        return new ScriptStepResult(true);
    }
}
