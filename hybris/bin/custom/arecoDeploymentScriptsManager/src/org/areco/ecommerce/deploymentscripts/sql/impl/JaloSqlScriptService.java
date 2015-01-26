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
package org.areco.ecommerce.deploymentscripts.sql.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.regioncache.CacheController;
import de.hybris.platform.regioncache.region.CacheRegion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.sql.SqlScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * This implementation uses the jalo layer.
 * 
 * TODO Find a way to execute SQL without using Jalo.
 * 
 * @author arobirosa
 * 
 */
@Service
@Scope("tenant")
public class JaloSqlScriptService implements SqlScriptService {

    private static final Logger LOG = Logger.getLogger(JaloSqlScriptService.class);

    @Autowired
    CacheController cacheController;

    /*
     * { @InheritDoc }
     */
    // CHECKSTYLE.OFF: This annotation generates a long line.
    @Override
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING", justification = "The SQL coming from deployment scripts is saved on the server and the user can't modify it.")
    // CHECKSTYLE.ON
    public int runDeleteOrUpdateStatement(final String aStatement) throws SQLException {
        if (aStatement == null || aStatement.trim().isEmpty()) {
            throw new IllegalArgumentException("The parameter aStatement is empty.");
        }
        if (!aStatement.trim().toUpperCase().startsWith("UPDATE") && !aStatement.trim().toUpperCase().startsWith("DELETE")) {
            throw new SQLException("The sql statement must start with update or delete.");
        }
        final String translatedStatement = translateTablePrefix(aStatement);

        int affectedRows = -1;
        Connection aConnection = null;
        PreparedStatement prepareStatement = null;
        try {
            aConnection = getConnection();
            aConnection.setAutoCommit(true);
            prepareStatement = aConnection.prepareStatement(translatedStatement);
            affectedRows = prepareStatement.executeUpdate();
            aConnection.commit();
        } finally {
            if (prepareStatement != null) {
                prepareStatement.close();
            }
            if (aConnection != null) {
                try {
                    aConnection.close();
                } catch (final SQLException e) {
                    LOG.error("There was an error while closing the connection", e);
                }
            }
        }
        // The cache are going to be cleared when the transaction finishes.
        for (final CacheRegion aRegion : this.cacheController.getRegions()) {
            this.cacheController.clearCache(aRegion);
        }
        Registry.getCurrentTenant().getCache().clear();
        return affectedRows;
    }

        private String translateTablePrefix(final String aStatement) {
           if (LOG.isDebugEnabled()) {
                   LOG.debug("SQL Statement before the translation: <" + aStatement + ">");
           }
           Pattern tablePrefixPattern = Pattern.compile("\\{table_prefix\\}", Pattern.CASE_INSENSITIVE);
           final String returnedStatement = tablePrefixPattern.matcher(aStatement).replaceAll(
                   Registry.getCurrentTenant().getDataSource().getTablePrefix());
                if (LOG.isDebugEnabled()) {
                        LOG.debug("SQL Statement after the translation: <" + returnedStatement + ">");
                }
           return returnedStatement;
        }

        private Connection getConnection() throws SQLException {
        return Registry.getCurrentTenant().getDataSource().getConnection();
    }
}
