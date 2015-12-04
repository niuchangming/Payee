package models;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="state")
public class State extends Model{
	
	@Column(name="state_name")
	public String stateName;
	
	@Column(name="state_code")
	public String stateCode;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
	public Country country;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "state")
	public List<City> cities;
	
	public static void createState(State state){
		if(state == null){
			throw new RuntimeException("State info required");
		}
		for(City city : state.cities){
			city.state = state;
			City.createCity(city);
		}
		state.save();
	}
}
