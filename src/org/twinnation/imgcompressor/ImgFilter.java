package org.twinnation.imgcompressor;


import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImgFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		return f.getName().lastIndexOf(".jpg")>=0
				|| f.getName().lastIndexOf(".jpeg")>=0
				|| f.isDirectory();
	}

	@Override
	public String getDescription() {
		return "jpg files";
	}
}
