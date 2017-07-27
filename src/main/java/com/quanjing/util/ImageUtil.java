package com.quanjing.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.FileCopyUtils;

import com.sun.imageio.plugins.bmp.BMPImageReader;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.png.PNGImageReader;

/**
 * 图片操作类
 *
 */
public class ImageUtil {
	private static Logger logg = LoggerFactory.getLogger(ImageUtil.class);
	public static byte[] creatImage(String rands, int WIDTH, int HEIGHT)
			throws IOException {

		// 创建内存图象并获得其图形上下文
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();

		// 产生图像
		drawBackground(g, WIDTH, HEIGHT);

		drawRands(g, rands.toCharArray());

		// 结束图像的绘制过程，完成图像
		g.dispose();

		// 将图像输出到客户端
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ImageIO.write(image, "JPEG", bos);

		byte[] buf = bos.toByteArray();
		bos.close();
		return buf;

	}

	// 获取随机颜色
	private static Color randomColor() {
		Random rand = new Random();

		return new Color(Math.abs(rand.nextInt()) % 256, Math.abs(rand
				.nextInt()) % 256, Math.abs(rand.nextInt()) % 256);
	}

	private static void drawRands(Graphics g, char[] rands) {

		g.setFont(new Font(null, Font.ITALIC | Font.BOLD, 24));

		// 在不同的高度上输出验证码的每个字符
		g.setColor(randomColor());
		g.drawString("" + rands[0], 1, 19);
		g.setColor(randomColor());
		g.drawString("" + rands[1], 16, 25);
		g.setColor(randomColor());
		g.drawString("" + rands[2], 31, 20);
		g.setColor(randomColor());
		g.drawString("" + rands[3], 46, 23);

	}

	private static void drawBackground(Graphics g, int WIDTH, int HEIGHT) {
		// 画背景
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		// 随机产生120个干扰点
		for (int i = 0; i < 120; i++) {
			int x = (int) (Math.random() * WIDTH);

			int y = (int) (Math.random() * HEIGHT);

			int red = (int) (Math.random() * 255);

			int green = (int) (Math.random() * 255);

			int blue = (int) (Math.random() * 255);

			g.setColor(new Color(red, green, blue));

			g.drawOval(x, y, 1, 0);

		}

	}

	public static BufferedImage  getImage(String destUrl) {
		URL url;
		HttpURLConnection connection=null;
		try {
			url = new URL(destUrl);
			  connection = (HttpURLConnection)url.openConnection(); //打开连接  
		       connection.setDoOutput(true);  
		       BufferedImage src = ImageIO.read(connection.getInputStream()); //读取连接的流，赋值给BufferedImage对象
		       return src;
		} catch (Exception e) {
			e.printStackTrace();
		}finally { 
			connection.disconnect();
		}
	      return  null;
	}

	/**
	 * 将一个图片转换到另一个等比例大小的图片
	 * 
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public static byte[] scaleImage(byte[] data, int width, int height)
			throws IOException {
		BufferedImage buffered_oldImage = ImageIO
				.read(new ByteArrayInputStream(data));
		int imageOldWidth = buffered_oldImage.getWidth();
		int imageOldHeight = buffered_oldImage.getHeight();

		int imageNewWidth = 0;
		int imageNewHeight = 0;

		float rate = (float) imageOldWidth / (float) imageOldHeight;// 宽高比例

		if (imageOldWidth > width && imageOldHeight <= height) {
			imageNewWidth = width;
			imageNewHeight = new Float((float) imageNewWidth / rate).intValue();
		} else if ((imageOldWidth <= width && imageOldHeight > height)
				|| (imageOldWidth > width && imageOldHeight > height)) {
			imageNewHeight = height;
			imageNewWidth = new Float((float) imageNewHeight * rate).intValue();
		} else {
			imageNewWidth = width;
			imageNewHeight = height;
		}

		BufferedImage buffered_newImage = new BufferedImage(imageNewWidth,
				imageNewHeight, BufferedImage.TYPE_INT_RGB);
		buffered_newImage.getGraphics()
				.drawImage(
						buffered_oldImage.getScaledInstance(imageNewWidth,
								imageNewHeight, BufferedImage.SCALE_SMOOTH), 0,
						0, null);
		buffered_newImage.getGraphics().dispose();
		ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
		ImageIO.write(buffered_newImage, "jpeg", outPutStream);
		return outPutStream.toByteArray();
	}

	public static BufferedImage zoomScale(BufferedImage im, int w, int h) {
		// 获得尺寸
		int oW = im.getWidth();
		int oH = im.getHeight();

		int nW = w, nH = h;

		/*
		 * 缩放
		 */
		// 未指定图像高度，根据原图尺寸计算出高度
		if (h == -1) {
			nH = (int) ((float) w / oW * oH);
		}
		// 未指定图像宽度，根据原图尺寸计算出宽度
		else if (w == -1) {
			nW = (int) ((float) h / oH * oW);
		}

