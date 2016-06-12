
/*
 * class Recognize
 * 对二值化图像进行切割及识别
 * 
 * @version 0.7
 * @date 06-12-2016
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.security.auth.x500.X500Principal;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Recognize {
	private BufferedImage binaryImg = null;// 图像

	private List<Pair> point = null;// 垂直分割点
	private List<Pair> rowPoint = null;// 水平分割点

	private Set<Integer> space = null;// 保存空格位置
	private Set<Integer> enter = null;// 保存换行位置

	private List<String> fileName = null;// 保存分割后文件名

	private int num = 0;// 分割文件计数器

	private int hImg;// 图像长
	private int wImg;// 图像宽

	/*
	 * 构造方法 进行部分属性初始化
	 * 
	 * @par 要处理的图像
	 */
	public Recognize(BufferedImage img) {
		binaryImg = img;

		space = new TreeSet<>();
		enter = new TreeSet<>();

		fileName = new ArrayList<>();

		hImg = img.getHeight();
		wImg = img.getWidth();
	}

	/*
	 * 图像分割方法
	 */
	public void division() {
		Set<Integer> stRow = getRowPoint();// 保存水平分割点
		rowPoint = getCut(stRow);// 获取水平分割点

		for (Pair p : rowPoint) {// 对每一行进行分割
			Set<Integer> st = getPoint(p);
			point = getCut(st);// 获得垂直分割点
			divImg(p);
			enter.add(num);// 添加结尾换行
		}

	}

	/*
	 * 获取图像水平连贯像素为得到图像分割点做准备
	 * 
	 * @par 垂直方向分割点
	 * 
	 * @return 连续切割点集
	 */
	public Set<Integer> getPoint(Pair p) {
		Set<Integer> st = new TreeSet<>();
		for (int i = 0; i < wImg; i++) {
			for (int j = p.x; j <= p.y; j++) {
				// 对每一个像素进行扫描
				int pix = binaryImg.getRGB(i, j);
				if (pix == -16777216) {// 如果是黑色，加入连贯点
					st.add(i);
				}

			}
		}

		return st;
	}

	/*
	 * 获取图像垂直连贯像素为得到图像分割点做准备
	 * 
	 * @par 水平方向分割点
	 * 
	 * @return 连续切割点集
	 */
	public Set<Integer> getRowPoint() {
		Set<Integer> st = new TreeSet<>();
		for (int i = 0; i < wImg; i++) {
			for (int j = 0; j < hImg; j++) {
				int pix = binaryImg.getRGB(i, j);
				if (pix == -16777216) {
					st.add(j);
				}

			}
		}

		return st;
	}

	/*
	 * 获取切割点
	 * 
	 * @par 连续切割点集
	 * 
	 * @return 切点对
	 */
	public List<Pair> getCut(Set<Integer> st) {
		int x = 0;// 当前切割点
		int y = 0;// 后一个切割点
		int old = -1;// 保存点集的第一个
		List<Pair> point = new ArrayList<>();// 用于存放切割点对

		Iterator<Integer> it = st.iterator();// 迭代遍历
		while (it.hasNext()) {
			if (old < 0) { // 避开old在第一次循环
				x = it.next();
				old = x;
			}
			y = it.next();

			if (x + 1 != y) {// true说明点集不连续
				Pair p = new Pair(old, x);
				point.add(p);// 保存切割点
				old = y;// old移向下个初点

			}
			x = y;// x向前移动
		}

		// 处理循环结束后的最后一个切割点
		Pair p = new Pair(old, x);
		point.add(p);

		return point;
	}

	/*
	 * 通过切割点对进行切割
	 * 
	 * @par 切割点对
	 */
	public void divImg(Pair p) {
		int oldY = 0;// 保存y进行空格判断

		for (Pair pair : point) {
			int x = pair.x;// 起始切割点
			int y = pair.y;// 结束切割点

			if (x - oldY > 5 && oldY != 0) {// 空格判断
				space.add(num);
			}

			// 创建一个切割后的图像
			BufferedImage tmpImg = new BufferedImage(y - x + 4, p.y - p.x + 4, BufferedImage.TYPE_BYTE_BINARY);
			// 初始化图像并设为全白
			for (int i = 0; i < y - x + 4; i++)
				for (int j = 0; j < p.y - p.x + 4; j++)
					tmpImg.setRGB(i, j, -1);
			// 对图像填充像素信息
			for (int i = 0; i < y - x + 1; i++) {
				for (int j = p.x; j <= p.y; j++) {
					int rgb = binaryImg.getRGB(x + i, j);
					tmpImg.setRGB(i + 2, j - p.x + 2, rgb);

				}
			}

			try {
				// 获取当前目录
				String name = System.getProperty("user.dir") + "\\tmp\\" + num + ".jpg";
				File file = new File(name);

				if (!file.exists())// 若不存在tmp目录则创建
					file.mkdirs();

				// 把图片写入
				ImageIO.write(tmpImg, "jpg", file);
				fileName.add(name);
				// 文件计数器增加
				num++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			oldY = y;
		}
	}

	/*
	 * 得到识别后的字符串
	 * 
	 * @return 识别后的字符串
	 */
	public String getString() {
		StringBuilder str = new StringBuilder();// 用于拼接字符串

		for (int i = 0; i < fileName.size(); i++) {
			if (space.contains(i)) {// 空格判断
				str.append(" ");
			}
			if (enter.contains(i)) {// 换行判断
				str.append("\n");
			}

			// 从文件名集合中构造文件对象
			File imageFile = new File(fileName.get(i));

			// 调用识别库进行识别
			ITesseract instance = new Tesseract();
			instance.setDatapath(System.getProperty("user.dir"));

			try {
				String result = instance.doOCR(imageFile);
				System.out.println(result);

				if (result.length() != 0)
					str.append(result.substring(0, result.length() - 2));
			} catch (TesseractException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return str.toString();
	}

	/*
	 * 保存切割点始末点对
	 */
	private class Pair {
		int x;
		int y;

		public Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
