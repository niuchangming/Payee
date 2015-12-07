package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="profile")
public class Profile extends Model{
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public User user; 
	
	@Column(name="first_name")
	public String firstName;
	
	@Column(name="last_name")
	public String lastName;
	
	public String phone;
	
	@OneToMany(mappedBy="profile")
	public Set<Address> addresses;
	
	public Profile createProfileByUser(User user){
		this.user = user;
		this.addresses = new HashSet<Address>();
		this.save();
		
		if(this.addresses != null){
			for(Address address : this.addresses){
				address.createAddressByProfile(this);
			}
		}
		
		return this;
	}

	public void updateByProfile(Profile profile){
		this.firstName = profile.firstName;
		this.lastName = profile.lastName;
		this.phone = profile.phone;
		if(this.addresses.iterator().hasNext()){
			this.addresses.iterator().next().updateByAddress(profile.addresses.iterator().next());
		}
		this.save();
	}
}



























