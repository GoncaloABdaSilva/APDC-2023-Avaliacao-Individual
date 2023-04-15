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
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.webapp.util.RegisterData;

// Isto é para ser fácil criar um superuser
// Apagar antes de entregar
@Path("/registersuper")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterSuperUser {
	private static final String ACTIVE = "ACTIVE";
	private static final String SU = "SU";
	private static final String PRIVATE = "PRIVATE";
	

	private static final Logger Log = Logger.getLogger(LoginResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

	public RegisterSuperUser() {
	}

	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doResgistration(RegisterData data) {
		data.verifyOptionals();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);

		Transaction txn = datastore.newTransaction();

		try {

			Entity user = Entity.newBuilder(userKey)
					.set("username", data.username)
					.set("password", DigestUtils.sha512Hex(data.password))
					.set("fullName", data.fullName)
					.set("email", data.email)
					.set("visibility", PRIVATE)
					.set("telephone", data.telephoneNumber)
					.set("mobilePhone", data.mobilePhoneNumber)
					.set("occupation", data.occupation)
					.set("workPlace", data.workPlace)
					.set("address", data.address)
					.set("compAddress", data.compAddress)
					.set("zip", data.zip)
					.set("nif", data.nif)
					.set("role", SU)
					.set("state", ACTIVE)
					.build();
			// Para o video, ao mesmo tempo que se cria um superUser, criamos uma entidade
			// Mode
			// Neste caso vou criar com o Mode com .newKey(data.username)
			Key modeKey = datastore.newKeyFactory().setKind("Mode").newKey("Mode");
			Entity mode = Entity.newBuilder(modeKey).set("mode", "ACTIVE").build();
			
			txn.add(user, mode);

			Log.info("User registered: " + data.username);

			txn.commit();

			return Response.ok("{}").build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

}
