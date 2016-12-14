package org.twinnation.imgcompressor;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;




public class ImageCompressor {

	public static final float LOW_COMPRESSION_FACTOR = 0.8f;
	public static final float MED_COMPRESSION_FACTOR = 0.5f;
	public static final float HIGH_COMPRESSION_FACTOR = 0.2f;

	/** lower = smaller size AND quality */
	private float compressionFactor;
	private String outputFolder;


	/** Default constructor */
	public ImageCompressor() {
		this.compressionFactor = MED_COMPRESSION_FACTOR;
	}


	/**
	 * Constructor
	 * @param compressionFactor Compression factor
	 */
	public ImageCompressor(float compressionFactor, String outputFolder) {
		this.compressionFactor = compressionFactor;
		this.outputFolder = outputFolder;
	}


	public String compressJPG(String imageFileName) throws Exception {
		String extension = imageFileName.substring(imageFileName.lastIndexOf('.')+1);
		String baseName = imageFileName.substring(0, imageFileName.lastIndexOf('.'));
		String outputImageFileName = baseName + "_min." + extension;

		File inputImg = new File(imageFileName); // source
		File outputImg = new File(outputImageFileName); // destination

		// I/O streams & writer
		InputStream input = new FileInputStream(inputImg);
		OutputStream output = new FileOutputStream(outputImg);
		BufferedImage bufferedImage = ImageIO.read(input);
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(extension);
		ImageWriter imageWriter = writers.next();
		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(output);
		imageWriter.setOutput(imageOutputStream);
		ImageWriteParam params = imageWriter.getDefaultWriteParam();

		// Compress the image
		params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		params.setCompressionQuality(compressionFactor);

		// Create new image file
		IIOImage iioImage = new IIOImage(bufferedImage, null, null);
		imageWriter.write(null, iioImage, params);

		// Close streams
		input.close();
		output.close();
		imageOutputStream.close();
		imageWriter.dispose();

		// calculate size difference between old file vs new file
		double difference = (double)(inputImg.length()-outputImg.length());
		if (difference < 0) {
			System.out.println("The image cannot be compressed, will copy the original image file instead");
			Files.copy(inputImg.toPath(), outputImg.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		// if there's an outputFolder set, make sure it's created and move the file
		if (outputFolder != null) {
			File outputDir = new File(outputFolder);
			if (!outputDir.exists()) {
				if (outputDir.mkdir()) {
					System.out.println("Output directory created at " + outputFolder);
				}
			}
			outputImg.renameTo(new File(outputFolder+"/"+outputImg.getName()));
		}
		System.out.println("The image is "+(int)((difference/inputImg.length())*100)+"% less large.");
		return "The image is "+(int)((difference/inputImg.length())*100)+"% less large.";
	}

	/* * * * * * * * * * * *
	 * SETTERS AND GETTERS *
	 * * * * * * * * * * * */

	/**
	 * Sets the compression factor (lower = smaller size, lower quality)
	 * @param compressionFactor Compression factor
	 */
	public void setCompressionFactor(float compressionFactor) {
		this.compressionFactor = compressionFactor;
	}


	/**
	 * Sets the output folder in which the compressed files will be moved
	 * @param outputFolder Output folder
	 */
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}


	/**
	 * Gets the output folder
	 * @return Output folder
	 */
	public String getOutputFolder() {
		return outputFolder;
	}



}