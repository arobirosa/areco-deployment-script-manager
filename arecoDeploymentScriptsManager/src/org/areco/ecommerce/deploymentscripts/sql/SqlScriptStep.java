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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptExecutionException;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep;
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
public class SqlScriptStep implements DeploymentScriptStep
{
	private static final Logger LOG = Logger.getLogger(SqlScriptStep.class);

	@Autowired
	private SqlScriptService sqlScriptService;

	private File sqlScript;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep#getId()
	 */
	@Override
	public String getId()
	{
		if (this.sqlScript == null)
		{
			return null;
		}
		return this.sqlScript.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStep#run()
	 */
	@Override
	public void run() throws DeploymentScriptExecutionException
	{
		final String sqlStatement = loadSqlContent();
		executeStatement(sqlStatement);
	}

	/**
	 * @param sqlStatement
	 * @throws DeploymentScriptExecutionException
	 */
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

	private String loadSqlContent() throws DeploymentScriptExecutionException
	{
		String sqlStatement;
		try
		{
			sqlStatement = FileUtils.readFileToString(this.sqlScript, DeploymentScript.DEFAULT_FILE_ENCODING);
		}
		catch (final IOException e)
		{
			throw new DeploymentScriptExecutionException("There was an error while reading the contents of the SQL Script "
					+ this.getId() + ':' + e.getLocalizedMessage(), e);
		}
		return sqlStatement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("SqlScriptStep [sqlScript=");
		builder.append(sqlScript);
		builder.append("]");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final SqlScriptStep other = (SqlScriptStep) obj;
		if (this.getId() == null)
		{
			if (other.getId() != null)
			{
				return false;
			}
		}
		else if (!this.getId().equals(other.getId()))
		{
			return false;
		}
		return true;
	}

	/**
	 * @return the sqlScript
	 */
	public File getSqlScript()
	{
		return sqlScript;
	}

	/**
	 * @param sqlScript
	 *           the sqlScript to set
	 */
	public void setSqlScript(final File sqlScript)
	{
		this.sqlScript = sqlScript;
	}

}
