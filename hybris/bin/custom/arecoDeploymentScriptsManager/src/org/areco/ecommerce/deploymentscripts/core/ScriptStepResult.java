package org.areco.ecommerce.deploymentscripts.core;

import de.hybris.platform.cronjob.model.CronJobModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
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
public class ScriptStepResult {

    private boolean successful;

    private CronJobModel cronJob;

    private Throwable exception;

    public ScriptStepResult(final boolean successful) {
        this(successful, null);
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The given parameters aren't modified")
    public ScriptStepResult(final Throwable exception) {
        this.successful = false;
        this.exception = exception;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The given parameters aren't modified")
    public ScriptStepResult(final boolean successful, final CronJobModel cronJob) {
        this.successful = successful;
        this.cronJob = cronJob;
    }

    public boolean isSuccessful() {
        return successful;
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
