package org.areco.ecommerce.deploymentscripts.model;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.model.LogFileModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Copyright 2023 Antonio Robirosa
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">...</a>
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ScriptExecutionFirstFailedCronjobLogFileAttributeHandler implements DynamicAttributeHandler<LogFileModel, ScriptExecutionModel> {
    @Override
    public LogFileModel get(final ScriptExecutionModel model) {
        if (nonNull(model) && nonNull(model.getFirstFailedCronjob()) && CollectionUtils.isNotEmpty(model.getFirstFailedCronjob().getLogFiles())) {
            final List<LogFileModel> logFiles = new ArrayList<>(model.getFirstFailedCronjob().getLogFiles());
            logFiles.sort(Comparator.comparing(ItemModel::getModifiedtime));
            return logFiles.get(logFiles.size() - 1);
        }
        return null;
    }

    @Override
    public void set(final ScriptExecutionModel model, final LogFileModel logFileModel) {
        throw new UnsupportedOperationException("The log files of the cronjob cannot be modified");
    }
}
