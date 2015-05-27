package org.exoplatform.addons.birthdaysreminder.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import org.exoplatform.addons.birthdaysreminder.model.UserImpl;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.addons.util.DateUtils;

/**
 * Created by IntelliJ IDEA.
 * User: ali
 * Date: 05/05/15
 * Time: 12:40
 */
public class BirthdaysReminderService {

  private static final String IN_DAYS = "PeriodInDays";
  private static final String BATCH_LOAD = "BatchLoad";
  private static final String MODE = "mode";
  private static final String ALL_PEOPLE = "all";
  private static final String CONTACTS = "contacts";
  private Log LOG = ExoLogger.getExoLogger(BirthdaysReminderService.class);
  private OrganizationService organizationService;
  private ListenerService listenerService;
  protected final ExoCache<String, List<UserImpl>> birthdaysMap;
  private int inDays = 7;
  private int batch = 10;
  private String mode;
  private DateFormat df = new SimpleDateFormat("dd-MM");

  public BirthdaysReminderService(ListenerService listenerService, InitParams params, CacheService cacheService, OrganizationService organizationService,RepositoryService repositoryService) {
    this.organizationService = organizationService;
    birthdaysMap = cacheService.getCacheInstance("tn.ahamdi.birthdaysreminder.services.BirthdaysReminderCache");
    // the cache size will be the number of days in a year
    birthdaysMap.setMaxSize(366);

    this.listenerService = listenerService;
    try {
      String days = params.getValueParam(IN_DAYS).getValue();
      String batchLoad = params.getValueParam(BATCH_LOAD).getValue();
      mode = params.getValueParam(MODE).getValue();
      inDays = Integer.parseInt(days);
      batch  = Integer.parseInt(batchLoad);
      // Delay the initiation of the service to avoid creating latency looping over all user and getting their birthdays
      ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
      executor.schedule(init(),5, TimeUnit.MINUTES);
      init();
    } catch (Exception e) {
      LOG.error("Error when initiating BirthdaysReminderService", e);
    }
  }

  private Runnable init() {
    return new Runnable() {
      @Override
      public void run() {
        LOG.debug("Birthdays Reminder Service : initialization");
        ListAccess<User> users = null;
        try {
          users = organizationService.getUserHandler().findAllUsers();

          int progress = 0;
          while (progress <= users.getSize()) {
            int length = progress + batch <= users.getSize() ? batch : users.getSize() - progress;
            org.exoplatform.services.organization.User[] userList = users.load(progress, length);
            for (org.exoplatform.services.organization.User user : userList) {
              UserProfile profile = organizationService.getUserProfileHandler().findUserProfileByName(user.getUserName());
              //get the birthday
              String birthday = profile.getAttribute(UserProfile.PERSONAL_INFO_KEYS[3]);

              if (birthday != null && !birthday.isEmpty()) {
                Calendar birthdayCal = DateUtils.convertDate(birthday);
                if (birthdayCal != null) {
                  String key = df.format(birthdayCal.getTime());
                  List<UserImpl> usersList = birthdaysMap.get(key);
                  if (usersList == null) {
                    usersList = new ArrayList<UserImpl>();
                  }
                  UserImpl wrapper = new UserImpl(user, birthdayCal);
                  usersList.add(wrapper);
                  birthdaysMap.put(key, usersList);
                }
              }
            }
            progress += batch;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

      }
    };
  }

  public List<UserImpl> getUserBirthdays(Date date, int days) throws Exception {
    return getUserBirthdays(date,days,null);
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

  public void addBirthDay(UserProfile profile) throws Exception{
    User user = organizationService.getUserHandler().findUserByName(profile.getUserName());
    String birthday = profile.getAttribute(UserProfile.PERSONAL_INFO_KEYS[3]);
    if (birthday != null && !birthday.isEmpty()) {
      Calendar birthdayCal = DateUtils.convertDate(birthday);
      if(birthdayCal != null) {
        String key = df.format(birthdayCal.getTime());
        List<UserImpl> usersList = birthdaysMap.get(key);
        if (usersList == null)  {
          usersList = new ArrayList<UserImpl>();
        }
        UserImpl wrapper = new UserImpl(user,birthdayCal);
        if(!usersList.contains(wrapper)) {
          usersList.add(wrapper);
          birthdaysMap.put(key, usersList);
        }
      }
    }
  }

  public List<UserImpl> getUserBirthdays(Date date, int days, String userId) {
    Calendar dayStart = DateUtils.getStartOfDay(date);
    Calendar dayEnd = DateUtils.getEndOfDay(date);
    if(days == -1){
      days = inDays;
    }
    dayEnd.set(Calendar.DAY_OF_YEAR,dayEnd.get(Calendar.DAY_OF_YEAR)+days);
    Calendar step = (Calendar) dayStart.clone();
    List<UserImpl> userNames = new ArrayList<UserImpl>();
    while(step.before(dayEnd)){
      String key = df.format(step.getTime());
      List<UserImpl> users = birthdaysMap.get(key);
      if(users != null) {
        if(CONTACTS.equalsIgnoreCase(mode) && userId != null && !userId.isEmpty()) {
          IdentityManager identityManager = (IdentityManager) PortalContainer.getInstance()
              .getComponentInstanceOfType(IdentityManager.class);

          RelationshipManager relationshipManager =  (RelationshipManager) PortalContainer.getInstance()
              .getComponentInstanceOfType(RelationshipManager.class);
           Identity sender = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,userId,true);
          for(UserImpl user : users){
           Identity receiver = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,user.getUser().getUserName(),true);
            Relationship relationship = relationshipManager.get(sender,receiver);
            if(relationship!=null && relationship.getStatus() == Relationship.Type.CONFIRMED){
              userNames.add(user);
            }
          }
        }else{
          userNames.addAll(users);
        }
      }
      step.set(Calendar.DAY_OF_YEAR,step.get(Calendar.DAY_OF_YEAR) + 1);
    }
    return userNames;
  }
}
