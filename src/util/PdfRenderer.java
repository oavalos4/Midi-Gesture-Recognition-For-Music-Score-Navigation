package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class PdfRenderer {

	public static Image renderPdfPage(String path, int pageNumber) {

	    try {
	        File file = new File(path);
	        PDDocument document = Loader.loadPDF(file);

	        PDFRenderer renderer = new PDFRenderer(document);

	        BufferedImage bufferedImage = renderer.renderImageWithDPI(pageNumber, 250);

	        document.close();

	        return SwingFXUtils.toFXImage(bufferedImage, null);

	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return null;
	}
    public static int getPageCount(String path) {

        try {
            File file = new File(path);
            PDDocument document = Loader.loadPDF(file);

            int pageCount = document.getNumberOfPages();

            document.close();

            return pageCount;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
}