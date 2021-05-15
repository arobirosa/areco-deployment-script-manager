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

import java.sql.SQLException;

/**
 * It runs raw SQL code.
 *
 * @author arobirosa
 *
 */
public interface SqlScriptService {
    /**
     * Executes a SQL statement containing an UPDATE or DELETE and returns the number of affected rows.
     *
     * The FlexibleSearchFacade from the HAC doesn't something similar but I can't include the class in the classpath.
     *
     * @param aStatement
     *            Required
     * @return Number of affected rows.
     * @throws SQLException
     */
    int runDeleteOrUpdateStatement(String aStatement) throws SQLException;
}
