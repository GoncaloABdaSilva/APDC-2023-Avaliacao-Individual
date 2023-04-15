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


import pt.unl.fct.di.apdc.webapp.util.RegisterData;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {
    private static final String MODE_MAINTENANCE = "MAINTENANCE";
	
    private static final Logger Log = Logger.getLogger(RegisterResource.class.getName());
    
    private static final String USER = "USER";
	private static final String INACTIVE_STATE = "INACTIVE";
    
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    
    public RegisterResource() {}
  
    @POST
    @Path("/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response doResgistration(RegisterData data) { 
    	data.verifyOptionals();
        Log.info("Register attempt by user: " + data.username);
        
        if ( !data.validRegistration() ) {
        	return Response.status(Status.BAD_REQUEST).entity("Invalid registration. Confirm parameters inserted.").build();
        }
        
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        
        Transaction txn = datastore.newTransaction();
        
        try {
        	Entity user = txn.get(userKey);
        	
        	if (user != null) {
        		txn.rollback();
        		return Response.status(Status.BAD_REQUEST).entity("User" + data.username +" already exists.").build();
        	}
        	
        	Key modeKey = datastore.newKeyFactory().setKind("Mode").newKey("Mode");
			Entity mode = txn.get(modeKey);
			
			if (mode.getString("mode").equals(MODE_MAINTENANCE)) {
				txn.rollback();
				Log.info("Regist account attempt while in maintenance mode.");
				return Response.status(Status.SERVICE_UNAVAILABLE).build();
			}
        	user = Entity.newBuilder(userKey)
        					.set("username", data.username)
        					.set("password", DigestUtils.sha512Hex(data.password))
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
        					.set("role", USER)
        					.set("state", INACTIVE_STATE)
        					.build();
        		
        		txn.add(user);
        		
        		Log.info("User " + data.username + " has been registred successfully.");
        		txn.commit();
        		return Response.ok("{}").entity("User " + data.username + " has been registred successfully.").build(); 
        } finally {
       		if ( txn.isActive() ) {
       			txn.rollback();
       			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error 500. Something went wrong with your request.").build();
       		}
       	}
    }
      

}