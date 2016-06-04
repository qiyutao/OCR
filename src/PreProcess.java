import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;


public class PreProcess {
	private File file = null;
	private BufferedImage  binaryImg = null;
	private int hImg;
	private int wImg;
	private int[][] gray = null;
	private int threshold;
	
	public PreProcess(File f) {
		file = f;
		hImg = 0;
		wImg = 0;
		threshold = 127;
		getImage();
	}
	
	public void getImage() {
		try {
			BufferedImage buffImage = ImageIO.read(file);
			hImg = buffImage.getHeight();
			wImg = buffImage.getWidth();
			gray = new int[wImg][hImg];
			gray(buffImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void gray(BufferedImage buffImage) {
		
		for(int x = 0;x<wImg;x++) {
			for(int y = 0;y<hImg;y++) {
				int rgb = buffImage.getRGB(x, y);
				int r = (rgb>>16)&0xff;
				int g = (rgb>>8)&0xff;
				int b = (rgb>>0)&0xff;
				
				int grayPixel = (int)((b*29+g*150+r*77)>>8);
				gray[x][y] = grayPixel;  
			
			}
		}
		//
		binVal();
	}
	
	public void binVal() {
		binaryImg = new BufferedImage(wImg, hImg, BufferedImage.TYPE_BYTE_BINARY);
		
		for(int x = 0;x<wImg;x++) {
			for(int y=0;y<hImg;y++) {
				if(gray[x][y]>=threshold) {
					gray[x][y] |= 0x00ffff;
					//System.out.println(gray[x][y]);
				} else {
					gray[x][y] &= 0xff0000;
				}
				binaryImg.setRGB(x, y, gray[x][y]);
			}
		}
		creatGrayImg();
	}
	
	public void creatGrayImg() {
		try {
			ImageIO.write(binaryImg, "jpg", new File("d:\\2.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BufferedImage getBinaryImg() {
		return binaryImg;
	}
	
	public static void main(String[] args) {
		PreProcess bin = new PreProcess(new File("d:\\1.jpg"));
		BufferedImage buffImage = bin.getBinaryImg();
		Recognize r = new Recognize(buffImage);
		r.division();
		//r.match();
 	}
}