		// 创建图像
		BufferedImage re = new BufferedImage(nW, nH, ColorSpace.TYPE_RGB);
		re.getGraphics().drawImage(im, 0, 0, nW, nH, null);
		// 返回
		return re;
	}

	public static String zoomScale(File file, int w, int h) throws IOException {

		BufferedImage target = zoomScale(ImageIO.read(file), w, h);
		String destPhotoName = file.getPath();

		destPhotoName = destPhotoName.substring(0, destPhotoName.indexOf("."))
				+ "_" + w + "_" + h + ".jpg";

		ImageIO.write(target, "jpg", new File(destPhotoName));
		return destPhotoName;

	}

	/**
	 * 将一个图片，按照比例缩放到固定大小 并保存成另外一个图片
	 * 
	 * @param srcData
	 * @param destFileName
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	public static String savePic(byte[] srcData, String photoFullPath,
			String photoName, int width, int height) throws IOException {
		String destPhotoName = null;
		FileCopyUtils.copy(srcData, new FileOutputStream(new File(photoFullPath
				+ photoName + ".jpg")));
		if (width != 0 && height != 0) {
			srcData = scaleImage(srcData, width, height);
			destPhotoName = photoFullPath + photoName + "_" + width + "_"
					+ height + ".jpg";
			FileCopyUtils.copy(srcData, new FileOutputStream(new File(
					destPhotoName)));
		}
		return destPhotoName;
	}

	public void savePicProperty(final byte[] srcData, String photoFullPath,
			ReloadableResourceBundleMessageSource messageSource)
			throws IOException {
		String isize = messageSource.getMessage("image.size", null, null, null)
				.trim();
		String isuffix = messageSource.getMessage("image.suffix", null, null,
				null).trim();
		String[] isizeA = isize.split("_");
		String[] isuffixA = isuffix.split("_");
		for (int i = 0; i < isizeA.length; i++) {
			int width = Integer.valueOf(isizeA[i].split("\\*")[0]);
			int height = Integer.valueOf(isizeA[i].split("\\*")[1]);
			byte[] newData = scaleImage(srcData, width, height);
			for (int j = 0; j < isuffixA.length; j++) {
				String destPhotoName = photoFullPath.substring(0,
						photoFullPath.lastIndexOf("."))
						+ "_" + width + "_" + height + "." + isuffixA[j];
				FileCopyUtils.copy(newData, new FileOutputStream(new File(
						destPhotoName)));
			}
		}
	}

	/**
	 * 根据上传文件流判断上传图片类型
	 * 
	 * @param fis
	 * @return 图片类型 gif,jpg,png,bmp
	 * @throws IOException
	 */
	public static String imageType(InputStream fis) throws IOException {
		int leng = fis.available();
		BufferedInputStream buff = new BufferedInputStream(fis);
		byte[] mapObj = new byte[leng];
		buff.read(mapObj, 0, leng);

		String type = "";
		ByteArrayInputStream bais = null;
		MemoryCacheImageInputStream mcis = null;
		try {
			bais = new ByteArrayInputStream(mapObj);
			mcis = new MemoryCacheImageInputStream(bais);
			Iterator itr = ImageIO.getImageReaders(mcis);
			while (itr.hasNext()) {
				ImageReader reader = (ImageReader) itr.next();
				if (reader instanceof GIFImageReader) {
					type = "gif";
				} else if (reader instanceof JPEGImageReader) {
					type = "jpg";
				} else if (reader instanceof PNGImageReader) {
					type = "png";
				} else if (reader instanceof BMPImageReader) {
					type = "bmp";
				}
				if (!StringUtils.isEmpty(type)) {
					break;
				}
			}
		} finally {
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException ioe) {

				}
			}

			if (mcis != null) {
				try {
					mcis.close();
				} catch (IOException ioe) {

				}
			}
		}
		fis.reset();
		return type;
	}

	/**
	 * 将一个图片无视其原始尺寸转换尺寸为指定宽高图片(排除原始图片宽和高均小于指定宽高)
	 * 
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public static byte[] scaleFixedImage(byte[] data, int width, int height)
			throws IOException {
		BufferedImage buffered_oldImage = ImageIO
				.read(new ByteArrayInputStream(data));
		int imageOldWidth = buffered_oldImage.getWidth();
		int imageOldHeight = buffered_oldImage.getHeight();

		int imageNewWidth = width;
		int imageNewHeight = height;
		// 判断如果原始图片宽和高均小于指定宽高则使用原始尺寸
		if (imageOldWidth < width && imageOldHeight < height) {
			imageNewWidth = imageOldWidth;
			imageNewHeight = imageOldHeight;
		}
		BufferedImage buffered_newImage = new BufferedImage(imageNewWidth,
				imageNewHeight, BufferedImage.TYPE_INT_RGB);
		buffered_newImage.getGraphics()
				.drawImage(
						buffered_oldImage.getScaledInstance(imageNewWidth,
								imageNewHeight, BufferedImage.SCALE_SMOOTH), 0,
						0, null);
		buffered_newImage.getGraphics().dispose();
		ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
		ImageIO.write(buffered_newImage, "jpeg", outPutStream);
		return outPutStream.toByteArray();
	}

	/**
	 * 保存一个指定尺寸的图片和一个原始尺寸的图片
	 * 
	 * @param srcData
	 * @param photoFullPath
	 * @param photoName
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	public static String saveFixedPic(byte[] srcData, String photoFullPath,
			String photoName, int width, int height) throws IOException {
		File targetPicFile = null;
		FileCopyUtils.copy(srcData, new FileOutputStream(new File(photoFullPath
				+ photoName + ".jpg")));
		if (width != 0 && height != 0) {
			srcData = scaleFixedImage(srcData, width, height);
			String destPhotoName = photoFullPath + photoName + "_" + width
					+ "_" + height + ".jpg";
			targetPicFile = new File(destPhotoName);
			FileCopyUtils.copy(srcData, new FileOutputStream(targetPicFile));
		}
		return targetPicFile.getName();
	}

	public static String getMd5ByFile(FileInputStream in, long size) {
		String value = null;
		try {
			MappedByteBuffer byteBuffer = in.getChannel().map(
					FileChannel.MapMode.READ_ONLY, 0, size);

			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
	
	/**
	 * 图片是否存在
	 * @param urlString
	 * @return
	 */
	public static boolean canDownload(String urlString) {
		boolean ret = false;
		CloseableHttpClient httpclient = null;
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(120 * 1000)
				.setConnectTimeout(120 * 1000).build();
		httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
		HttpGet httpget = new HttpGet(urlString);
		logg.info("executing request " + httpget.getURI());
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
			if (response.getStatusLine().getStatusCode() != 200) {
				logg.error("Got bad response, error code = " + response.getStatusLine() + " imageUrl: " + urlString);
				return false;
			}

			HttpEntity entity = response.getEntity();

			logg.info(entity.getContentType().getValue() + "," + entity.getContentLength());
			if (entity.getContentType().getValue().indexOf("image") != -1 && entity.getContentLength() > 1) {
				ret = true;
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}