package org.exoplatform.addons.birthdaysreminder.job;

import java.util.Calendar;


import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.scheduler.BaseJob;
import org.exoplatform.services.scheduler.JobContext;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.exoplatform.addons.birthdaysreminder.services.BirthdaysReminderService;

/**
 * Created by IntelliJ IDEA.
 * User: ali
 * Date: 05/05/15
 * Time: 12:59
 */
public class BirthdaysCollectorJob extends BaseJob {
  private static final Log LOG = ExoLogger.getLogger(BirthdaysCollectorJob.class);

  @Override
  public void execute(JobContext context) throws Exception {
    super.execute(context);
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    PortalContainer portalContainer = PortalContainer.getInstance();
    BirthdaysReminderService birthdaysReminderService = (BirthdaysReminderService) portalContainer.getComponentInstanceOfType(BirthdaysReminderService.class);
    try {
      birthdaysReminderService.collectBirthdaysFor(Calendar.getInstance().getTime());
    } catch (Exception e) {
      LOG.error("An error occurred when executing the job",e);
    }
  }
}
