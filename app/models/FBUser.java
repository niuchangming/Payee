package models;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("facebook")
public class FBUser extends User{
	@Column(name="fb_id")
	public String fbId;
	
	public FBUser(String fbId) {
		super();
		this.fbId = fbId;
		this.accessToken = this.genernateAcessToken(this.fbId);
		this.save();
	}
	
	
}
