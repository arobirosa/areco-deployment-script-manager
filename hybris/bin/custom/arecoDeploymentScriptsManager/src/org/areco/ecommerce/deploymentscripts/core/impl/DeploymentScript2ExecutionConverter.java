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
package org.areco.ecommerce.deploymentscripts.core.impl;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.ScriptExecutionResultDao;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * It takes a deployment script and returns an unsaved ScriptExecutionModel.
 *
 * @author Antonio Robirosa <mailto:deployment.manager@areko.consulting>
 */
@Scope("tenant")
@Component("deploymentScript2ExecutionConverter")
public class DeploymentScript2ExecutionConverter implements Converter<DeploymentScript, ScriptExecutionModel> {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentScript2ExecutionConverter.class);

    @Resource
    private ModelService modelService;

    @Resource
    private ScriptExecutionResultDao scriptExecutionResultDao;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public ScriptExecutionModel convert(final DeploymentScript source) throws ConversionException {
        return this.convert(source, this.modelService.create(ScriptExecutionModel.class));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
     */
    @Override
    public ScriptExecutionModel convert(final DeploymentScript source, final ScriptExecutionModel execution) throws ConversionException {
        ServicesUtil.validateParameterNotNullStandardMessage("source", source);
        ServicesUtil.validateParameterNotNullStandardMessage("execution", execution);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating an script execution model from the deployment script {}", source);
        }
        execution.setExtensionName(source.getExtensionName());
        execution.setScriptName(source.getName());
        execution.setResult(scriptExecutionResultDao.getWillBeExecuted());
        execution.setPhase(source.getPhase());

        return execution;
    }

}
