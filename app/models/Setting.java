package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="setting")
public class Setting extends Model{
	public boolean isDebug;

	public Setting(boolean isDebug) {
		super();
		this.isDebug = isDebug;
		this.save();
	}
	
	public void updateByDebug(boolean isDebug){
		this.isDebug = isDebug;
		this.save();
	}
}
