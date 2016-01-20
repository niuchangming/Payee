package models;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;
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

import org.junit.ClassRule;

import play.Play;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.Images;
import play.libs.MimeTypes;
import play.modules.s3blobs.S3Blob;
import utils.CommonUtil;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="image_type", discriminatorType = DiscriminatorType.STRING)
@Table(name="image")
public class Image extends Model{
	public S3Blob image;
	
	public S3Blob thumbnail;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="uploaded_datetime", nullable = false)
	public Date uploadedDateTime;

	public Image(File file) throws FileNotFoundException {
		this.image = new S3Blob();
		this.image.set(new FileInputStream(file), MimeTypes.getContentType(file.getName()));
		this.uploadedDateTime = new Date();
	}
	
	public int doDelete(){
		image.delete();
		thumbnail.delete();
		return Image.delete("id = ?", this.id);
	}
	
	public void setThumbnailByCrop(int x, int y, int width, int height, float ratio) throws IOException{
		BufferedImage img = ImageIO.read(image.get());
		BufferedImage cropped;
		if(x == 0 && y == 0 && width == 0 && height == 0 && ratio == 0){
			cropped = img;
		}else{
			cropped = img.getSubimage(Math.round(x / ratio), Math.round(y / ratio), Math.round(width / ratio), Math.round(height / ratio));
		}
		BufferedImage resized = cropped;
		if (width != 128) {
			resized = getScaledInstance(cropped, 128, 128, RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
		}
		
		File thumbnailFile = new File(UUID.randomUUID().toString());
		ImageIO.write(resized, "jpg", thumbnailFile);
		this.thumbnail = new S3Blob();
		this.thumbnail.set(new FileInputStream(thumbnailFile), MimeTypes.getContentType(thumbnailFile.getName()));
	}
	
	public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality){
		int type = BufferedImage.TYPE_INT_RGB;
		BufferedImage ret = (BufferedImage)img;
		int w, h;
		if (higherQuality && img.getWidth() > targetWidth && img.getHeight() > targetHeight) {
			w = img.getWidth();
			h = img.getHeight();
		} else {
			w = targetWidth;
			h = targetHeight;
		}
		
		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}
		
		if (higherQuality && h > targetHeight) {
			h /= 2;
			if (h < targetHeight) {
				h = targetHeight;
			}
		}
		
		BufferedImage tmp = new BufferedImage(w, h, type);
		Graphics2D g2 = tmp.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
		g2.drawImage(ret, 0, 0, w, h, null);
		g2.dispose();
		
		ret = tmp;
		} while (w != targetWidth || h != targetHeight);
		
		return ret;
	}
}



























