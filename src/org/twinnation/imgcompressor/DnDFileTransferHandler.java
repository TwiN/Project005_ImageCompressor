package org.twinnation.imgcompressor;


import javax.swing.TransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

class DnDFileTransferHandler extends TransferHandler {
	@Override
	public boolean canImport(TransferSupport support) {
		for (DataFlavor flavor : support.getDataFlavors()) {
			if (flavor.isFlavorJavaFileListType()) {
				return true;
			}
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean importData(TransferSupport support) {
		if (!this.canImport(support)) {
			return false;
		}
		List<File> files;
		try {
			files = (List<File>) support.getTransferable()
					.getTransferData(DataFlavor.javaFileListFlavor);
		} catch (UnsupportedFlavorException | IOException ex) {
			return false;
		}
		// Adds the paths in the list
		for (File f: files) {
			try {
				ImageCompressorGui.dragNdropCompress(f.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
