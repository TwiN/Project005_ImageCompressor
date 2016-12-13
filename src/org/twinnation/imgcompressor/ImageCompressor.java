package org.twinnation.imgcompressor;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

//
// IN PROGRESS
//


public class ImageCompressor {

	/** lower = smaller size AND quality */
	public static final float IMAGE_QUALITY_COMPRESSION_FACTOR = 0.5f;


	/**
	 * Converts a png file to a jpg file
	 * WARNING: JPG does not support transparent! It will be replaced by white.
	 * @param imgName
	 * @deprecated
	 */
	public static void png2jpg(String imgName) {
		String baseName = imgName.substring(0, imgName.lastIndexOf('.'));
		try {
			BufferedImage bufferedImage = ImageIO.read(new File(imgName));
			BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
					bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
			// write to jpeg file
			ImageIO.write(newBufferedImage, "jpg", new File(baseName+"_converted.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void compressJPG(String imageFileName) throws Exception {
		String extension = imageFileName.substring(imageFileName.lastIndexOf('.')+1);
		String baseName = imageFileName.substring(0, imageFileName.lastIndexOf('.'));
		String outputImageFileName = baseName + "_min." + extension;
		// if extension is png {
		// 	png2jpg(imageFileName);
		// } TODO: before running that, warn the user that the picture will lose transparency

		File inputImg = new File(imageFileName); // source
		File outputImg = new File(outputImageFileName); // destination

		// io streams
		InputStream input = new FileInputStream(inputImg);
		OutputStream output = new FileOutputStream(outputImg);

		BufferedImage bufferedImage = ImageIO.read(input);
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(extension);

		ImageWriter imageWriter = writers.next();
		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(output);
		imageWriter.setOutput(imageOutputStream);

		ImageWriteParam params = imageWriter.getDefaultWriteParam();

		// Compress only if not png file...
		if (!(extension.equalsIgnoreCase("png"))) {
			params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			params.setCompressionQuality(IMAGE_QUALITY_COMPRESSION_FACTOR);
		}

		// Create new image file
		IIOImage iioImage = new IIOImage(bufferedImage, null, null);
		imageWriter.write(null, iioImage, params);

		// Close streams
		input.close();
		output.close();
		imageOutputStream.close();
		imageWriter.dispose();

		double difference = (double)(inputImg.length()-outputImg.length());
		System.out.println("The image is "+(int)((difference/inputImg.length())*100)+"% less large.");
	}

	public static void main(String[] args) throws Exception {
		compressJPG("input.jpg");


	}

}