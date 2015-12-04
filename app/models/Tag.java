package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.Model;
import utils.CommonUtil;

@Entity
@Table(name="tag")
public class Tag extends Model{
	
	@Column(unique= true, nullable = false)
	public String name;
	
	@OneToMany(mappedBy="parentTag", fetch = FetchType.LAZY)
    public List<Tag> childrenTags;
	
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="parent_tag_id")
    public Tag parentTag;
    
	@ManyToMany(mappedBy = "tags")
	public Set<Task> tasks;
	
	public static void createTagsByJson(String json){
		if(CommonUtil.isBlank(json)){
			throw new RuntimeException("Tag data is required.");
		}
		
		LinkedHashMap<String, Class> clzMap = new LinkedHashMap<String, Class>();
		clzMap.put("values", Tag.class);
		clzMap.put("values.childrenTags", ArrayList.class);
		clzMap.put("values.childrenTags.values", Tag.class);
		clzMap.put("values.parentTag", Tag.class);
		
		List<Tag> tags = CommonUtil.parseArray(json, clzMap);
		for(int i = 0; i < tags.size(); i++){
			tags.get(i).toSave();
		}
	}
	
	public void toSave(){
		if(this.childrenTags != null){
			for(int i = 0; i < this.childrenTags.size(); i++){
				this.childrenTags.get(i).parentTag = this;
				this.childrenTags.get(i).toSave();
			}
		}
		this.save();
	}
	
}










































































