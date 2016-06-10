import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollBar;
import javax.swing.JLabel;
import java.awt.Canvas;
import javax.swing.JScrollPane;
import java.awt.Panel;
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

public class OcrUI extends JFrame implements ActionListener{

	private JPanel contentPane;
	private Canvas canvas = null;
	private JTextArea textArea = null;
	private JButton button = null;
	private JButton button_1 = null;
	private File file = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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

		canvas = new Canvas();
		textArea = new JTextArea();
		button = new JButton("\u9009\u62E9\u56FE\u7247\u6587\u4EF6");
		button_1 = new JButton("保存文件");
		creatDisplay();
	}
	
	public void creatDisplay() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("\u8981\u8F6C\u6362\u7684\u56FE\u7247\uFF1A");
		label.setBounds(10, 10, 84, 15);
		contentPane.add(label);
		
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
	
	public void addEvent() {
		button.addActionListener(this);
		button_1.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==button) {
			open();
		} else {
			close();
		}
	}
	
	public void open() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(this);
		file = fileChooser.getSelectedFile();
	}
	
	public void close() {
		String str = textArea.getText();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showSaveDialog(this);
		File file = fileChooser.getSelectedFile();
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
}
