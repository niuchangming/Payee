package models;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import play.db.jpa.Model;

@Entity
@Table(name="reward")
public class Reward extends Model{
	
	public String title;
	
	@Column(name="reward_type")
	public RewardType rewardType;
	
	@Column(name="min_shares")
	public int minShares;
	
	public double value;
	
	@OneToMany(mappedBy = "reward")
	public Set<RewardImage> images;
	
	@Lob
	public String instruction;
	
	@Temporal(TemporalType.DATE)
	@Column(name="expire_date")
	public Date expireDate;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id")
	public Task task;
	
	@OneToMany(mappedBy = "reward")
	@LazyCollection(LazyCollectionOption.EXTRA)
	public Set<Voucher> vouchers;
	
	public Reward(Task task) {
		this.task = task;
		vouchers = new HashSet<Voucher>();
		this.save();
	}

	public void updateByReward(Reward reward){
		this.title = reward.title;
		this.rewardType = reward.rewardType;
		this.instruction = reward.instruction;
		this.minShares = reward.minShares;
		this.value = reward.value;
		this.expireDate = reward.expireDate;
		this.save();
	}
	
}

