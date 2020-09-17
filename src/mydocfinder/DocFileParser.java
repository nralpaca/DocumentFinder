package mydocfinder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class DocFileParser {

	public String DocFileContentParser(String fileName) throws FileNotFoundException, IOException {
		POIFSFileSystem fs = null;
		fs = new POIFSFileSystem(new FileInputStream(fileName));

		if (fileName.endsWith(".doc")) {
			HWPFDocument doc = new HWPFDocument(fs);
			WordExtractor we = new WordExtractor(doc);
			return we.getText();
		} else if (fileName.endsWith(".xls")) {
			ExcelExtractor ex = new ExcelExtractor(fs);
			ex.setFormulasNotResults(true);
			ex.setIncludeSheetNames(true);
			return ex.getText();
		} else if (fileName.endsWith(".ppt")) {
			PowerPointExtractor extractor = new PowerPointExtractor(fs);
			return extractor.getText();
		}
		return "";
	}
	
}
