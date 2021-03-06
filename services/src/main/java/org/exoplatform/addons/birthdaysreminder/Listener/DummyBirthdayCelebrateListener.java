package org.exoplatform.addons.birthdaysreminder.Listener;

import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.addons.birthdaysreminder.services.BirthdaysReminderService;

/**
 * Created by IntelliJ IDEA.
 * User: ali
 * Date: 05/05/15
 * Time: 19:05
 */
public class DummyBirthdayCelebrateListener extends Listener<BirthdaysReminderService,User> {
  Log LOG = ExoLogger.getLogger(DummyBirthdayCelebrateListener.class);
  @Override
  public void onEvent(Event<BirthdaysReminderService, User> event) throws Exception {
     LOG.info("DumpBirthdayCelebrateListener is invoked here !!"+event.getData().getDisplayName());
  }
}
