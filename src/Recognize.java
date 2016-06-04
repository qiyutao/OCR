import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.security.auth.x500.X500Principal;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;


public class Recognize {
	private BufferedImage binaryImg = null;
	private List<Pair> point = null;
	private List<double[][]> mat = null;
	private int hImg;
	private int wImg;
	
	public Recognize(BufferedImage img) {
		binaryImg = img;
		hImg = img.getHeight();
		wImg = img.getWidth();
	}
	
	public void division() {
		Set<Integer> st = getPoint();
		getCut(st);
		divImg();
	}
	
	public Set<Integer> getPoint() {
		Set<Integer> st = new HashSet<>();
		for(int i=0;i<wImg;i++) {
			for(int j=0;j<hImg;j++) {
				int pix = binaryImg.getRGB(i, j);
				if(pix==-16777216) {
					st.add(i);
				}
				
			}
		}
		/*Iterator<Integer> it = st.iterator();
		while(it.hasNext())
			System.out.println(it.next());*/
		
		return st;
	}
	
	public void getCut(Set<Integer> st) {
		int x = 0;
		int y = 0;
		int old = -1;
		point = new ArrayList<>();
		
		Iterator<Integer> it = st.iterator();
		while(it.hasNext()) {
			if(old<0) { 
				x = it.next();
				old = x;
			}
			y = it.next();
			
			if(x+1!=y) {
				Pair p = new Pair(old,x);
				point.add(p);//System.out.println(old+"  "+x);
				old = y;
				
			}
			x = y;
		}
		Pair p = new Pair(old,x);
		point.add(p);//System.out.println(old+"  "+x);
	}
	
	public void divImg() {
		mat = new ArrayList<>();
	
		for(Pair pair:point) {
			int x = pair.x;
			int y = pair.y;
			BufferedImage tmpImg = new BufferedImage(y-x+1, hImg, BufferedImage.TYPE_BYTE_BINARY);
			double[][] tmpMat = new double[hImg][y-x+1];
			int total=0;
			for(int i=0;i<y-x+1;i++) {
				for(int j=0;j<hImg;j++) {
					int rgb = binaryImg.getRGB(x+i, j);
					//System.out.println(1);
					tmpImg.setRGB(i, j, rgb);
					/*if(rgb==-1)
						//tmpMat[j][i] = 0;
					else
						total++;//tmpMat[j][i] = 1;*/
					//////////
					if(rgb!=-1)
						total++;
				}
			}
			mat.add(tmpMat);
			/////////////////////
			System.out.println(total);
			try {
				ImageIO.write(tmpImg, "jpg", new File("d:\\tmp"+x+".jpg"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void match() {
		//System.out.println(mat.size());
		//for(int i=0;i<mat.size();i++) {
			double[] eigenVal = getEigenvalue(4);
			for(int j=0;j<eigenVal.length;j++)
				System.out.println(eigenVal[j]);
		//}
	}
	
	public double[] getEigenvalue(int num) {
		double[][] array = mat.get(num);
		Matrix aMatrix = new Matrix(array);
		EigenvalueDecomposition evd = new 
				EigenvalueDecomposition(aMatrix);
		aMatrix.print(aMatrix.getRowDimension(), aMatrix.getColumnDimension());
		double[] eigenVal = evd.getRealEigenvalues();
		return eigenVal;
	}
	
	private class Pair {
		int x;
		int y;
		
		public Pair(int x,int y) {
			this.x = x;
			this.y = y;
		}
	}
}
