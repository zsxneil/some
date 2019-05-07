package image;

import org.junit.Test;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImgUtils {
//
	public static boolean createNamePhoto(String basePath,String lastName){
		boolean isSuccess=true;
		try {
			String fileName = "certificate";
			InputStream is = new FileInputStream(basePath + fileName + ".png");

			BufferedImage buffImg = ImageIO.read(is);
			//得到画笔对象
			Graphics g = buffImg.getGraphics();
			//创建你要附加的图象。
			//2.jpg是你的小图片的路径
			ImageIcon imgIcon = new ImageIcon(basePath + "certificate_cover.jpg");

			//得到Image对象。
			Image img = imgIcon.getImage();

			//将小图片绘到大图片上。
			//5,300 .表示你的小图片在大图片上的位置。
			boolean result = g.drawImage(img,0,0,null);
			System.out.println(result);

			//设置颜色。
			g.setColor(Color.red);

			//最后一个参数用来设置字体的大小
			Font f = new Font("微软雅黑",Font.PLAIN,100);

			g.setFont(f);
			//10,20 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
			g.drawString(lastName,300,500);
			g.drawString("第二个",300,600);

			g.dispose();

			OutputStream os = new FileOutputStream(basePath+lastName.hashCode()+".png");

			ImageIO.write(buffImg, "PNG", os);
			is.close();
			os.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 	
		return isSuccess;
	}
	
	public static void main(String[] args) {
		String basePath = "F:\\";
		String lastName = "张";
		createNamePhoto(basePath,lastName);
	}

	@Test
	public void createCeritificateImg() throws IOException {
		String basePath = "F:\\";
		String fileName = "certificate";
		InputStream is = new FileInputStream(basePath + "certificate.png");

		BufferedImage buffImg = ImageIO.read(is);
		//得到画笔对象
		Graphics g = buffImg.getGraphics();
		//创建你要附加的图象。
		//2.jpg是你的小图片的路径
		ImageIcon imgIcon = new ImageIcon(basePath + "certificate_cover.jpg");

		//得到Image对象。
		Image img = imgIcon.getImage();

		String name = "刘伟斌";
		String year = "2018";
		String month = "9";
		String trainProject = "EAS客户化开发训练营";
		String ID = "510525199309150150";
		String certificateNumber = "E06-201705-0000";

		//将小图片绘到大图片上。
		//5,300 .表示你的小图片在大图片上的位置。
		boolean result = g.drawImage(img,0,0,null);
		System.out.println(result);

		//设置颜色。
		g.setColor(Color.BLACK);

		//最后一个参数用来设置字体的大小
		Font f = new Font("华文楷体",Font.BOLD,42);
		g.setFont(f);
		//10,20 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
		g.drawString(name,360,625);
		g.drawString(year,605,625);
		g.drawString(month,810,625);
		g.drawString(trainProject,330,700);

		Font f1 = new Font("微软雅黑",Font.PLAIN,26);
		g.setFont(f1);
		g.drawString(ID,540,1062);
		g.drawString(certificateNumber,540,1112);



		g.dispose();

		OutputStream os = new FileOutputStream(basePath+fileName.hashCode()+".png");

		ImageIO.write(buffImg, "PNG", os);
		is.close();
		os.close();
	}

	public static Font getFont(String fontStyle, float fontSize) {
		Font font = null;
		FileInputStream fileInputStream = null;
		String fontUrl = "";
		try {
			switch (fontStyle) {
				case "楷体":
					//文悦新青年体
					fontUrl = "F:\\fonts\\simkai.ttf";
					break;
				default:
					fontUrl = "F:\\fonts\\micross.ttf";
					break;
			}
			fileInputStream = new FileInputStream(new File(fontUrl));
			Font tempFont = Font.createFont(Font.TRUETYPE_FONT,fileInputStream);
			font = tempFont.deriveFont(fontSize);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return font;
	}

}
