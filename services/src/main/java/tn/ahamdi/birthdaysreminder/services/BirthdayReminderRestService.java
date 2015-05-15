package tn.ahamdi.birthdaysreminder.services;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.portal.application.localization.LocalizationFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.json.JSONArray;
import org.json.JSONObject;
import tn.ahamdi.birthdaysreminder.model.UserImpl;

/**
 * Created by IntelliJ IDEA.
 * User: ali
 * Date: 06/05/15
 * Time: 10:47
 */
@Path("/birthdayreminder")
@Produces("application/json")
public class BirthdayReminderRestService implements ResourceContainer {

  private static final Log LOG = ExoLogger.getLogger(BirthdayReminderRestService.class);

  private static final CacheControl cacheControl;
  private static final String OPENSOCIAL_VIEWER_ID = "opensocial_viewer_id";
  private BirthdaysReminderService birthdaysReminderService;
  private IdentityManager identityManager;

  static {
    RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
    cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
  }

  public BirthdayReminderRestService(BirthdaysReminderService birthdaysReminderService,IdentityManager identityManager) {
    this.birthdaysReminderService = birthdaysReminderService;
    this.identityManager = identityManager;
  }
  @GET
  @Path("birthdays")
  public Response nextBirthdays(@Context SecurityContext sc, @Context UriInfo uriInfo, @QueryParam("inDays") String inDays) {
    try {
      String userId = getUserId(sc, uriInfo);
      if (userId == null) {
        return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
      }
      Date today = Calendar.getInstance().getTime();
      int days =0;
      try {
        days = Integer.parseInt(inDays);
      }catch (NumberFormatException nfe){
        days = 7;
      }
      DateFormat df = new SimpleDateFormat("dd-MM-YYYY");
      Locale locale = LocalizationFilter.getCurrentLocale();
      if(locale != null){
        df = DateFormat.getDateInstance(DateFormat.MEDIUM,locale);
      }
      List<UserImpl> users = birthdaysReminderService.nextBirthdays(today, days);

      JSONArray jsonArray = new JSONArray();
      for (UserImpl wrapper : users) {
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,wrapper.getUser().getUserName(),true);
        JSONObject json = new JSONObject();
        json.put("username",wrapper.getUser().getDisplayName());
        json.put("identityId",identity.getId());
        json.put("avatar",identity.getProfile().getAvatarUrl());
        json.put("profileLink",identity.getProfile().getUrl());
        json.put("birthday",df.format(wrapper.getBirthday()));
        jsonArray.put(json);
      }

      return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    } catch (Exception e) {
      LOG.error("An error occurred, don't panic!!", e);
      return Response.serverError().cacheControl(cacheControl).build();
    }
  }

  private String getUserId(SecurityContext sc, UriInfo uriInfo) {
    try {
      return sc.getUserPrincipal().getName();
    } catch (NullPointerException e) {
      return getViewerId(uriInfo);
    } catch (Exception e) {
      return null;
    }
  }

  private String getViewerId(UriInfo uriInfo) {
    URI uri = uriInfo.getRequestUri();
    String requestString = uri.getQuery();
    if (requestString == null) return null;
    String[] queryParts = requestString.split("&");
    for (String queryPart : queryParts) {
      if (queryPart.startsWith(OPENSOCIAL_VIEWER_ID)) {
        return queryPart.substring(queryPart.indexOf("=") + 1, queryPart.length());
      }
    }
    return null;
  }

}
