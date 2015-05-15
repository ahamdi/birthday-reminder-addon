package tn.ahamdi.birthdaysreminder.services;

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
  private RelationshipManager relashioshiManager;
  private IdentityManager identityManager;
  private ListenerService listenerService;
  protected final Map<String, UserImpl> birthdaysMap;
  private int inDays = 7;
  private int batch = 10;

  public BirthdaysReminderService(OrganizationService organizationService, ListenerService listenerService, InitParams params,RelationshipManager relashioshiManager,IdentityManager identityManager) {
    this.relashioshiManager = relashioshiManager;
    this.identityManager = identityManager;
    this.organizationService = organizationService;

    birthdaysMap = new ConcurrentHashMap<String, UserImpl>();
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
        for (String key : UserProfile.PERSONAL_INFO_KEYS) {
          if (profile.getAttribute(key) != null && !profile.getAttribute(key).isEmpty()) {
            userAttributes.append(key).append(" : ").append(profile.getAttribute(key)).append("\n");
          }

        }
        String birthday = profile.getAttribute(UserProfile.PERSONAL_INFO_KEYS[3]);
        if (birthday != null && !birthday.isEmpty()) {
          LOG.debug("User " + userAttributes.toString() + "\n birthday is " + birthday);
          Date birthdayDate = DateUtils.convertDate(birthday);
          UserImpl wrapper = new UserImpl(user, birthdayDate);
          birthdaysMap.put(user.getUserName(), wrapper);
        }
      }
      progress += batch;
    }
  }

  public List<UserImpl> myConnectionsBirthdays(String user,Date date, int days) throws Exception {
    List<UserImpl> users = new ArrayList<UserImpl>();
    DateFormat format = new SimpleDateFormat("dd-MM-YYYY");
    //start day
    Calendar dayStart = Calendar.getInstance();
    dayStart.setTime(date);
    dayStart.set(Calendar.HOUR_OF_DAY, 0);
    dayStart.set(Calendar.MINUTE, 0);
    dayStart.set(Calendar.SECOND, 0);
    dayStart.set(Calendar.MILLISECOND, 0);
    //End day
    Calendar dayEnd = Calendar.getInstance();
    dayEnd.set(Calendar.DAY_OF_YEAR, dayEnd.get(Calendar.DAY_OF_YEAR) + days);
    dayEnd.set(Calendar.HOUR, 23);
    dayEnd.set(Calendar.MINUTE, 59);
    dayEnd.set(Calendar.SECOND, 59);
    dayEnd.set(Calendar.MILLISECOND, 999);
    // Check the birthdays of my connections
    Identity userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,user,true);
    ListAccess<Identity> connections = relashioshiManager.getConnections(userIdentity);
    Identity[] userConnections = connections.load(0,connections.getSize());
    for(Identity connection : userConnections){
       connection.getProfile().getProperty("birthday");
    }


    for (UserImpl item : birthdaysMap.values()) {
      Calendar birthdayCal = Calendar.getInstance();
      birthdayCal.setTime(item.getBirthday());
      birthdayCal.set(Calendar.YEAR, dayEnd.get(Calendar.YEAR));
      if (dayStart.compareTo(birthdayCal) <= 0 && dayEnd.compareTo(birthdayCal) >= 0) {
        users.add(item);
        LOG.info("User " + item.getUser().getDisplayName() + " Birthday : " + format.format(item.getBirthday()));
      }
    }
    return users;
  }

  public List<UserImpl> nextBirthdays(Date date, int days) throws Exception {
    List<UserImpl> users = new ArrayList<UserImpl>();
    DateFormat format = new SimpleDateFormat("dd-MM-YYYY");
    //start day
    Calendar dayStart = Calendar.getInstance();
    dayStart.setTime(date);
    dayStart.set(Calendar.HOUR_OF_DAY, 0);
    dayStart.set(Calendar.MINUTE, 0);
    dayStart.set(Calendar.SECOND, 0);
    dayStart.set(Calendar.MILLISECOND, 0);
    //End day
    Calendar dayEnd = Calendar.getInstance();
    dayEnd.set(Calendar.DAY_OF_YEAR, dayEnd.get(Calendar.DAY_OF_YEAR) + days);
    dayEnd.set(Calendar.HOUR, 23);
    dayEnd.set(Calendar.MINUTE, 59);
    dayEnd.set(Calendar.SECOND, 59);
    dayEnd.set(Calendar.MILLISECOND, 999);
    for (UserImpl item : birthdaysMap.values()) {
      Calendar birthdayCal = Calendar.getInstance();
      birthdayCal.setTime(item.getBirthday());
      birthdayCal.set(Calendar.YEAR, dayEnd.get(Calendar.YEAR));
      if (dayStart.compareTo(birthdayCal) <= 0 && dayEnd.compareTo(birthdayCal) >= 0) {
        users.add(item);
        LOG.info("User " + item.getUser().getDisplayName() + " Birthday : " + format.format(item.getBirthday()));
      }
    }
    return users;
  }

  public void celebrateBirthday(Date date) {
    try {
      List<UserImpl> users = nextBirthdays(date, inDays);
      for (UserImpl item : users) {
        listenerService.broadcast("tn.ahamdi.birthdayreminder.celebrate", this, item.getUser());
      }
    } catch (Exception e) {
      LOG.error("An error occurred when broadcasting Birthday celebration", e);
    }
  }
}
