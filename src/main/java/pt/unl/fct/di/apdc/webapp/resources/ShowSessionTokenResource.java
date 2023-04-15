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
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.webapp.util.AuthToken;
import pt.unl.fct.di.apdc.webapp.util.LogoutData;
import pt.unl.fct.di.apdc.webapp.util.ShowSessionTokenData;


@Path("/showSessionToken")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ShowSessionTokenResource {
		
		public Gson g = new Gson();

		private static final Logger Log = Logger.getLogger(LoginResource.class.getName());

		private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		
		KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
		
		public ShowSessionTokenResource() {	}

		@POST
		@Path("/v1")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response doShowSessionToke (ShowSessionTokenData data) {
			
			Key userKey = userKeyFactory.newKey(data.username);
			
			Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.username);

			Transaction txn = datastore.newTransaction();
			
			try{
				Entity user = txn.get(userKey);

				if (user == null) {
					txn.rollback();
					Log.warning("Username " + data.username + " does not exists.");
					return Response.status(Status.FORBIDDEN).entity("Username " + data.username + " does not exists.").build();
				}
				
				Entity token = txn.get(tokenKey);
				
				if (token == null) {
					txn.rollback();

					Log.warning("Modifier user  " + data.username + " is not logged in.");
					return Response.status(Status.FORBIDDEN).entity("Modifier user  " + data.username + " is not logged in.").build();
				}
				
				if (token.getLong("expirationDate")  < System.currentTimeMillis() ) {
					txn.rollback();
					Log.warning("Session has expired for modifier user " + data.username + ".");
					
					LogoutResource logout = new LogoutResource();
					LogoutData ldata = new LogoutData(data.username);
					logout.doLogout( ldata );
					return Response.status(Status.FORBIDDEN).entity("Session has expired for modifier user " + data.username + ".").build();
				}
				
				AuthToken auToken = new AuthToken(token.getString("username"),
						token.getString("tokenId"),token.getLong("creationDate"),
						token.getLong("expirationDate"), user.getString("role"));
				
				Log.info("token criado");
				
				txn.commit();
				
				Log.info("Token pedido por" + data.username + ".");
				return Response.ok(g.toJson(auToken)).build();
			} finally {
				if ( txn.isActive() ) {
	       			txn.rollback();
	       			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error 500. Something went wrong with your request.").build();
	       		}
			}
			
		}

}
