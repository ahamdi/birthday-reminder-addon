package tn.ahamdi.birthdaysreminder.services;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.social.core.identity.IdentityProvider;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import tn.ahamdi.birthdaysreminder.model.UserImpl;
import tn.ahamdi.util.DateUtils;

/**
 * Created by IntelliJ IDEA.
 * User: ali
 * Date: 05/05/15
 * Time: 12:40
 */
public class BirthdaysReminderService {

  private static final String IN_DAYS = "PeriodInDays";
  private static final String BATCH_LOAD = "BatchLoad";
  private Log LOG = ExoLogger.getExoLogger(BirthdaysReminderService.class);
  private OrganizationService organizationService;
  private ListenerService listenerService;
  protected final ExoCache<String, List<UserImpl>> birthdaysMap;
  private int inDays = 7;
  private int batch = 10;
  private DateFormat df = new SimpleDateFormat("dd-MM");

  public BirthdaysReminderService(OrganizationService organizationService, ListenerService listenerService, InitParams params, CacheService cacheService) {
    this.organizationService = organizationService;
    birthdaysMap = cacheService.getCacheInstance("tn.ahamdi.birthdaysreminder.services.BirthdaysReminderCache");
    this.listenerService = listenerService;
    try {
      String days = params.getValueParam(IN_DAYS).getValue();
      String batchLoad = params.getValueParam(BATCH_LOAD).getValue();
      inDays = Integer.parseInt(days);
      batch  = Integer.parseInt(batchLoad);
      init();
    } catch (Exception e) {
      LOG.error("Error when initiating BirthdaysReminderService", e);
    }
  }

  private void init() throws Exception {
    LOG.debug("Birthdays Reminder Service : initialization");
    ListAccess<org.exoplatform.services.organization.User> users = organizationService.getUserHandler().findAllUsers();
    int progress = 0;
    while(progress <= users.getSize()) {
      org.exoplatform.services.organization.User[] userList = users.load(progress, batch);
      for (org.exoplatform.services.organization.User user : userList) {
        UserProfile profile = organizationService.getUserProfileHandler().findUserProfileByName(user.getUserName());
        StringBuilder userAttributes = new StringBuilder();
        //TODO for debugging , to remove
        for (String key : UserProfile.PERSONAL_INFO_KEYS) {
          if (profile.getAttribute(key) != null && !profile.getAttribute(key).isEmpty()) {
            userAttributes.append(key).append(" : ").append(profile.getAttribute(key)).append("\n");
          }

        }
        //get the birthday
        String birthday = profile.getAttribute(UserProfile.PERSONAL_INFO_KEYS[3]);
        if (birthday != null && !birthday.isEmpty()) {
          LOG.debug("User " + userAttributes.toString() + "\n birthday is " + birthday);
          Calendar birthdayCal = DateUtils.convertDate(birthday);
          if(birthdayCal != null) {
            String key = df.format(birthdayCal.getTime());
            List<UserImpl> usersList = birthdaysMap.get(key);
            if (usersList == null)  {
              usersList = new ArrayList<UserImpl>();
            }
            UserImpl wrapper = new UserImpl(user,birthdayCal);
            usersList.add(wrapper);
            birthdaysMap.put(key,usersList) ;
          }
        }
      }
      progress += batch;
    }
  }

  public List<UserImpl> getUserBirthdays(Date date, int days) throws Exception {
    List<UserImpl> users = new ArrayList<UserImpl>();
    DateFormat format = new SimpleDateFormat("dd-MM-YYYY");
    Calendar dayStart = DateUtils.getStartOfDay(date);
    Calendar dayEnd = DateUtils.getEndOfDay(date);
    dayEnd.set(Calendar.DAY_OF_YEAR,dayEnd.get(Calendar.DAY_OF_YEAR)+days);
    Calendar step = (Calendar) dayStart.clone();
    List<UserImpl> usernames = new ArrayList<UserImpl>();
    while(step.before(dayEnd)){
      String key = df.format(step.getTime());
      if(birthdaysMap.get(key) != null) {
        usernames.addAll(birthdaysMap.get(key));
      }
      step.set(Calendar.DAY_OF_YEAR,step.get(Calendar.DAY_OF_YEAR)+1);
    } /*
    for (String item : birthdaysMap.values()) {
      Calendar birthdayCal = Calendar.getInstance();
      birthdayCal.setTime(item.getBirthday());
      birthdayCal.set(Calendar.YEAR, dayEnd.get(Calendar.YEAR));
      if (dayStart.compareTo(birthdayCal) <= 0 && dayEnd.compareTo(birthdayCal) >= 0) {
        users.add(item);
        LOG.info("User " + item.getUser().getDisplayName() + " Birthday : " + format.format(item.getBirthday()));
      }
    }    */
    return usernames;
  }

  public void fireBirthdayEvents(List<UserImpl> users) {
    try {
      for (UserImpl item : users) {
        listenerService.broadcast("tn.ahamdi.birthdayreminder.celebrate", this, item.getUser().getDisplayName());
      }
    } catch (Exception e) {
      LOG.error("An error occurred when broadcasting Birthday celebration", e);
    }
  }

  public void collectBirthdaysFor(Date time) throws Exception {
    List<UserImpl> users = getUserBirthdays(time, 0);
    fireBirthdayEvents(users);
  }
}
