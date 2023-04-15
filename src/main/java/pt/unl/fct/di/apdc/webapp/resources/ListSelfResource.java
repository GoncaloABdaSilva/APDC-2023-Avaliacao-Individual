package pt.unl.fct.di.apdc.webapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.webapp.util.ListSelfData;
import pt.unl.fct.di.apdc.webapp.util.LogoutData;

@Path("/listSelf")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ListSelfResource {

	private static final Logger Log = Logger.getLogger(LoginResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public Gson g = new Gson();

	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doListSelf(ListSelfData data) {

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.username);
		
		Transaction txn = datastore.newTransaction();
		
		try {

		Entity user = txn.get(userKey);

		if (user == null) {
			txn.rollback();
			Log.warning("User " + data.username + " does not exists.");
			return Response.status(Status.FORBIDDEN).entity("User " + data.username + " does not exists.").build();
		}

		Entity token = txn.get(tokenKey);

		if (token == null) {
			txn.rollback();
			Log.warning("User " + data.username + " is not logged in.");
			return Response.status(Status.FORBIDDEN).entity("User " + data.username + " is not logged in.").build();
		}

		if (token.getLong("expirationDate") < System.currentTimeMillis()) {
			txn.rollback();
			Log.warning("Session has expired for modifier user " + data.username + ".");

			LogoutResource logout = new LogoutResource();
			LogoutData ldata = new LogoutData(data.username);
			logout.doLogout(ldata);
			return Response.status(Status.FORBIDDEN)
					.entity("Session has expired for modifier user " + data.username + ".").build();
		}
		

		data.fullName = user.getString("fullName");
		data.email = user.getString("email");
		data.profileVisibility = user.getString("visibility");
		data.telephoneNumber = user.getString("telephone");
		data.mobilePhoneNumber = user.getString("mobilePhone");
		data.occupation = user.getString("occupation");
		data.workPlace = user.getString("workPlace");
		data.address = user.getString("address");
		data.compAddress = user.getString("compAddress");
		data.zip = user.getString("zip");
		data.nif = user.getString("nif");
		data.state = user.getString("state");
		data.role = user.getString("role");

		txn.commit();
		Log.info("User " + data.username +" was given his profile data.");

		return Response.ok(g.toJson(data)).build();

	} finally {
		if ( txn.isActive() ) {
   			txn.rollback();
   			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error 500. Something went wrong with your request.").build();
   		}
		
	}
		
	}
}
