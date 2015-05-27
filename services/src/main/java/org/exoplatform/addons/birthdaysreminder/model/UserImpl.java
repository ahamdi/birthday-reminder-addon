package org.exoplatform.addons.birthdaysreminder.model;

import java.util.Calendar;


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

  public UserImpl(User user, Calendar birthday) {
    this.user = user;
    this.birthday = birthday;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserImpl)) return false;

    UserImpl user1 = (UserImpl) o;

    if (user != null ? !user.equals(user1.user) : user1.user != null) return false;
    return !(birthday != null ? !birthday.equals(user1.birthday) : user1.birthday != null);

  }

  @Override
  public int hashCode() {
    int result = user != null ? user.hashCode() : 0;
    result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
    return result;
  }
}
