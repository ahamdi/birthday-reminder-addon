package org.exoplatform.addons.birthdaysreminder.Listener;

import org.exoplatform.addons.birthdaysreminder.services.BirthdaysReminderService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileEventListener;

/**
 * Created by IntelliJ IDEA.
 * User: ali
 * Date: 25/05/15
 * Time: 18:27
 */
public class NewUserProfileListener extends UserProfileEventListener {

  private BirthdaysReminderService birthdaysReminderService;

  public NewUserProfileListener(BirthdaysReminderService birthdaysReminderService){
    this.birthdaysReminderService = birthdaysReminderService;
  }

  @Override
  public void postSave(UserProfile user, boolean isNew) throws Exception {
    String birthday = user.getAttribute(UserProfile.PERSONAL_INFO_KEYS[3]);
    if(birthday!=null && !birthday.isEmpty()){
       birthdaysReminderService.addBirthDay(user);
    }
  }
}
