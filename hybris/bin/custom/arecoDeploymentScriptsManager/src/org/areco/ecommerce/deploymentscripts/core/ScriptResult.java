package org.areco.ecommerce.deploymentscripts.core;

import de.hybris.platform.cronjob.model.CronJobModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.areco.ecommerce.deploymentscripts.model.ScriptExecutionResultModel;

import java.util.Objects;

/**
 * Represents the result of the execution of a whole areco script.
 * <p>
 * Copyright 2023 Antonio Robirosa
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
public class ScriptResult {

    private final ScriptExecutionResultModel status;

    private CronJobModel cronJob;

    private Throwable exception;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The given parameters aren't modified")
    public ScriptResult(final ScriptExecutionResultModel status) {
        Objects.requireNonNull(status, "The parameter status is null");
        this.status = status;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The given parameters aren't modified")
    public ScriptResult(final ScriptExecutionResultModel status, final CronJobModel cronJob, final Throwable exception) {
        Objects.requireNonNull(status, "The parameter status is null in the constructor with status, cronjob and exception");
        this.status = status;
        this.cronJob = cronJob;
        this.exception = exception;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "The calling code don't change the given model")
    public ScriptExecutionResultModel getStatus() {
        return status;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "The calling code don't change the given model")
    public CronJobModel getCronJob() {
        return cronJob;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "The calling code don't change the given exception")
    public Throwable getException() {
        return exception;
    }
}
