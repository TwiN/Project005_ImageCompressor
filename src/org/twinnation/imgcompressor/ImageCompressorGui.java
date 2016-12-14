package org.twinnation.imgcompressor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;


public class ImageCompressorGui extends JPanel implements ActionListener {

	private JButton btnBrowse, btnCompress;
	private JTextArea log;
	private JFileChooser fc;
	private String[] filePaths;
	private ImageCompressor imageCompressor;

	public ImageCompressorGui() {
		super(new BorderLayout());

		// Instantiate the image compressor
		imageCompressor = new ImageCompressor();
		imageCompressor.setOutputFolder(System.getProperty("user.home") +"/Desktop/CompressedImages");

		// Creates the log
		log = new JTextArea(10,30);
		log.setMargin(new Insets(5,5,5,5));
		log.setEditable(false);
		log.append("The compressed images will be located at: \n"+imageCompressor.getOutputFolder()+"\n\n");
		JScrollPane logScrollPane = new JScrollPane(log);

		// Create the open button
		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(this);

		// Create the save button
		btnCompress = new JButton("Compress");
		btnCompress.addActionListener(this);
		btnCompress.setEnabled(false);

		// For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btnBrowse);
		buttonPanel.add(btnCompress);

		// Create compression levels radio buttons
		JRadioButton
				lowCompress = new JRadioButton("Low"),
				medCompress = new JRadioButton("Medium (recommended)"),
				highCompress= new JRadioButton("High");

		lowCompress.setActionCommand("cl_low");
		medCompress.setActionCommand("cl_med");
		highCompress.setActionCommand("cl_high");

		lowCompress.addActionListener(this);
		medCompress.addActionListener(this);
		highCompress.addActionListener(this);

		// Sets the default compression level button
		medCompress.setSelected(true);

		// Group the radio button together (no multi select)
		ButtonGroup compressGroup = new ButtonGroup();
		compressGroup.add(lowCompress);
		compressGroup.add(medCompress);
		compressGroup.add(highCompress);

		// Add the radio button to another panel
		JPanel compressionLevelPanel = new JPanel();
		compressionLevelPanel.add(lowCompress);
		compressionLevelPanel.add(medCompress);
		compressionLevelPanel.add(highCompress);

		// Add everything to frame
		add(buttonPanel, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.CENTER);
		add(compressionLevelPanel, BorderLayout.AFTER_LAST_LINE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("Browse")) {
			// Set up the file chooser.
			if (fc == null) {
				fc = new JFileChooser(System.getProperty("user.home") +"/Desktop");
				// Accept only specific file types
				fc.addChoosableFileFilter(new ImgFilter());
				fc.setAcceptAllFileFilterUsed(false);
				// Allow multiple files to be selected
				fc.setMultiSelectionEnabled(true);
			}

			// pops up dialog
			int status = fc.showDialog(ImageCompressorGui.this, "Select file(s)");

			// Process the results.
			if (status == JFileChooser.APPROVE_OPTION) {
				btnCompress.setEnabled(true);
				File[] files = fc.getSelectedFiles();
				filePaths = new String[files.length];
				for (int i = 0; i <filePaths.length; i++) {
					filePaths[i] = files[i].getAbsolutePath();
				}
				log.append(files.length + " files selected\n");
			}
			log.setCaretPosition(log.getDocument().getLength());

			// Reset the file chooser for the next time it's shown.
			fc.setSelectedFile(null);
			fc.setSelectedFiles(null);
		} else if (e.getActionCommand().startsWith("cl_")) {
			switch (e.getActionCommand().toLowerCase()) {
				case "cl_low":
					imageCompressor.setCompressionFactor(ImageCompressor.LOW_COMPRESSION_FACTOR);
					break;
				case "cl_high":
					imageCompressor.setCompressionFactor(ImageCompressor.HIGH_COMPRESSION_FACTOR);
					break;
				case "cl_med":
				default:
					imageCompressor.setCompressionFactor(ImageCompressor.MED_COMPRESSION_FACTOR);
					break;
			}
			log.append("Compression factor set to "+e.getActionCommand()+"\n");
		} else if (e.getActionCommand().equalsIgnoreCase("Compress")) {
			// Run on a different thread so the GUI doesn't freeze
			(new Thread() {
				public void run() {
					log.append("\n=== COMPRESSION START ===\n");
					for (int i = 0; i<filePaths.length; i++) {
						log.append("Compressing "+filePaths[i]+" ...\n");
						try {
							String result = imageCompressor.compressJPG(filePaths[i]);
							log.append(result+"\n");
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						// always stay at bottom of log
						log.setCaretPosition(log.getDocument().getLength());
					}
					log.append("\n=== COMPRESSION END ===\n");
				}
			}).start();
		} else {
			log.append("Invalid command.\n");
		}
	}


	public static void main(String[] args) {
		JFrame frame = new JFrame("Image Compressor");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(new ImageCompressorGui());
		frame.pack();
		frame.setVisible(true);
	}

}
