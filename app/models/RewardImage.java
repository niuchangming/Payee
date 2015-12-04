package models;

import java.io.File;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import play.Play;
import play.db.jpa.Blob;
import play.libs.Images;

@Entity
@DiscriminatorValue("reward")
public class RewardImage extends Image{
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "reward_id")
	public Reward reward;
	
	public RewardImage(Reward reward, Blob blob) {
		super(blob);
		this.reward = reward;
		this.setThumbnail(blob);
		this.save();
	}

	public void setThumbnail(Blob blob){
		File thumbnailFile = getFile();
		Images.resize(blob.getFile(), thumbnailFile, 300, -1);
		thumbnailPath = Play.configuration.getProperty("application.baseUrl") + "data/thumbnails/" + this.image.getUUID();
	}
}
