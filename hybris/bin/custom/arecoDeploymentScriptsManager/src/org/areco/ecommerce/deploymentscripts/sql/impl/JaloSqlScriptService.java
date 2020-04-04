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
package org.areco.ecommerce.deploymentscripts.sql.impl;

import de.hybris.platform.core.Registry;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.sql.SqlScriptService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * This implementation uses the jalo layer.
 * <p/>
 * TODO Find a way to execute SQL without using Jalo.
 *
 * @author arobirosa
 */
@Service @Scope("tenant") public class JaloSqlScriptService implements SqlScriptService {

        private static final Logger LOG = Logger.getLogger(JaloSqlScriptService.class);

        // CHECKSTYLE.OFF: This annotation generates a long line.
        @edu.umd.cs.findbugs.annotations.SuppressWarnings(
                value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
                justification = "The SQL coming from deployment scripts is saved on the server and the user can't modify it.")
        // CHECKSTYLE.ON
        //The variable affectedRows is needed because the cache must be cleared afterwards.
        @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn") @Override public int runDeleteOrUpdateStatement(final String aStatement) throws SQLException {
                if (StringUtils.isBlank(aStatement)) {
                        throw new IllegalArgumentException("The parameter aStatement is empty.");
                }
                if (aStatement.trim().toUpperCase(Locale.getDefault()).startsWith("SELECT")) {
                        throw new SQLException("The sql statement can't start with select.");
                }
                final String translatedStatement = translateTablePrefix(aStatement);

                final int affectedRows = runStatementOnDatabase(translatedStatement);

                /* Because clearing the cache doesn't remove the old values of the attributes saved in the model instances, a refresh of the models
                  is required to get the new values stored in the database. */
                Registry.getCurrentTenant().getCache().clear();
                return affectedRows;
        }

        // CHECKSTYLE.OFF: This annotation generates a long line.
        @edu.umd.cs.findbugs.annotations.SuppressWarnings(
                value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
                justification = "The SQL coming from deployment scripts is saved on the server and the user can't modify it.")
        // CHECKSTYLE.ON
        private int runStatementOnDatabase(final String translatedStatement) throws SQLException {

                try (Connection aConnection = getConnection(); PreparedStatement prepareStatement = aConnection.prepareStatement(translatedStatement)) {
                        return prepareStatement.executeUpdate();
                }
        }

        private String translateTablePrefix(final String aStatement) {
                if (LOG.isDebugEnabled()) {
                        LOG.debug("SQL Statement before the translation: <" + aStatement + ">");
                }
                Pattern tablePrefixPattern = Pattern.compile("\\{table_prefix\\}", Pattern.CASE_INSENSITIVE);
                final String returnedStatement = tablePrefixPattern.matcher(aStatement)
                        .replaceAll(Registry.getCurrentTenant().getDataSource().getTablePrefix());
                if (LOG.isDebugEnabled()) {
                        LOG.debug("SQL Statement after the translation: <" + returnedStatement + ">");
                }
                return returnedStatement;
        }

        private Connection getConnection() throws SQLException {
                return Registry.getCurrentTenant().getDataSource().getConnection();
        }
}
