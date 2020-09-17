package mydocfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;

public class Indexer {
	private final static String ENCODING_TYPE = "UTF-8";
	private final boolean isNew = true; // 신규 생성여부

	private IndexWriter writer = null;
	private File indexDirectory = null;
	private String fileContent; // text 파일로 파싱한 결과를 임시 저장하는 변수

	public void createIndex(String filePath, String indexPath) throws FileNotFoundException, CorruptIndexException, IOException {
		try {
			long start = System.currentTimeMillis();
			createIndexWriter(indexPath); // 인덱스 객체 오픈
			checkFileValidity(new File(filePath)); // 문서 파싱및 인덱스에 적재
			writer.close();
			long end = System.currentTimeMillis();
			System.out.println("전체 인덱싱된 문서 수 : " + TotalDocumentsIndexed());
			System.out.println("소요 시간 : " + (end - start) / (100 * 60));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("작업 처리 실패");
		}
	}

	private void createIndexWriter(String indexPath) throws IOException {
		indexDirectory = new File("./data/index/" + indexPath);
		if (!indexDirectory.exists()) {
			indexDirectory.mkdir();
		}
		FSDirectory dir = FSDirectory.open(indexDirectory);

		// 한글 형태소 분석기 사용
		Analyzer analyzer = new KoreanAnalyzer(Version.LUCENE_43);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);

		if (isNew) {
			// 신규 인덱스 생성(기존 생성된 인덱스 삭제)
			config.setOpenMode(OpenMode.CREATE);
		} else {
			// 기존 생성된 인덱스에 추가
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}

		writer = new IndexWriter(dir, config);
	}

	private void checkFileValidity(File file) {
		File[] files = file.listFiles();

		for (int i = 0; i < files.length; i++) {
			try {
				if (files[i].isDirectory()) { // ### 디렉토리일 경우 해당 디렉토리를 탐색 ###
					checkFileValidity(files[i]); // 재귀 호출
				} else if (!files[i].isHidden() && files[i].exists() && files[i].canRead() && files[i].length() > 0.0
						&& files[i].isFile()) { // ### 파일인 경우 문서 파싱작업 ###
					if (files[i].getName().endsWith(".txt") || files[i].getName().endsWith(".htm")
							|| files[i].getName().endsWith(".html") || files[i].getName().endsWith(".xml")) {
						indexTextFiles(files[i]); // txt, htm, html, xml 파일의 텍스트 추출
						System.out.println("인덱싱 성공 : " + files[i].getAbsolutePath());
					} else if (files[i].getName().endsWith(".doc") || files[i].getName().endsWith(".ppt")
							|| files[i].getName().endsWith(".xls") || files[i].getName().endsWith(".docx")
							|| files[i].getName().endsWith(".pptx")|| files[i].getName().endsWith(".xlsx")
							|| files[i].getName().endsWith(".pdf") || files[i].getName().endsWith(".hwp")) {
						StartIndex(files[i]); // document 파일들의 텍스트 추출
					}
				}
			} catch (Exception e) {
				System.out.println("인덱싱 실패 : " + files[i].getAbsolutePath());
			}
		}
	}

	// 파일 내용 파싱 & 추출
	private void StartIndex(File file) throws FileNotFoundException, CorruptIndexException, IOException {
		fileContent = null;
		try {
			Document doc = new Document();
			if (file.getName().endsWith(".pdf")) {
				fileContent = new PdfFileParser().PdfFileContentParser(file.getAbsolutePath());
			}

			if (file.getName().endsWith(".doc") || file.getName().endsWith(".ppt") || file.getName().endsWith(".xls")) {
				fileContent = new DocFileParser().DocFileContentParser(file.getAbsolutePath());
			}

			if (file.getName().endsWith(".docx") || file.getName().endsWith(".pptx")
					|| file.getName().endsWith(".xlsx")) {
				fileContent = new DocxFileParser().docxFileContentParser(file.getAbsolutePath());
			}
			if (file.getName().endsWith(".hwp")) {
				fileContent = new HwpFileParser().HwpFileContentParser(file.getAbsolutePath());
			}

			// 필드 타입 설정
			FieldType fieldType = new FieldType();
			fieldType.setIndexed(true); // 인덱스 포함 여부
			fieldType.setStored(true); // 검색에 출력 여부
			fieldType.setTokenized(true); // 필드 토큰화 여부
			fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);

			/*
			 * FieldType - IndexOptions ### DOCS_AND_FREQS : 문서와 용어 빈도를 포함, 위치 생략
			 * DOCS_AND_FREQS_AND_POSITIONS : 문서와 용어 빈도, 위치값을 포함
			 * DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS : 문서와 용어 빈도, 위치값, 오프셋 포함 (# PDF 인덱싱
			 * 오류 발생) DOCS_ONLY : 문서만 색인, 용어 빈도, 위치 생략
			 */
			doc.add(new StringField("fileName", file.getName(), Field.Store.YES));
			doc.add(new StringField("fullPath", file.getAbsolutePath(), Field.Store.YES));
			doc.add(new Field("contents", fileContent, fieldType)); // 내용

			if (doc != null) {
				writer.addDocument(doc);
			}
			System.out.println("인덱스 성공 : " + file.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("인덱싱 실패 : " + (file.getAbsolutePath()));
		}
	}

	// txt, htm, html, xml 파일의 내용을 파싱및 추출
	private void indexTextFiles(File file) throws CorruptIndexException, IOException {
		Document doc = new Document();
		fileContent = null;

		BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING_TYPE));

		while (true) {
			String s = buff.readLine();
			if (s == null)
				break;
			fileContent += s + ' ';
		}

		System.out.println(fileContent);
		buff.close();

		// txt파일 이외의 문서(htm,html,xml)는 모든 태그형식(<로 시작, >로 끝나는)을 제거
		if (!file.getName().endsWith(".txt")) {
			fileContent = removeTag(fileContent);
		}

		FieldType fieldType = new FieldType();
		fieldType.setIndexed(true); // 인덱스 포함 여부
		fieldType.setStored(true); // 검색에 출력 여부
		fieldType.setTokenized(true); // 필드 토큰화 여부

		fieldType.setIndexOptions(IndexOptions.DOCS_ONLY);
		/*
		 * FieldType - IndexOptions ### DOCS_AND_FREQS : 문서와 용어 빈도를 포함, 위치 생략
		 * DOCS_AND_FREQS_AND_POSITIONS : 문서와 용어 빈도, 위치값을 포함
		 * DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS : 문서와 용어 빈도, 위치값, 오프셋 포함 DOCS_ONLY :
		 * 문서만 색인, 용어 빈도, 위치 생략
		 */
		doc.add(new StringField("fileName", file.getName(), Field.Store.YES));
		doc.add(new StringField("fullPath", file.getAbsolutePath(), Field.Store.YES));
		doc.add(new Field("contents", fileContent, fieldType)); // 내용

		if (doc != null) {
			writer.addDocument(doc);
		}
	}

	private int TotalDocumentsIndexed() {
		try {
			IndexReader reader = IndexReader.open(FSDirectory.open(indexDirectory));
			return reader.maxDoc();
		} catch (Exception ex) {
			System.out.println("인덱스 된 문서가 없습니다.");
		}
		return 0;
	}

	private static String removeTag(String s) {
		return s.replaceAll("<[^>]*>", " "); // <> 태그형식을 모두 공백으로 대체
	}

	public static void main(String arg[]) throws Exception {
		//new Indexer().createIndex();
	}
}
