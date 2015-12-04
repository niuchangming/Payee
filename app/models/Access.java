package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Index;

import play.db.jpa.Model;

@Entity
@Table(name="access")
public class Access extends Model{

	@Index(name = "ip_index")
	public String ip;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="access_datetime", nullable = false)
	public Date accessDateTime;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
	public Job job;
	
	public Access(Job job, String ip){
		this.job = job;
		this.ip = ip;
		this.accessDateTime = new Date();
		this.save();
	}
}
