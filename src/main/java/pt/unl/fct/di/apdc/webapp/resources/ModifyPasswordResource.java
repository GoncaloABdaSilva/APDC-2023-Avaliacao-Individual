package pt.unl.fct.di.apdc.webapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.webapp.util.LogoutData;
import pt.unl.fct.di.apdc.webapp.util.ModifyPasswordData;


@Path("/modifyPassword")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public class ModifyPasswordResource {
		private static final String INACTIVE_STATE = "INACTIVE";
	
		private static final Logger Log = Logger.getLogger(LoginResource.class.getName());

		private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
		@POST
		@Path("/v1")
		@Consumes(MediaType.APPLICATION_JSON )
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response doModifyPassword(ModifyPasswordData data) {
			
			Log.info("Modify password attempt by username " + data.username + ".");
			
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Key userTokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.username);
			
			Transaction txn = datastore.newTransaction();
			
			try {
				Entity user = txn.get(userKey);
				
				if (user == null) {
					txn.rollback();
					Log.warning("User " + data.username + " does not exists.");
					return Response.status(Status.FORBIDDEN).entity("User " + data.username + " does not exists.").build();
				}
				
				Entity token = txn.get(userTokenKey);
				
				if (token == null) {
					txn.rollback();
					Log.warning("Modifier user  " + data.username + " is not logged in.");
					return Response.status(Status.FORBIDDEN).entity("User  " + data.username + " is not logged in.").build();
				}
				
				if (token.getLong("expirationDate")  < System.currentTimeMillis() ) {
					txn.rollback();
					Log.warning("Session has expired for modifier user " + data.username + ".");
					
					LogoutResource logout = new LogoutResource();
					LogoutData ldata = new LogoutData(data.username);
					logout.doLogout( ldata );
					return Response.status(Status.FORBIDDEN).entity("Session has expired for modifier user " + data.username + ".").build();
				}
				
				if (user.getString("state").equals(INACTIVE_STATE)) {
					txn.rollback();
					Log.warning("Modifier user " + data.username + " is currently inactive.");
					return Response.status(Status.FORBIDDEN).entity("Modifier user " + data.username + " is currently inactive.").build();
				}
				
				String hashedOldPassword = user.getString("password");
				
				if (!data.isOldPasswordValid( hashedOldPassword )) {
					txn.rollback();
					Log.warning("Wrong password for username " + data.username + ".");
					return Response.status(Status.FORBIDDEN).entity("Wrong password for username " + data.username + ".").build();
				}
				
				if (data.newPwdEqualsOldPwd()) {
					txn.rollback();
					Log.warning("New password can't be the same as the previous one for user " + data.username + ".");
					return Response.status(Status.FORBIDDEN).entity("New password can't be the same as the previous one for user " + data.username + ".").build();
				}
				
				if (!data.isNewPasswordValid()) {
					txn.rollback();
					Log.warning("Both password and confirmation have to be equal for user " + data.username + ".");
					return Response.status(Status.FORBIDDEN).entity("Both password and confirmation have to be equal for user " + data.username + ".").build();
				}
				
				Entity newPwdUser = Entity.newBuilder(user)
						.set("password", DigestUtils.sha512Hex(data.newPassword))
						.build();
				txn.update(newPwdUser);
				txn.commit();
				
				Log.info("Password of user " + data.username + " has been chaged successfully.");
				return Response.ok("{}").entity("Password of user " + data.username + " has been chaged successfully.").build(); 
				
			}  finally {
				if ( txn.isActive() ) {
	       			txn.rollback();
	       			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error 500. Something went wrong with your request.").build();
	       		}
			}			
		}
		
}
