package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="address")
public class Address extends Model{
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id")
	public Profile profile;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	public Company company;
	
	public String street;
	
	public int block;
	
	public String unit;
	
	@Column(name="post_code")
	public int postCode;
	
	public Address createAddressByProfile(Profile profile){
		this.profile = profile;
		return this.save();
	}
	
	public Address createAddressByCompany(Company company){
		this.company = company;
		return this.save();
	}
	
	public Address updateByAddress(Address address){
		this.street = address.street;
		this.block = address.block;
		this.unit = address.unit;
		this.postCode = address.postCode;
		return this.save();
	}
	
}
