package org.twinnation.imgcompressor;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

//
// IN PROGRESS
//


public class ImageCompressor {

	/** lower = smaller size AND quality */
	public static final float IMAGE_QUALITY_COMPRESSION_FACTOR = 0.5f;


	public static void main(String[] args) throws Exception {
		String imageFileName = "input.jpg";

		File inputImg = new File(imageFileName); // source
		File outputImg = new File("min_"+imageFileName); // destination

		// io streams
		InputStream input = new FileInputStream(inputImg);
		OutputStream output = new FileOutputStream(outputImg);

		BufferedImage bufferedImage = ImageIO.read(input);
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

		ImageWriter imageWriter = writers.next();
		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(output);
		imageWriter.setOutput(imageOutputStream);

		ImageWriteParam params = imageWriter.getDefaultWriteParam();

		// Compress
		params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		params.setCompressionQuality(IMAGE_QUALITY_COMPRESSION_FACTOR);

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

}