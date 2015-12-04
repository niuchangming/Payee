package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.Model;

@Entity
@Table(name="product")
public class Product extends Model{
	@Column(name="product_name")
	public String productName;
	
	@Lob
	public String description;
	
	public double price;
	
	@OneToMany(mappedBy = "product")
	public Set<Coupon> coupons;
	
	@Column(name="merchant_link")
	public String merchantLink;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id")
	public Task task;
	
	public Product(Task task) {
		this.task = task;
		this.coupons = new HashSet<Coupon>();
		this.save();
	}

	public void updateByProduct(Product product){
		this.productName = product.productName;
		this.description = product.description;
		this.merchantLink = product.merchantLink;
		this.price = product.price;
		
		if(product.coupons == null){
			this.save();
			return;
		}
		
		if(!this.coupons.iterator().hasNext()){
			product.coupons.iterator().next().updateByProduct(this);
		}else{
			this.coupons.iterator().next().updateByCoupon(product.coupons.iterator().next());
			this.save();
		}
	}
	
}

























