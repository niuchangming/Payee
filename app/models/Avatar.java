package models;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.Play;
import play.db.jpa.Blob;
import play.libs.Images;
import play.libs.MimeTypes;
import play.modules.s3blobs.S3Blob;

@Entity
@DiscriminatorValue("avatar")
public class Avatar extends Image{
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public User user;
	
	public Avatar(User user, int x, int y, int width, int height, float ratio, File file) throws IOException {
		super(file);
		this.user = user;
		this.setThumbnailByCrop(x, y, width, height, ratio);
		this.save();
		
		file.delete();
	}
	
}
