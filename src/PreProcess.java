
/*
 * class PerProcess
 * 对文件进行灰度化和二值化处理
 * 
 * @version 0.7
 * @date 06-12-2016
 */

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class PreProcess {
	private File file = null;// 待处理文件
	private BufferedImage binaryImg = null;// 获取图像相关属性

	private int hImg;// 图像高度
	private int wImg;// 图像宽

	private int[][] gray = null;// 图像矩阵

	private int threshold;// 二值化阈值

	/*
	 * 构造方法 进行相关属性初始化
	 */
	public PreProcess(File f) {
		file = f;
		hImg = 0;
		wImg = 0;
		threshold = 150;
		getImage();
	}

	/*
	 * 把Image对象转换为可处理的BufferedImage对象
	 */
	public void getImage() {
		try {
			BufferedImage buffImage = ImageIO.read(file);

			// 图像宽高获取
			hImg = buffImage.getHeight();
			wImg = buffImage.getWidth();

			gray = new int[wImg][hImg];
			gray(buffImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * 对图像进行灰度化
	 */
	public void gray(BufferedImage buffImage) {

		for (int x = 0; x < wImg; x++) {
			for (int y = 0; y < hImg; y++) {
				/*
				 * 获取图像每个像素RGB值 并进行分离
				 */
				int rgb = buffImage.getRGB(x, y);
				// 位移操作进行三色分离
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = (rgb >> 0) & 0xff;

				// 用灰度化公式进行灰度化
				int grayPixel = (int) ((b * 29 + g * 150 + r * 77) >> 8);
				gray[x][y] = grayPixel;

			}
		}
		binVal();
	}

	/*
	 * 对图像进行二值化 本方法使用固定阈值法
	 */
	public void binVal() {
		// 保存二值化后的图像
		binaryImg = new BufferedImage(wImg, hImg, BufferedImage.TYPE_BYTE_BINARY);

		for (int x = 0; x < wImg; x++) {
			for (int y = 0; y < hImg; y++) {
				// 大于阈值则二值化为黑色
				if (gray[x][y] >= threshold) {
					gray[x][y] |= 0x00ffff;
				} else {// 大于阈值则二值化为白色
					gray[x][y] &= 0xff0000;
				}

				// 将二值化后的像素填充到图像
				binaryImg.setRGB(x, y, gray[x][y]);
			}
		}

		creatGrayImg();
	}

	/*
	 * 保存二值化后的图像
	 */
	public void creatGrayImg() {
		try {
			File file = new File(System.getProperty("user.dir") + "\\gray.jpg");
			ImageIO.write(binaryImg, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * get方法
	 * 
	 * @return binaryImg属性
	 */
	public BufferedImage getBinaryImg() {
		return binaryImg;
	}

	/*
	 * 获取识别后的字符串
	 * 
	 * @return 识别后的字符串
	 */
	public String getResult() {

		BufferedImage buffImage = getBinaryImg();

		Recognize r = new Recognize(buffImage);
		r.division();
		String result = r.getString();

		return result;
	}

}
