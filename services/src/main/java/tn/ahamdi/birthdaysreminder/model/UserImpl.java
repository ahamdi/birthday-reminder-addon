package tn.ahamdi.birthdaysreminder.model;

import java.util.Date;


import org.exoplatform.services.organization.User;

/**
 * Created by IntelliJ IDEA.
 * User: ali
 * Date: 05/05/15
 * Time: 17:08
 */
public class UserImpl {
  private org.exoplatform.services.organization.User user;
  private Date birthday;

  public UserImpl(org.exoplatform.services.organization.User user, Date birthday) {
    this.user = user;
    this.birthday = birthday;
  }

  public org.exoplatform.services.organization.User getUser() {
    return user;
  }

  public void setUser(org.exoplatform.services.organization.User user) {
    this.user = user;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }
}
