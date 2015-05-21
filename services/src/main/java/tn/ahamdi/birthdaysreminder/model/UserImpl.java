package tn.ahamdi.birthdaysreminder.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import org.exoplatform.services.organization.User;

/**
 * Created by IntelliJ IDEA.
 * User: ali
 * Date: 05/05/15
 * Time: 17:08
 */
public class UserImpl {
  private org.exoplatform.services.organization.User user;
  private Calendar birthday;
  private Locale locale;

  public UserImpl(User user, Calendar birthday) {
    this.user = user;
    this.birthday = birthday;
    this.locale = locale;
  }

  public org.exoplatform.services.organization.User getUser() {
    return user;
  }

  public void setUser(org.exoplatform.services.organization.User user) {
    this.user = user;
  }

  public Calendar getBirthday() {
    return birthday;
  }

  public void setBirthday(Calendar birthday) {
    this.birthday = birthday;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }
}
