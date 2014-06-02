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
package org.areco.ecommerce.deploymentscripts.core;

import java.util.ArrayList;
import java.util.List;


/**
 * @author arobirosa
 * 
 */
public class DeploymentScript
{
	private String name;

	private String extensionName;

	private List<DeploymentScriptStep> orderedSteps;

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @return the extensionName
	 */
	public String getExtensionName()
	{
		return extensionName;
	}

	/**
	 * @param extensionName
	 *           the extensionName to set
	 */
	public void setExtensionName(final String extensionName)
	{
		this.extensionName = extensionName;
	}

	/**
	 * @return the orderedSteps
	 */
	public List<DeploymentScriptStep> getOrderedSteps()
	{
		if (orderedSteps == null)
		{
			this.orderedSteps = new ArrayList<DeploymentScriptStep>();
		}
		return orderedSteps;
	}

	/**
	 * @param orderedSteps
	 *           the orderedSteps to set
	 */
	public void setOrderedSteps(final List<DeploymentScriptStep> orderedSteps)
	{
		this.orderedSteps = orderedSteps;
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
		result = prime * result + ((extensionName == null) ? 0 : extensionName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		final DeploymentScript other = (DeploymentScript) obj;
		if (extensionName == null)
		{
			if (other.extensionName != null)
			{
				return false;
			}
		}
		else if (!extensionName.equals(other.extensionName))
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		return true;
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
		builder.append("DeploymentScript [name=");
		builder.append(name);
		builder.append(", extensionName=");
		builder.append(extensionName);
		builder.append(", orderedSteps=");
		builder.append(orderedSteps);
		builder.append("]");
		return builder.toString();
	}



}
