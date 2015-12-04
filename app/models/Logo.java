package models;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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
@DiscriminatorValue("logo")
public class Logo extends Image{
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	public Company company;
	
	public Logo(Company company, int x, int y, int width, int height, float ratio, Blob blob) {
		super(blob);
		this.company = company;
		this.setThumbnailByCrop(x, y, width, height, ratio, blob);
		this.save();
	}
	
	public void setThumbnailByCrop(int x, int y, int width, int height, float ratio, Blob blob){
		File thumbnailFile = getFile();
		BufferedImage img;
		try {
			img = ImageIO.read(image.get());
			BufferedImage cropped;
			if(x == 0 && y == 0 && width == 0 && height == 0 && ratio == 0){
				cropped = img;
			}else{
				cropped = img.getSubimage(Math.round(x / ratio), Math.round(y / ratio), Math.round(width / ratio), Math.round(height / ratio));
			}
			BufferedImage resized = cropped;
			if (width != 100) {
				resized = getScaledInstance(cropped, 100, 100, RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
			}
			ImageIO.write(resized, "jpg", thumbnailFile);
			thumbnailPath = Play.configuration.getProperty("application.baseUrl") + "data/thumbnails/" + this.image.getUUID();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
