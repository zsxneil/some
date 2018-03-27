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

			//通过JPEG图象流创建JPEG数据流解码器

//			JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(is);

			//解码当前JPEG数据流，返回BufferedImage对象

//			BufferedImage buffImg = jpegDecoder.decodeAsBufferedImage();
			BufferedImage buffImg = ImageIO.read(is);
			//得到画笔对象
			Graphics g = buffImg.getGraphics();
			//创建你要附加的图象。
			//2.jpg是你的小图片的路径
			ImageIcon imgIcon = new ImageIcon(basePath + "1.jpg");

			//得到Image对象。

			Image img = imgIcon.getImage();

			//将小图片绘到大图片上。

			//5,300 .表示你的小图片在大图片上的位置。

			boolean result = g.drawImage(img,0,0,null);
			System.out.println(result);

			//设置颜色。

			g.setColor(Color.white);

			//最后一个参数用来设置字体的大小

			Font f = new Font("微软雅黑",Font.PLAIN,100);

			g.setFont(f);
			//10,20 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。

			g.drawString(lastName,40,120);
			g.drawString("第二个",40,180);

			g.dispose();

			OutputStream os = new FileOutputStream(basePath+lastName.hashCode()+".png");
			//创键编码器，用于编码内存中的图象数据。
//			JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);

//			en.encode(buffImg);
			ImageIO.write(buffImg, "PNG", os);
			is.close();

			os.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
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


}
