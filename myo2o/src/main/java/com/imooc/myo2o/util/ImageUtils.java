package com.imooc.myo2o.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imooc.myo2o.dto.ImageHolder;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class ImageUtils {
	//获取水印图片的相对位置,水印图片放在项目的resources文件夹下面
	private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	private static Logger logger = LoggerFactory.getLogger(ImageUtils.class);
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final Random randomint = new Random();

	/*
	 * 对商品的缩略图进行处理: 
	 * CommonsMultpartFile是Spring自带的文件格式.表示要进行处理的图片
	 * 本来是要用CommonsMultpartFile的,但是做service层的测试的时候不方便传入参数,所以改成了File.
	 * targetAddr表示创建后的图片放到哪个文件夹
	 * 传入一个图片,创建其缩略图,缩略图规格是200*200,并加上水印透明度是35%,图片输出质量是0.8,并将图片输出到传入的targetAddr目录下.
	 *@return 新文件的相对路径
	 */
	public static String generateThumbnail(ImageHolder imageHolder, String targetAddr) {
		String randomFileName = getRandomFileName();
		String extension = getFileExtension(imageHolder.getImageName());
		mkdir(targetAddr);
		//把文件改名之后的相对路径,包含了文件名和文件后缀名
		String relativePath = targetAddr + randomFileName + extension;
		logger.debug("current relativePath:" + relativePath);
		//全路径
		File dest = new File(PathUtil.getImgBasePath() + relativePath);
		logger.debug("current completePath:" + dest);
		try {
			Thumbnails.of(imageHolder.getImageInputStream()).size(300, 300)
					.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/coding.jpg")), 0.35f)
					.outputQuality(0.8f).toFile(dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return relativePath;
	}

	/*
	 * 对商品详情图片进行处理
	 */
	public static String generateNormalImg(ImageHolder imageHolder, String targetAddr) {
		String randomFileName = getRandomFileName();
		String extension = getFileExtension(imageHolder.getImageName());
		mkdir(targetAddr);
		//把文件改名之后的相对路径,包含了文件名和文件后缀名
		String relativePath = targetAddr + randomFileName + extension;
		logger.debug("current relativePath:" + relativePath);
		//全路径
		File dest = new File(PathUtil.getImgBasePath() + relativePath);
		logger.debug("current completePath:" + dest);
		try {
			Thumbnails.of(imageHolder.getImageInputStream()).size(337, 640)
					.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/coding.jpg")), 0.35f)
					.outputQuality(0.9f).toFile(dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return relativePath;
	}

	/*
	 * 递归的创建目标文件地址所需要的所有的文件夹,包括绝对路径
	 * 比如/home/huanghong/Myo2o/image/shop/phone/目录
	 * 其中/home/huanghong/Myo2o/image/是绝对路径(ImgBasePath),shop/phone/是相对路径(targetAddr)
	 */
	private static void mkdir(String targetAddr) {
		String realFileParentPath = PathUtil.getImgBasePath() + targetAddr;
		File dirPath = new File(realFileParentPath);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
	}

	/*
	 * 获取传入文件的后缀名
	 */
	private static String getFileExtension(String fileName) {
		//CommonsMutiFile没有重写toString 方法.但File重写了
		String extension = fileName.substring(fileName.lastIndexOf("."));
		//String extension = cMFile.getOriginalFilename().substring(cMFile.getOriginalFilename().lastIndexOf("."));
		return extension;
	}

	/*
	 * 获取随机的文件名,文件名字组成如下:当前的年月日时分秒+五位随机数
	 */
	public static String getRandomFileName() {
		String randomFile = simpleDateFormat.format(new Date());
		int random = randomint.nextInt(89999) + 10000;
		return randomFile + random;
	}

	/*
	 * 传入一个相对路径的字符串,如果这个storePath是文件,则直接删除
	 * 如果storePath是目录,则先删除该目录下的全部文件.然后删除目录.
	 */
	public static void deleteFileOrPath(String storePath) {
		File fileOrPath = new File(PathUtil.getImgBasePath() + storePath);
		if (fileOrPath.exists()) {
			if (fileOrPath.isDirectory()) {
				File[] files = fileOrPath.listFiles();
				for (File file : files) {
					file.delete();
				}
			}
			fileOrPath.delete();
		}
	}

	/*
	 * 用于测试Thumbnails工具
	 */
	public static void main(String[] args) throws IOException {
		Thumbnails.of(new File("C:/Users/hh/Pictures/manyCoder.jpg")).size(600, 400)
				.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/coding.jpg")), 0.35f)
				.outputQuality(0.8).toFile(new File("C:/Users/hh/Pictures/manyCoderWithCoding.jpg"));

		//		System.out.println(System.getProperty("file.separator"));
		//		System.out.println(System.getProperty("os.name"));
		//		Properties properties = System.getProperties();
		//		String[] split = properties.toString().split(",");
		//		for (String string : split) {
		//			System.out.println(string);
		//		}

	}

}
