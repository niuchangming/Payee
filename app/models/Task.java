package models;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import play.db.jpa.Model;

@Entity
@Table(name="task")
public class Task extends Model{
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
	public Company company;
	
	@Column(nullable = false)
	public String title;
	
	@Lob
	public String description;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_datetime")
	private Date createDateTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="approved_datetime")
	public Date approvedDateTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="published_datetime")
	public Date pubishDateTime;
	
	@Column(name="is_published")
	public boolean isPubished;
	
	@Temporal(TemporalType.DATE)
	@Column(name="start_date")
	public Date startDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="end_date")
	public Date endDate;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "tag_task", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	public Set<Tag> tags;
	
	@OneToMany(mappedBy = "task")
	public Set<TaskImage> images;
	
	@OneToMany(mappedBy = "task")
	public Set<Product> products;
	
	@OneToMany(mappedBy = "task")
	public Set<Reward> rewards;
	
	@OneToMany(mappedBy = "task")
	public Set<Job> jobs;
	
	@Column(name="is_delete")
	public boolean isDelete;

	public Task(Company company, String title) {
		this.title = title;
		this.company = company;
		this.createDateTime = new Date();
		this.isDelete = false;
		this.isPubished = false;
		this.rewards = new HashSet<Reward>();
		this.products = new HashSet<Product>();
		this.images = new HashSet<TaskImage>();
		this.tags = new HashSet<Tag>();
		this.save();
		
		this.rewards.add(new Reward(this));
		this.products.add(new Product(this));
	}

	public void updateByTask(Task task){
		this.title = task.title;
		this.description = task.description;
		this.startDate = task.startDate;
		this.endDate = task.endDate;
		this.addParentTag(task.tags.iterator().next());
		this.save();
	}
	
	public void addParentTag(Tag tag){
		Iterator<Tag> iterator = this.tags.iterator();
		while(iterator.hasNext()){
			Tag t = iterator.next();
			if(t.parentTag == null){
				this.tags.remove(t);
				break;
			}
		}
		
		this.tags.add(tag);
	}
	
	public int deleteImage(long imageId){
		Image delImage = Image.findById(imageId);
		if(delImage == null)
			throw new RuntimeException("Cannot find image by this ID: " + imageId);
		
		this.images.remove(delImage);
		this.save();
		
		return delImage.doDelete();
	}
	
	public void doDelete(){
		this.isDelete = true;
		
		Iterator<Job> iterator = jobs.iterator();
		while(iterator.hasNext()){
			Job job = iterator.next();
			job.isDelete = true;
			job.save();
		}
		
		this.save();
	}
	
	@Override
	public String toString() {
		return this.title;
	}
	
}











