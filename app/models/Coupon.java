package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.Model;

@Entity
@Table(name="coupon")
public class Coupon extends Model{
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	public Product product;
	
	@Lob
	public String instruction;
	
	public double value;
	
	@Temporal(TemporalType.DATE)
	@Column(name="expire_date")
	public Date expireDate;
	
	public void updateByProduct(Product product){
		this.product = product;
		this.save();
	}
	
	public void updateByCoupon(Coupon coupon){
		this.value = coupon.value;
		this.instruction = coupon.instruction;
		this.expireDate = coupon.expireDate;
		this.save();
	}
}
