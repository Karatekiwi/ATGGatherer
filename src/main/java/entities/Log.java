package entities;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Represents a log entry
 */
public class Log implements Comparable<Log>{

	private int id;
	private String text;
	private long date;	
	
	public Log(int id, String text, long date) {
		super();
		this.id = id;
		this.text = text;
		this.date = date;
	}
	
	public Log(String text, long date) {
		super();
		this.text = text;
		this.date = date;
	}
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		this.date = date;
	}

	public String getDateString() {
		SimpleDateFormat ft = new SimpleDateFormat ("dd.MM.yyyy HH:mm:ss");
		return ft.format(new Date(date));
	}

	@Override
	public int compareTo(Log o) {
		return ((Integer)(o.getId())).compareTo(this.id);
	}
	
	
	
}
