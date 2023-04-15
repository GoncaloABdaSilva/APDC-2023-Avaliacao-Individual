package pt.unl.fct.di.apdc.webapp.util;

import com.google.cloud.datastore.Entity;

public class RemoveUserData {

	private static final String SU = "SU";
	private static final String GBO = "GBO";
	private static final String GA = "GA";
	private static final String GS = "GS";
	private static final String USER = "USER";
	
	public String removerUsername;
	public String toBeRemovedUsername;
	
	public RemoveUserData() {}
	
	public RemoveUserData(String removerUsername, String toBeRemovedUsername) {
		this.removerUsername = removerUsername;
		this.toBeRemovedUsername = toBeRemovedUsername;
	}
	
	public boolean userCanRemove(Entity removerUser, Entity toBeRemovedUser) {
		if (removerUser.getString("role").equals(SU))
			return true;
		
		if (removerUser.getString("role").equals(GS) && (toBeRemovedUser.getString("role").equals(GA) ||
				toBeRemovedUser.getString("role").equals(GBO) || toBeRemovedUser.getString("role").equals(USER)))
			return true;
		
		if (removerUser.getString("role").equals(GA) && (toBeRemovedUser.getString("role").equals(GBO) ||
				toBeRemovedUser.getString("role").equals(USER)))
			return true;
		
		if (removerUser.getString("role").equals(GBO) && toBeRemovedUser.getString("role").equals(USER))
			return true;
		
		if (removerUser.getString("role").equals(USER) && removerUsername.equals(toBeRemovedUsername))
			return true;
		
		return false;
	}
}
