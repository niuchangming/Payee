package models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import utils.AES;
import utils.CommonUtil;

@Entity
@DiscriminatorValue("normal")
public class NormalUser extends User{
	
	public String email;
	public String password;
	
	public NormalUser(String email, String password) {
		super();
		this.email = email;
		this.password = CommonUtil.md5(password);
		this.accessToken = this.genernateAcessToken(this.email);
		this.save();
	}

	public void updateByPassword(String password){
		this.password = CommonUtil.md5(password);
		this.save();
	}
	
}
