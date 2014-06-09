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

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.sql.SqlScriptService;
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
public class JaloSqlScriptService implements SqlScriptService
{

	private static final Logger LOG = Logger.getLogger(JaloSqlScriptService.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.areco.ecommerce.deploymentscripts.sql.SqlScriptService#runDeleteOrUpdateStatement(java.lang.String)
	 */
	@Override
	public int runDeleteOrUpdateStatement(final String aStatement) throws SQLException
	{
		int affectedRows = -1;
		Connection con = null;
		try
		{
			con = getConnection();
			affectedRows = con.prepareStatement(aStatement).executeUpdate();
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (final SQLException e)
				{
					LOG.error("There was an error while closing the connection", e);
				}
			}
		}
		Registry.getCurrentTenant().getCache().clear();
		return affectedRows;
	}

	private Connection getConnection() throws SQLException
	{
		return Registry.getCurrentTenant().getDataSource().getConnection();
	}
}
