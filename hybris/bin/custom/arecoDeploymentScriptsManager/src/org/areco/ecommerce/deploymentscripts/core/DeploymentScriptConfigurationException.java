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

/**
 * Represents any error related to the configuration of the scripts. It usually doesn't have to be managed.
 * 
 * @author arobirosa
 * 
 */
public class DeploymentScriptConfigurationException extends RuntimeException
{

	/*
	 * Constructor with a message and a cause.
	 */
	public DeploymentScriptConfigurationException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/*
	 * Constructor with a message.
	 */
	public DeploymentScriptConfigurationException(final String message)
	{
		super(message);
	}

	/*
	 * Constructor with a cause.
	 */
	public DeploymentScriptConfigurationException(final Throwable cause)
	{
		super(cause);
	}
}
