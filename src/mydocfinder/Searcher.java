package mydocfinder;

import java.io.File;
import java.util.Date;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;

public class Searcher {
	private final static String INDEX_PATH = "./data/index/";
	private final static String FIELD_CONTENTS = "contents";

	public Vector<SearchResult> searchIndex(String instring, String indexNum) {
		Vector<SearchResult> res = new Vector<SearchResult>();
		
		try {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(INDEX_PATH + indexNum)));

		System.out.println("Searching for ' " + instring + " '");
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new KoreanAnalyzer(Version.LUCENE_43);
		QueryParser queryParser = new QueryParser(Version.LUCENE_43, FIELD_CONTENTS, analyzer);
		Query query = queryParser.parse(instring);
		TopDocs hits = searcher.search(query, 100); // 최대 100개의 결과를 반환
		ScoreDoc[] document = hits.scoreDocs;

		System.out.println("파일 내용 검색 결과 : " + hits.totalHits);
		for (int i = 0; i < document.length; i++) {
			Document doc = searcher.doc(document[i].doc);
			String filePath = doc.get("fullPath");
			System.out.println(filePath);
			
			File file = new File(filePath);
				
			res.add(new SearchResult(file.getName(),new Date(file.lastModified()), Double.toString((double)file.length()/1024.0), filePath.substring(filePath.lastIndexOf('.')+1), filePath));
		}
		}catch(Exception e) {}
		
		return res;
	}

	public static void main(String args[]) throws Exception {
		Searcher s = new Searcher();
	}
}