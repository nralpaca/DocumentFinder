package mydocfinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;

public class DocxFileParser {

	public String docxFileContentParser(String fileName) throws IOException, XmlException, OpenXML4JException {
		FileInputStream fs = new FileInputStream(new File(fileName));
		OPCPackage d = OPCPackage.open(fs);
		
		if (fileName.endsWith(".docx")) {
			XWPFWordExtractor xw = new XWPFWordExtractor(d);
			return xw.getText();
		} else if (fileName.endsWith(".pptx")) {
			XSLFPowerPointExtractor xp = new XSLFPowerPointExtractor(d);
			return xp.getText();
		} else if (fileName.endsWith(".xlsx")) {
			XSSFExcelExtractor xe = new XSSFExcelExtractor(d);
			xe.setFormulasNotResults(true);
			xe.setIncludeSheetNames(true);
			return xe.getText();
		}
		return "";
	}
	
}
