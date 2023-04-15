package pt.unl.fct.di.apdc.webapp.resources;

import java.util.ArrayList;
import java.util.List;
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
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.webapp.util.ListUserData;
import pt.unl.fct.di.apdc.webapp.util.LogoutData;

@Path("/listUser")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ListUserResource {
	private static final String INACTIVE_STATE = "INACTIVE";

	private static final String SU = "SU";
	private static final String GBO = "GBO";
	private static final String GA = "GA";	
	private static final String GS = "GS";
	private static final String USER = "USER";

	private static final String PUBLIC = "PUBLIC";
	private static final String ACTIVE = "ACTIVE";

	public Gson g = new Gson();

	private static final Logger Log = Logger.getLogger(LoginResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doListUsers(ListUserData data) {

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.username);

		Transaction txn = datastore.newTransaction();

		List<String> userList = new ArrayList();

		try {
			Entity user = txn.get(userKey);
			Entity token = txn.get(tokenKey);

			if (user == null) {
				txn.rollback();
				Log.warning("User " + data.username + " does not exists.");
				return Response.status(Status.FORBIDDEN).entity("User " + data.username + " does not exists.").build();
			}

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

			Query<Entity> query;
			QueryResults<Entity> queryResults;

			switch (user.getString("role")) {
			case USER:
				query = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(CompositeFilter.and(PropertyFilter.eq("role", USER),
								PropertyFilter.eq("visibility", PUBLIC), PropertyFilter.eq("state", ACTIVE)))
						.build();

				queryResults = datastore.run(query);

				queryResults.forEachRemaining(list -> {
					userList.add(":- username: " + list.getString("username") + " | email: " + list.getString("email")
							+ " | name: " + list.getString("fullName") + ";");
				});
				break;

			// Since there is no PropertyFiler.or, we position the code like this,
			//without breaks between cases
			case SU:
				query = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(PropertyFilter.eq("role", GS)).addOrderBy(OrderBy.asc("username"))
						.build();

				queryResults = datastore.run(query);
				doQuery(queryResults, userList);
				
				query = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(PropertyFilter.eq("role", GA)).addOrderBy(OrderBy.asc("username"))
						.build();
				
				queryResults = datastore.run(query);
				doQuery(queryResults, userList);
				
			case GS:
				query = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(PropertyFilter.eq("role", GBO)).addOrderBy(OrderBy.asc("username"))
						.build();

				queryResults = datastore.run(query);
				doQuery(queryResults, userList);

			case GBO:
				query = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(PropertyFilter.eq("role", USER)).addOrderBy(OrderBy.asc("username"))
						.build();

				queryResults = datastore.run(query);
				doQuery(queryResults, userList);
				break;
			}
			txn.commit();
			Log.info("Query successfull.");
			return Response.ok(g.toJson(userList)).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error 500. Something went wrong with your request.").build();
			}
		}

	}

	private void doQuery(QueryResults<Entity> queryResults, List<String> userList) {
		queryResults.forEachRemaining(list -> {
			userList.add(":- username: " + list.getString("username") + " | email: " + list.getString("email")
			        + " | name: " + list.getString("fullName")
					+ " | visibility: " + list.getString("visibility") + " | telephone number: " + list.getString("telephone") 
					+ " | mobile phone: " + list.getString("mobilePhone") + " | occupation: " + list.getString("occupation") 
					+ " | work place: " + list.getString("workPlace") + " | address: " + list.getString("address")
					+ " | other Address: " + list.getString("compAddress") + " | zip code: " + list.getString("zip")
					+ " | nif: " + list.getString("nif") + " | role: " + list.getString("role") + " | state: " + list.getString("state") + " ;");
		});
	}
}
