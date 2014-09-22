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
package org.areco.ecommerce.deploymentscripts.sql;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionException;
import org.areco.ecommerce.deploymentscripts.core.impl.AbstractSingleFileScriptStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Represents an SQL script.
 * 
 * @author arobirosa
 * 
 */
@Component
//Every time the step factory is called, it creates a new instance.
@Scope("prototype")
public class SqlScriptStep extends AbstractSingleFileScriptStep
{
	private static final Logger LOG = Logger.getLogger(SqlScriptStep.class);

	@Autowired
	private SqlScriptService sqlScriptService;

	/*
	 * { @InheritDoc }
	 */
	@Override
	public void run() throws DeploymentScriptExecutionException
	{
		final String sqlStatement = loadFileContent();
		executeStatement(sqlStatement);
	}

	private void executeStatement(final String sqlStatement) throws DeploymentScriptExecutionException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Running the SQL Statement: '" + sqlStatement + "'.");
		}
		int rows = -1;

		try
		{
			rows = this.sqlScriptService.runDeleteOrUpdateStatement(sqlStatement);
		}
		catch (final SQLException e)
		{
			throw new DeploymentScriptExecutionException("There was an error while running the SQL Script " + this.getId() + ':'
					+ e.getLocalizedMessage(), e);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("The SQL Script was executed successfully. " + rows + " rows were affected.");
		}
	}
}
