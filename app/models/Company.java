package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
@Table(name="company")
public class Company extends Model{
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public User user;
	
	public String name;
	
	@Lob
	public String description;
	
	@OneToMany(mappedBy="company")
	public Set<Address> addresses;
	
	@Column(name="contact_no")
	public String contactNo;
	
	public String fax;
	
	@Column(name="verify_status")
	public VerifyStatus verifyStatus;
	
	@OneToMany(mappedBy = "company")
	public Set<Logo> logos;
	
	@Column(name="front_image")
	public Blob frontIC;
	
	@Column(name="back_image")
	public Blob backIC;
	
	@Column(name="business_cert")
	public Blob businessCert;
	
	@OneToMany(mappedBy = "company")
	public Set<Task> tasks;
	
	public Company createCompanyByUser(User user){
		this.user = user;
		this.verifyStatus = VerifyStatus.UNVERIFY;
		this.addresses = new HashSet<Address>();
		this.logos = new HashSet<Logo>();
		this.tasks = new HashSet<Task>();
		return this.save();
	}
	
	public void updateByCompany(Company company){
		this.name = company.name;
		this.description = company.description;
		this.contactNo = company.contactNo;
		this.fax = company.fax;
		this.save();
	}
	
	public void updateByTask(Task task){
		this.tasks.add(task);
		this.save();
	}
	
}
