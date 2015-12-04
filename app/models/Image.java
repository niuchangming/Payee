package models;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.Play;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.Images;
import utils.CommonUtil;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="image_type", discriminatorType = DiscriminatorType.STRING)
@Table(name="image")
public class Image extends Model{
	public Blob image;
	
	@Column(name="thumbnail_path")
	public String thumbnailPath;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="uploaded_datetime", nullable = false)
	public Date uploadedDateTime;

	public Image(Blob blob) {
		this.image = blob;
		this.uploadedDateTime = new Date();
	}
	
	public int doDelete(){
		File thumbnail = new File(thumbnailPath);
		if(thumbnail.exists()){
			thumbnail.delete();
		}
		
		File originalImage = image.getFile();
		if(originalImage.exists()){
			originalImage.delete();
		}
		return Image.delete("id = ?", this.id);
	}
	
	public File getFile() {
        return new File(getStore(), this.image.getUUID());
    }
	
	public File getStore() {
        String name = Play.configuration.getProperty("thumbnails.path", "thumbnails");
        File store = null;
        if(new File(name).isAbsolute()) {
            store = new File(name);
        } else {
            store = Play.getFile(name);
        }
        if(!store.exists()) {
            store.mkdirs();
        }
        return store;
    }
	
}



























