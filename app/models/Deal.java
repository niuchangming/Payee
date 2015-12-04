package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.Model;

@Entity
@Table(name="deal")
public class Deal extends Model{	
	public String ip;
	
	public String qrcode;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Job job;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="received_datetime")
	public Date receivedDatetime;
	
	public Deal(Job job, String ip, String qrcode) {
		this.ip = ip;
		this.job = job;
		this.receivedDatetime = new Date();
		this.qrcode = qrcode;
		this.save();
	}
	
}
