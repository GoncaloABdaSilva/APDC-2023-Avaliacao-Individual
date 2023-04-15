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
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.webapp.util.LogoutData;
import pt.unl.fct.di.apdc.webapp.util.MaintenanceModeData;

@Path("/maintenance")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MaintenanceModeResource {

	private static final String INACTIVE_STATE = "INACTIVE";

	private static final String SU = "SU";
	private static final String GS = "GS";
	private static final String USER = "USER";
	
	private static final String MODE_ACTIVE = "ACTIVE";
	private static final String MODE_MAINTENANCE = "MAINTENANCE";

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private static final Logger Log = Logger.getLogger(LogoutResource.class.getName());

	public MaintenanceModeResource() {
	}

	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doMaintenance(MaintenanceModeData data) {
		Log.info("User " + data.username + " is trying to activate maintenance mode.");

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.username);

		Transaction txn = datastore.newTransaction();

		try {
			Entity user = txn.get(userKey);

			if (user == null) {
				txn.rollback();
				Log.warning("User " + data.username + " does not exists.");
				return Response.status(Status.BAD_REQUEST).entity("User " + data.username + " does not exists.")
						.build();
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

			if (user.getString("state").equals(INACTIVE_STATE)) {
				txn.rollback();
				Log.warning("Modifier user " + data.username + " is currently inactive.");
				return Response.status(Status.FORBIDDEN)
						.entity("Modifier user " + data.username + " is currently inactive.").build();
			}

			Key modeKey = datastore.newKeyFactory().setKind("Mode").newKey("Mode");
			Entity mode = txn.get(modeKey);
			Entity newMode;
			
			if (mode.getString("mode").equals(MODE_MAINTENANCE)) {
				newMode = Entity.newBuilder(mode).set("mode", MODE_ACTIVE).build();
				txn.update(newMode);
				txn.commit();
				
				Log.info("User " + data.username + " has ended maintenance mode.");
				return Response.ok("{}").entity("Maintenance mode has ended.").build();
			}
			
			if (user.getString("role").equals(SU) || user.getString("role").equals(GS)) {

				Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(PropertyFilter.eq("role", USER)).build();

				QueryResults<Entity> queryResults = datastore.run(query);

				queryResults.forEachRemaining(userLO -> {

					LogoutResource logout = new LogoutResource();
					LogoutData ldata = new LogoutData(userLO.getString("username"));
					logout.doLogout(ldata);
				});
				newMode = Entity.newBuilder(mode).set("mode", MODE_MAINTENANCE)
						.build();
				txn.update(newMode);
				txn.commit();
				
				Log.info("User " + data.username + " has started maintenance mode.");
				return Response.ok("{}").entity("Maitenance mode activated. All users were logged out.").build();

			} else {
				txn.rollback();
				Log.warning("User " + data.username + " does not have permission to start maintenance mode.");
				return Response.status(Status.FORBIDDEN)
						.entity("User " + data.username + " does not have permission to start maintenance mode.")
						.build();
			}

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error 500. Something went wrong with your request.").build();
			}
		}
	}

}
