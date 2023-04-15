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
import pt.unl.fct.di.apdc.webapp.util.ModifyAttributesData;


@Path("/modifyAttributes")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ModifyAttributesResource {
	private static final String INACTIVE_STATE = "INACTIVE";
	
	private static final String SU = "SU";
	private static final String GBO = "GBO";
	private static final String GS = "GS";
	private static final String USER = "USER";
	
	private static final Logger Log = Logger.getLogger(LoginResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON )
	public Response doModifyAttributes(ModifyAttributesData data) {
		
		Log.info("User " + data.modifierUsername + " is attempting to modify attributes of user " + data.modifiedUsername + ".");
		
		Key modifierKey = datastore.newKeyFactory().setKind("User").newKey(data.modifierUsername);
		Key modifiedKey = datastore.newKeyFactory().setKind("User").newKey(data.modifiedUsername);
		Key modifierTokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.modifierUsername);
		
		Transaction txn = datastore.newTransaction();
		
		try {
			
			Entity modifierUser = txn.get(modifierKey);
			
			if (modifierUser == null) {
				txn.rollback();
				Log.warning("User " + data.modifierUsername + " does not exists.");
				return Response.status(Status.FORBIDDEN).entity("User " + data.modifierUsername + " does not exists.").build();
			}
			
			Entity modifiedUser = txn.get(modifiedKey);
			
			if (modifiedUser == null) {
				txn.rollback();
				Log.warning("User " + data.modifiedUsername + " does not exists.");
				return Response.status(Status.FORBIDDEN).entity("User " + data.modifiedUsername + " does not exists.").build();
			}
			
			Entity modifierToken = txn.get(modifierTokenKey);
			
			if (modifierToken == null) {
				txn.rollback();
				Log.warning("Modifier user " + data.modifierUsername + " is not logged in.");
				return Response.status(Status.FORBIDDEN).entity("User " + data.modifierUsername + " is not logged in.").build();
			}
			
			if (modifierToken.getLong("expirationDate")  < System.currentTimeMillis() ) {
				txn.rollback();
				Log.warning("Session has expired for modifier user " + data.modifierUsername + ".");
				
				LogoutResource logout = new LogoutResource();
				LogoutData ldata = new LogoutData(data.modifierUsername);
				logout.doLogout( ldata );
				return Response.status(Status.FORBIDDEN).entity("Session has expired for modifier user " + data.modifierUsername + ".").build();
			}
			
			if (modifierUser.getString("state").equals(INACTIVE_STATE)) {
				txn.rollback();
				Log.warning("Modifier user " + data.modifierUsername + " is currently inactive.");
				return Response.status(Status.FORBIDDEN).entity("Modifier user " + data.modifierUsername + " is currently inactive.").build();
			}
			
			data.verifyOptionals(modifiedUser);
			
			if (userCanModify(modifierUser, modifiedUser, data)) {
				if (!data.userCanChangeRole(modifierUser, modifiedUser)) {
					txn.rollback();
					Log.warning("Modifier user " + data.modifierUsername + " does not have permission to change the role of user " + data.modifiedUsername + ".");
					return Response.status(Status.FORBIDDEN).entity("Modifier user " + data.modifierUsername + " does not have permission to change the role of user " + data.modifiedUsername + ".").build();
				}
				
				if (!data.userCanChangeState(modifierUser, modifiedUser)) {
					txn.rollback();
					Log.warning("Modifier user " + data.modifierUsername + " does not have permission to change the state of user " + data.modifiedUsername + ".");
					return Response.status(Status.FORBIDDEN).entity("Modifier user " + data.modifierUsername + " does not have permission to change the state of user " + data.modifiedUsername + ".").build();
				}
				
				Entity newModifiedUser;
				if (modifierUser.getString("role").equals(USER)) {
					newModifiedUser = Entity.newBuilder(modifiedUser)
        					.set("visibility", data.profileVisibility)
        					.set("telephone", data.telephoneNumber)
        					.set("mobilePhone", data.mobilePhoneNumber)
        					.set("occupation", data.occupation)
        					.set("workPlace", data.workPlace)
        					.set("address", data.address)
        					.set("compAddress", data.compAddress)
        					.set("zip", data.zip)
        					.set("nif", data.nif)
        					.build();
				}
				else {
					newModifiedUser = Entity.newBuilder(modifiedUser)
							.set("fullName", data.fullName)
        					.set("email", data.email)
        					.set("visibility", data.profileVisibility)
        					.set("telephone", data.telephoneNumber)
        					.set("mobilePhone", data.mobilePhoneNumber)
        					.set("occupation", data.occupation)
        					.set("workPlace", data.workPlace)
        					.set("address", data.address)
        					.set("compAddress", data.compAddress)
        					.set("zip", data.zip)
        					.set("nif", data.nif)
        					.set("role", data.role)
        					.set("state", data.state)
        					.build();
				}
				
				txn.update(newModifiedUser);
				txn.commit();
				
				Log.info("User " + data.modifierUsername + " has successfully changed attributes of user " + data.modifiedUsername + ".");
				return Response.ok("{}").entity("User " + data.modifierUsername + " has successfully changed attributes of user " + data.modifiedUsername + ".").build();
			}
			else {
				txn.rollback();
				Log.warning("Modifier user " + data.modifierUsername + " does not have permission to change attributes of user " + data.modifiedUsername + ".");
				return Response.status(Status.FORBIDDEN).entity("Modifier user " + data.modifierUsername + " does not have permission to change attributes of user " + data.modifiedUsername + ".").build();
			}
			
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error 500. Something went wrong with your request.").build();
			}
		}
	}
	
	private boolean userCanModify(Entity modifierUser, Entity modifiedUser, ModifyAttributesData data) {
		if (modifierUser.getString("role").equals(SU)) 
			return true;
		
		if (modifierUser.getString("role").equals(GS) && 
				(modifiedUser.getString("role").equals(GBO) || modifiedUser.getString("role").equals(USER)) )
			return true;
		
		if (modifierUser.getString("role").equals(GBO) && modifiedUser.getString("role").equals(USER) ) 
			return true;
		
		if (modifierUser.getString("role").equals(USER) && data.modifierUsername.equals(data.modifiedUsername))
			return true;
		
		if(modifierUser.getString("username").equals(modifiedUser.getString("username")))
			return true;
		
		return false;
	}		
	
}
