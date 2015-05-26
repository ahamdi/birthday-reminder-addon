package tn.ahamdi.birthdaysreminder.Listener;

import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileEventListener;
import tn.ahamdi.birthdaysreminder.services.BirthdaysReminderService;

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
