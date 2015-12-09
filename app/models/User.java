package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.db.jpa.Model;
import utils.AES;
import utils.CommonUtil;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name="user")
public class User extends Model{
	
	public Role role;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="register_datetime")
	public Date registerDateTime;
	
	@Column(name = "is_active")
	public boolean isActive;
	
	@Column(name="is_delete")
	public boolean isDelete;

	@Column(name="access_token", columnDefinition="TEXT")
	public String accessToken;
	
	public int point;
	
	@OneToMany(mappedBy = "user")
	public Set<Company> companys;
	
	@OneToMany(mappedBy = "user")
	public Set<Avatar> avatars;

	@OneToMany(mappedBy = "user")
	public Set<Profile> profiles;
	
	@OneToMany(mappedBy = "user")
	public Set<Task> tasks;
	
	@OneToMany(mappedBy = "user")
	public Set<Job> jobs;
	
	@ManyToOne(cascade=CascadeType.ALL)    
    @JoinColumn(name = "boss_cashier_id")
    public User boss;

    @OneToMany(mappedBy = "boss")
    public Set<User> cashiers;
	
	public User(){
		this.registerDateTime = new Date();
		this.isActive = false;
		this.role = Role.NORMAL;
		this.tasks = new HashSet<Task>();
		this.profiles = new HashSet<Profile>();
		this.avatars = new HashSet<Avatar>();
		this.companys = new HashSet<Company>();
		this.jobs = new HashSet<Job>();
		this.cashiers = new HashSet<User>();
		this.boss = this;
		this.isDelete = false;
	}
	
	public void updateByRole(Role role){
		this.role = role;
		this.save();
	}
	
	public void updateByTask(Task task){
		this.tasks.add(task);
		this.save();
	}
	
	public boolean hasJob(long taskId){
		Job job = Job.find("user_id = ? and task_id = ? and is_delete = ?", this.id, taskId, false).first();
		return job != null;
	}
	
	public String genernateAcessToken(String param){
		return AES.encrypt(
				CommonUtil.formatDateTime(this.registerDateTime)
				+ param, null).trim();
	}
	
	public void updateByPoint(int point){
		this.point = point;
		this.save();
	}
	
	public void updateByBoss(User boss){
		this.boss = boss;
		this.role = Role.CASHIER;
		this.save();
	}
	
	public void removeBoss(){
		this.boss = null;
		this.role = Role.NORMAL;
		this.save();
	}
	
	@Override
	public String toString() {
		return this.id.toString();
	}
	
}


















