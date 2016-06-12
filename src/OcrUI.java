/*
 * class OcrUI
 * 负责程序主界面，输入输出显示
 * 
 * @version 0.7
 * @date 06-12-2016
 */

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollBar;
import javax.swing.JLabel;
import java.awt.Canvas;
import javax.swing.JScrollPane;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JFileChooser;

public class OcrUI extends JFrame implements ActionListener {

	private JPanel contentPane;//放置组件
	private MyCanvas canvas = null;//显示要转换的图片
	private JTextArea textArea = null;//显示转换后的字符串
	private JButton button = null;//选择文件按钮
	private JButton button_1 = null;//文件保存按钮
	private File file = null;//保存选择的文件

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//为主界面创建独立的线程
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OcrUI frame = new OcrUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public OcrUI() {
		//各对象初始化
		canvas = new MyCanvas();
		textArea = new JTextArea();
		button = new JButton("\u9009\u62E9\u56FE\u7247\u6587\u4EF6");
		button_1 = new JButton("保存文件");
		creatDisplay();
	}
	
	/*
	 * 创建并显示组件
	 */
	public void creatDisplay() {
		//窗口设置
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		//窗口功能提示标签
		JLabel label = new JLabel("被转换的图片：");
		label.setBounds(10, 10, 84, 15);
		contentPane.add(label);

		//为显示窗口附加滚动条
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 31, 580, 229);
		contentPane.add(scrollPane);

		scrollPane.setViewportView(canvas);

		JLabel label_1 = new JLabel("\u8F6C\u6362\u540E\u6587\u4EF6\uFF1A");
		label_1.setBounds(10, 270, 84, 15);
		contentPane.add(label_1);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 295, 578, 256);
		contentPane.add(scrollPane_1);

		scrollPane_1.setViewportView(textArea);

		button.setBounds(654, 69, 120, 32);
		contentPane.add(button);

		button_1.setBounds(654, 165, 120, 32);
		contentPane.add(button_1);

		textArea.setEditable(false);

		addEvent();
	}

	/*
	 * 为button加入事件监听
	 */
	public void addEvent() {
		button.addActionListener(this);
		button_1.addActionListener(this);
	}

	/*
	 * 事件处理方法
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == button) {//点击选择文件按钮
			open();
		} else {//点击保存按钮
			save();
		}
	}

	/*
	 * 点击选择文件按钮时
	 * 弹出文件选择对话框并调用图像识别类进行处理
	 */
	public void open() {
		//弹出文件选择对话框
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(this);
		//获取选择的文件
		file = fileChooser.getSelectedFile();
		
		//在canvas显示图像
		setImage();
		
		//对文件预处理并得到识别结果
		PreProcess bin = new PreProcess(file);
		String result = bin.getResult();
		
		//在文本域中显示结果
		textArea.setText(result);
	}

	/*
	 * 点击保存按钮后进行保存
	 */
	public void save() {
		//获取文本域中结果
		String str = textArea.getText();
		
		//弹出文件保存对话框选择要保存的路径
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showSaveDialog(this);
		File file = fileChooser.getSelectedFile();
		
		//获取文件输出流对象进行保存文件
		try {
			FileOutputStream fStream = new FileOutputStream(file);
			fStream.write(str.getBytes());
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 在canvas中显示图像
	 */
	public void setImage() {
		//获取图片对象
		Image img = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
		canvas.setImg(img);
		
		//进行重绘显示图像
		canvas.repaint();
	}

	/*
	 * class MyCanvas
	 * 继承Canvas
	 * 重写paint（）方法显示图片
	 */
	private class MyCanvas extends Canvas {
		private Image img = null;//要显示的图像

		/*
		 * set方法
		 * @par i 传入要显示的图像
		 */
		public void setImg(Image i) {
			img = i;
		}

		/*
		 * 重写paint方法显示图像
		 * @see java.awt.Canvas#paint(java.awt.Graphics)
		 */
		@Override
		public void paint(Graphics g) {
			// TODO Auto-generated method stub
			g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), this);

		}
	}
}
