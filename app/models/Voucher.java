package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.Model;

@Entity
@Table(name="voucher")
public class Voucher extends Model{
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Job job;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Reward reward;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="generate_datetime")
	public Date generateDatetime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="exchanged_datetime")
	public Date exchangedDatetime;
	
	public boolean isValid;
	
	public double value;
	
	public Voucher(Job job, double value) {
		this.job = job;
		this.generateDatetime = new Date();
		this.value = value;
		this.reward = job.task.rewards.iterator().next();
		this.isValid = true;
		this.save();
	}
	public void updateByValue(double value){
		this.value = value;
		this.save();
	}
	
	public void updateVoucherByScan(){
		this.exchangedDatetime = new Date();
		this.isValid = false;
		this.save();
	}
}



