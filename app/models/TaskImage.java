package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

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
import play.libs.MimeTypes;
import play.modules.s3blobs.S3Blob;

@Entity
@DiscriminatorValue("task")
public class TaskImage extends Image{
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id")
	public Task task;
	
	@Lob
	public String caption;
	
	public TaskImage(Task task, File file) throws FileNotFoundException {
		super(file);
		this.task = task;
		this.caption = "";
		this.setThumbnail(file);
		this.save();
		
		file.delete();
	}
	
	public void updateByCaption(String caption){
		this.caption = caption;
		this.save();
	}

	public void setThumbnail(File file) throws FileNotFoundException{
		File thumbnailFile = new File(UUID.randomUUID().toString());
		Images.resize(file, thumbnailFile, 300, -1);
		this.thumbnail = new S3Blob();
		this.thumbnail.set(new FileInputStream(thumbnailFile), MimeTypes.getContentType(file.getName()));
	}
	
}
