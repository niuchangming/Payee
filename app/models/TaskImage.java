package models;

import java.io.File;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.Play;
import play.db.jpa.Blob;
import play.libs.Images;

@Entity
@DiscriminatorValue("task")
public class TaskImage extends Image{
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id")
	public Task task;
	
	@Lob
	public String caption;
	
	public TaskImage(Task task, Blob blob) {
		super(blob);
		this.task = task;
		this.caption = "";
		this.setThumbnail(blob);
		this.save();
	}
	
	public void updateByCaption(String caption){
		this.caption = caption;
		this.save();
	}

	public void setThumbnail(Blob blob){
		File thumbnailFile = getFile();
		Images.resize(blob.getFile(), thumbnailFile, 300, -1);
		thumbnailPath = Play.configuration.getProperty("application.baseUrl") + "data/thumbnails/" + this.image.getUUID();
	}

}
