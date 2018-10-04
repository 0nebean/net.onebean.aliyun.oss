package net.onebean.component.aliyun.image;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.onebean.component.aliyun.MimeTypes;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;

/**
 * 图像处理，封装了诸如缩略图生成、水印、格式转换等API
 * 
 * @author yc
 *
 */
public class ImageUtil {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
	private static final Logger logger = Logger.getLogger(ImageUtil.class);
//	private static Boolean include = false;

	// 最大允许下载2M以内的图像
	public final static int DOWNLOAD_IMG_MAX_LENGHT = 1024 * 1024 * 2;

	public enum IMAGE_FORMAT {
		BMP("bmp"), JPG("jpg"), WBMP("wbmp"), JPEG("jpg"), PNG("png"), GIF(
				"gif"), WEBP("webp");

		private String value;

		IMAGE_FORMAT(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	/**
	 * 获取以当前时间时分秒加四位随机数的图片名称
	 * 
	 * @return String
	 */
	public static String imageName() {
		Calendar cl = Calendar.getInstance();
		int max = 999999;
		int min = 100000;
		Random random = new Random();
		int s = random.nextInt(max) % (max - min + 1) + min;
		return sdf.format(cl.getTime()) + s;
	}

	/**
	 * 按比例通过指定像素大小缩放图片 若图片横比${width}小，高比${height}小，不变
	 * 若图片横比${width}小，高比${height}大，高缩小到${height}，图片比例不变
	 * 若图片横比${width}大，高比${height}小，横缩小到${width}，图片比例不变
	 * 若图片横比${width}大，高比${height}大，图片按比例缩小，横为${width}或高为${height}
	 * 
	 * @param sourceFile
	 * @param toFile
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	public static void scale(String sourceFile, String toFile, int width,int height) throws IOException {
		String formatName = ImageUtil.getImageFormatName(new File(sourceFile));
		if (formatName == IMAGE_FORMAT.GIF.getValue()) {
			GifDecoder gd = new GifDecoder();
			int status = gd.read(new FileInputStream(new File(sourceFile)));
			if (status != GifDecoder.STATUS_OK) {
				return;
			}

			AnimatedGifEncoder ge = new AnimatedGifEncoder();
			ge.start(new FileOutputStream(new File(toFile)));
			ge.setRepeat(0);

			for (int i = 0; i < gd.getFrameCount(); i++) {
				BufferedImage frame = gd.getFrame(i);
				BufferedImage rescaled = Scalr.resize(frame,
						Scalr.Mode.FIT_EXACT, width,
						height);
				int delay = gd.getDelay(i);
				ge.setDelay(delay);
				ge.addFrame(rescaled);
			}
			ge.finish();
		} else {
			Thumbnails.of(sourceFile).size(width, height).outputQuality(1)
					.toFile(toFile);
		}
	}

	/**
	 * 
	 * @param backgroundFile
	 * @param overFile
	 * @param newFile
	 * @param width
	 * @param height
	 */
	public static void watermark(String backgroundFile, String overFile,String newFile, int width, int height, int x, int y) {
		try {
			Thumbnails
					.of(backgroundFile)
					.size(width, height)
					.watermark(new CurstomPosition(x, y),
							ImageIO.read(new File(overFile)), 1f)
					.outputQuality(1f).toFile(newFile);
		} catch (IOException e) {
			logger.error(e);
		}
	}

//	public static void main(String[] ags) throws IOException {
//		Point point = new Point(300, 800);
//		Thumbnails
//				.of("E:/11.jpg")
//				.size(1280, 1024)
//				.watermark(Positions.CENTER,
//						ImageIO.read(new File("E:/adv_007.jpg")), 0.9f)
//				.outputQuality(0.8f)
//				.toFile("E:/image_watermark_bottom_right.jpg");
//	}

	/**
	 * 旋转照片
	 * 
	 * @param sourceFile
	 * @param toFile
	 * @param angle
	 */
	public static void rotate(String sourceFile, String toFile, double angle) {
		try {
			Thumbnails.of(sourceFile).scale(1).rotate(angle).toFile(toFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 为图片添加水印
	 * 
	 * @param sourceFile
	 * @param toFile
	 * @param logoImg
	 * @throws IOException
	 */
	public static void addLogo(String sourceFile, String toFile, String logoImg)
			throws IOException {
		// watermark(位置，水印图，透明度)
		Thumbnails
				.of(sourceFile)
				.watermark(Positions.BOTTOM_RIGHT,
						ImageIO.read(new File(logoImg)), 0.5f)
				.outputQuality(0.8f).toFile(toFile);

	}

	/**
	 * 获取图片格式
	 * 
	 * @param file
	 *            图片文件
	 * @return 图片格式
	 */
	public static String getImageFormatName(File file) {
		String formatName = null;
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> imageReader = ImageIO.getImageReaders(iis);
			if (imageReader.hasNext()) {
				ImageReader reader = imageReader.next();
				formatName = reader.getFormatName();
			}
		} catch (IOException e) {
			logger.error("获取图片类型发生异常", e);
		}
		return formatName;
	}

	/**
	 * 转换为MimeType
	 * 
	 * @param type
	 * @return
	 */
	public static String getMimeType(String type) {
		if (type.equals(IMAGE_FORMAT.GIF)) {
			return MimeTypes.GIF.getValue();
		} else if (type.equals(IMAGE_FORMAT.JPEG.getValue())
				|| type.equals(IMAGE_FORMAT.JPG.getValue())) {
			return MimeTypes.JPEG.getValue();
		} else if (type.equals(IMAGE_FORMAT.PNG.getValue())) {
			return MimeTypes.PNG.getValue();
		} else if (type.equals(IMAGE_FORMAT.BMP.getValue())) {
			return MimeTypes.BMP.getValue();
		} else if (type.equals(IMAGE_FORMAT.WEBP.getValue())) {
			return MimeTypes.WEBP.getValue();
		}
		return MimeTypes.GIF.getValue();
	}

	/**
	 * 从给定的地址下载图片到本地
	 * 
	 * @param url
	 * @param localPath
	 * @return
	 */
	public static String downLoadImage(String urlPath, String localPath) throws  Exception{
		URL  url = new URL(urlPath);
		logger.debug("开始从给定URL下载图片" + url.toString());
		String fileName = imageName();
		String type = "jpg";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if(url.toString().indexOf(" ") != -1){
			return fileName;
		}
		HttpGet httpget = new HttpGet(url.toString());
		httpget.setHeader(HTTP.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36");
		httpget.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
		httpget.setConfig(requestConfig);

		try {
			HttpResponse resp = httpClient.execute(httpget);
			if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
				HttpEntity entity = resp.getEntity();
				InputStream in = entity.getContent();
				Header cType = resp.getFirstHeader("content-type");
				String imgtype = "jpg";
				if (cType != null) {
					imgtype = cType.getValue();
				} else {
					imgtype = URLConnection.guessContentTypeFromStream(in);
				}

				if (imgtype != null) {
					imgtype = imgtype.substring(imgtype.indexOf("/") + 1)
							.toLowerCase();
					imgtype = checkImageType(imgtype);
					fileName = fileName + "." + imgtype;
				} else {
					fileName = fileName + "." + type;
				}

				savePicToDisk(in, localPath, fileName);

				if ("webp".equals(imgtype)) {
					// webp形式的图像需要另外处理
					logger.debug("图像为webp格式，需要转换为jpg进行显示");
					imgtype = "jpg";
					ImageUtil.webpToJpg(
							localPath + File.separator + fileName,
							localPath + File.separator
									+ fileName.replace("webp", imgtype));
					fileName = fileName.replace("webp", imgtype);
				}
			}
		} catch (Exception e) {
			logger.error("图片下载失败", e);
		} finally {
			
		}
		return fileName;
	}

	private static String checkImageType(String imgType) {
		if ("png".equalsIgnoreCase(imgType)) {
			return "png";
		} else if ("jpeg".equalsIgnoreCase(imgType)) {
			return "jpg";
		} else if ("gif".equalsIgnoreCase(imgType)) {
			return "gif";
		} else if ("bmp".equalsIgnoreCase(imgType)) {
			return "bmp";
		} else if ("webp".equalsIgnoreCase(imgType)) {
			return "webp";
		} else {
			return "jpg";
		}
	}

	/**
	 * 将图片写到 硬盘指定目录下
	 * 
	 * @param in
	 * @param dirPath
	 * @param filePath
	 */
	public static void savePicToDisk(InputStream in, String dirPath,String filePath) {
		try {
			File dir = new File(dirPath);
			if (dir == null || !dir.exists()) {
				dir.mkdirs();
			}

			// 文件真实路径
			String realPath = dirPath.concat(filePath);
			File file = new File(realPath);
			if (file == null || !file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = in.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
			fos.flush();
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Webp格式图片转换为jpg图片格式
	 * 
	 * @param webpPath
	 * @param jpgPath
	 */
	public static void webpToJpg(String webpPath, String jpgPath) {
//		if (!include) {
//			IIORegistry r = IIORegistry.getDefaultInstance();
//			WebPImageReaderSpi s = new WebPImageReaderSpi();
//			r.registerServiceProvider(s);
//			include = true;
//		}
		BufferedImage bi;
		try {
			bi = ImageIO.read(new File(webpPath));
			ImageIO.write(bi, "jpg", new File(jpgPath));
		}catch (IIOException e) {
			logger.error("webp转换为jpg时发生异常",e);
		} catch (IOException e) {
			logger.error("webp转换jpg时发生异常,webpPath:" + webpPath);
		} catch (Exception e) {
			 logger.error("webp转换为jpg时发生异常",e);
		}
	}

	/**
	 * 使用ImageIO的方式下载图片
	 * 
	 * @param urlStr
	 * @param filePath
	 * @param format
	 */
	public static void downLoadImageUseImageIO(String urlStr, String filePath,String formatName) {
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e1) {
			logger.error(e1);
		}
		if (formatName == null) {
			HttpURLConnection urlconnection;
			try {
				urlconnection = (HttpURLConnection) url.openConnection();
				urlconnection.connect();
				BufferedInputStream bis = new BufferedInputStream(
						urlconnection.getInputStream());
				String fileType = HttpURLConnection
						.guessContentTypeFromStream(bis);
				bis.close();
				urlconnection.disconnect();
				String imgtype = "jpg";
				if (fileType != null) {
					imgtype = imgtype.substring(imgtype.indexOf("/") + 1)
							.toLowerCase();
					imgtype = checkImageType(imgtype);
					formatName = imgtype;
				}
			} catch (IOException e) {
				logger.error(e);
			}
			try {
				BufferedImage img = ImageIO.read(url);
				ImageIO.write(img, formatName, new File(filePath));
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 通过读取文件并获取其width及height的方式，来判断判断当前文件是否图片,以及图片是否完整，这是一种非常简单的方式。
	 * 
	 * @param imageFilePath
	 * @return
	 */
	public static boolean checkImage(String imageFilePath) {
		File imageFile = new File(imageFilePath);
		if (!imageFile.exists()) {
			return false;
		}
		Image img = null;
		try {
			img = ImageIO.read(imageFile);
			if (img == null || img.getWidth(null) <= 0
					|| img.getHeight(null) <= 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			img = null;
		}
	}

	/**
	 * 给图片加上文字水印
	 * 
	 * @param filePath
	 *            图片路径
	 * @param markContent
	 *            文字内容
	 * @param markContentColor
	 *            文字颜色
	 * @param qualNum
	 *            透明度
	 * @param fontType
	 *            字体类型
	 * @param fontSize
	 *            字体大小
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean createMark(String filePath, String markContent,
                                     Color markContentColor, float qualNum, String fontType,
                                     int fontSize, int x, int y) {
		ImageIcon imgIcon = new ImageIcon(filePath);
		Image theImg = imgIcon.getImage();
		int width = theImg.getWidth(null);
		int height = theImg.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimage.createGraphics();
		g.setColor(markContentColor);
		g.setBackground(Color.white);
		g.drawImage(theImg, 0, 0, null);
		AttributedString ats = new AttributedString(markContent);
		Font f = new Font(fontType, Font.BOLD, fontSize);

		ats.addAttribute(TextAttribute.FONT, f, 0, markContent.length());
		AttributedCharacterIterator iter = ats.getIterator();

		g.drawString(iter, x, y); // 添加水印的文字和设置水印文字出现的内容
		g.dispose();

		try {
			FileOutputStream out = new FileOutputStream(filePath);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bimage);
			param.setQuality(qualNum, true);
			encoder.encode(bimage, param);
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 获取图片宽度
	 * @param file  图片文件
	 * @return 宽度
	 */
	public static int getImgWidth(File file) {
		InputStream is = null;
		BufferedImage src = null;
		int ret = -1;
		try {
			is = new FileInputStream(file);
			src = javax.imageio.ImageIO.read(is);
			// 得到源图宽
			ret = src.getWidth(null);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}


	/**
	 * 获取图片高度
	 * @param file  图片文件
	 * @return 高度
	 */
	public static int getImgHeight(File file) {
		InputStream is = null;
		BufferedImage src = null;
		int ret = -1;
		try {
			is = new FileInputStream(file);
			src = javax.imageio.ImageIO.read(is);
			// 得到源图高
			ret = src.getHeight(null);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
