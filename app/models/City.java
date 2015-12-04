package models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;
import utils.CommonUtil;

@Entity
@Table(name="city")
public class City extends Model{
	
	@Column(name="city_name")
	public String cityName;
	
	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="state_id")
	public State state;
	
	public static void createCity(City city){
		if(city == null){
			throw new RuntimeException("City info required");
		}
		city.save();
	}
	
}
