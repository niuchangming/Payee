package controllers;

import java.util.Date;

import utils.CommonUtil;
import models.Role;
import models.User;

public class Security extends Secure.Security{
	
	public static boolean authenticate(String username, String password) {
		User user = User.find("byEmailAndPasswordAndRoleAndIsDelete", username, CommonUtil.md5(password), Role.ADMIN, false).first();
		if(user != null){
			return true;
		}
        return false;
    }
	
}
