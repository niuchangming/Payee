package models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import play.db.jpa.Model;
import utils.AES;
import utils.CommonUtil;

@Entity
@Table(name="job")
public class Job extends Model{
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public User user;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id")
	public Task task;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="taken_datetime")
	public Date takenDatetime;
	
	@OneToMany(mappedBy = "job")
	@LazyCollection(LazyCollectionOption.EXTRA)
	public Set<Access> accesses;
	
	@Column(name="access_count")
	public int accessCount;
	
	@OneToMany(mappedBy = "job")
	public Set<Deal> deals;
	
	@OneToMany(mappedBy = "job")
	public Set<Voucher> vouchers;
	
	public String token;
	
	@Column(name="is_delete")
	public boolean isDelete;

	public Job(User user, Task task) {
		this.user = user;
		this.task = task;
		this.takenDatetime = new Date();
		this.accesses = new HashSet<Access>();
		this.deals = new HashSet<Deal>();
		this.vouchers = new HashSet<Voucher>();
		this.isDelete = false;
		this.token = getJobToken().trim();
		this.accessCount = 0;
		this.save();
	}
	
	public String getJobToken(){
		String jobToken = AES.encrypt(this.user.id + "|" + this.task.id + "|" + System.currentTimeMillis(), null);
		return jobToken;
	}	 
	
	public void updateByAccess(Access access){
		this.accesses.add(access);
		this.accessCount = this.accessCount + 1;
		this.save();
	}
	
	public void createDeal(String ip, String qrcode){
		new Deal(this, ip, qrcode);
	}
	
	public void doDelete(){
		this.isDelete = true;
		this.save();
	}

}






















