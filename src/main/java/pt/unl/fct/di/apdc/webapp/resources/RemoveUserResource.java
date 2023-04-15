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

import pt.unl.fct.di.apdc.webapp.util.LogoutData;
import pt.unl.fct.di.apdc.webapp.util.RemoveUserData;

@Path("/removeUser")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoveUserResource {

	private static final String INACTIVE_STATE = "INACTIVE";

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private static final Logger Log = Logger.getLogger(LogoutResource.class.getName());

	public RemoveUserResource() {
	}

	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRemoveUser(RemoveUserData data) {
		Log.info("User " + data.removerUsername + " is trying to remove " + data.toBeRemovedUsername + "'s account.");

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.removerUsername);
		Key userToRemoveKey = datastore.newKeyFactory().setKind("User").newKey(data.toBeRemovedUsername);
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.removerUsername);
		Key toRemoveTokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.toBeRemovedUsername);

		Transaction txn = datastore.newTransaction();

		try {

			Entity user = txn.get(userKey);

			if (user == null) {
				txn.rollback();
				Log.warning("User " + data.removerUsername + " does not exists.");
				return Response.status(Status.BAD_REQUEST).entity("User " + data.removerUsername + " does not exists.")
						.build();
			}

			Entity userToRemove = txn.get(userToRemoveKey);

			if (userToRemove == null) {
				txn.rollback();
				Log.warning("User " + data.toBeRemovedUsername + " does not exists.");
				return Response.status(Status.BAD_REQUEST)
						.entity("User " + data.toBeRemovedUsername + " does not exists.").build();
			}

			Entity token = txn.get(tokenKey);

			if (token == null) {
				txn.rollback();
				Log.warning("User " + data.removerUsername + " is not logged in.");
				return Response.status(Status.FORBIDDEN).entity("User " + data.removerUsername + " is not logged in.")
						.build();
			}

			if (token.getLong("expirationDate") < System.currentTimeMillis()) {
				txn.rollback();
				Log.warning("Session has expired for modifier user " + data.removerUsername + ".");

				LogoutResource logout = new LogoutResource();
				LogoutData ldata = new LogoutData(data.removerUsername);
				logout.doLogout(ldata);
				return Response.status(Status.FORBIDDEN)
						.entity("Session has expired for modifier user " + data.removerUsername + ".").build();
			}

			if (user.getString("state").equals(INACTIVE_STATE)) {
				txn.rollback();
				Log.warning("Modifier user " + data.removerUsername + " is currently inactive.");
				return Response.status(Status.FORBIDDEN)
						.entity("Modifier user " + data.removerUsername + " is currently inactive.").build();
			}

			if (data.userCanRemove(user, userToRemove)) {
				LogoutResource logout = new LogoutResource();
				LogoutData ldata = new LogoutData(data.toBeRemovedUsername);
				logout.doLogout(ldata);
				
				txn.delete(userToRemoveKey, toRemoveTokenKey);
				txn.commit();
				Log.info("User " + data.removerUsername + " has successfully remove the account of user "
						+ data.toBeRemovedUsername + ".");
				return Response.ok("{}").entity("User " + data.removerUsername + " has successfully remove the account of user "
						+ data.toBeRemovedUsername + ".").build();
			} 
			else {
				txn.rollback();
				Log.warning("User " + data.removerUsername + " does not have permission to remove the account of user " + data.toBeRemovedUsername + ".");
				return Response.status(Status.FORBIDDEN).entity("User " + data.removerUsername +" does not have permission to remove the account of user "
						+ data.toBeRemovedUsername + ".").build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error 500. Something went wrong with your request.").build();
			}
		}

	}

}
