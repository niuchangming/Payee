package models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.JPA;
import play.db.jpa.Model;
import utils.CommonUtil;

@Entity
@Table(name="country")
public class Country extends Model{
	
	@Column(name="country_name")
	public String countryName;
	
	@Column(name="country_code")
	public String countryCode;
	
	@Column(name="dial_no")
	public String dialNo;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "country")
	public List<State> states;
	
	public static void createCrountry(Country country){
		if(country == null){
			throw new RuntimeException("User info required");
		}
		for(State state : country.states){
			state.country = country;
			State.createState(state);
		}
		country.save();
	}
	
	public static void createCountriesByJson(String json){
		if(CommonUtil.isBlank(json)){
			throw new RuntimeException("Country data is required.");
		}
		
		LinkedHashMap<String, Class> clzMap = new LinkedHashMap<String, Class>();
		clzMap.put("values", Country.class);
		clzMap.put("values.states", ArrayList.class);
		clzMap.put("values.states.values", State.class);
		clzMap.put("values.states.values.cities", ArrayList.class);
		clzMap.put("values.states.values.cities.values", City.class);
		
		List<Country> countries = CommonUtil.parseArray(json, clzMap);
		
		for(Country country : countries){
			Country.createCrountry(country);
		}
	}
	
}







