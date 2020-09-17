package mydocfinder;

import java.util.Date;

public class SearchResult {
	String name;
	Date date;
	String size;
	String type;
	String path;
	
	public SearchResult(String name, Date date, String size, String type, String path) {
		this.name = name;
		this.date = date;
		this.size = size;
		this.type = type;
		this.path = path;		
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
}
