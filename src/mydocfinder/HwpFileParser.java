package mydocfinder;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.textextractor.TextExtractMethod;
import kr.dogfoot.hwplib.textextractor.TextExtractor;

public class HwpFileParser {
		
    public String HwpFileContentParser(String filePath) throws Exception{
    	String content;

    	HWPFile file = HWPReader.fromFile(filePath);
    	
    	content= new TextExtractor().extract(file, TextExtractMethod.OnlyMainParagraph);
    	
        return content;
    }
}
