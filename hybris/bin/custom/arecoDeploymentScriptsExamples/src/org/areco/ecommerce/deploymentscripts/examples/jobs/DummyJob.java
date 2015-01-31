package org.areco.ecommerce.deploymentscripts.examples.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.JobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Empty cronjob used by the example which runs cron jobs.
 *
 * Created by arobirosa on 31.01.15.
 */
@Scope("tenant")
@Component
public class DummyJob implements JobPerformable<CronJobModel> {
        Logger LOG = Logger.getLogger(DummyJob.class);

        @Override public PerformResult perform(CronJobModel cronJobModel) {
                LOG.warn("His is a dummy job used by the example which runs cron jobs.");
                return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        }

        @Override public boolean isPerformable() {
                return true;
        }

        @Override public boolean isAbortable() {
                return false;
        }
}
