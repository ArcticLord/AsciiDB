package AsciiDB;

import java.io.IOException;


public class AsciiDBEntry {
	private String[] entry;
	private AsciiDBHandler handler;
	private boolean newEntry;
	
	public AsciiDBEntry(AsciiDBHandler handler){
		this.handler = handler;
		this.entry = new String[handler.Columns.length];
		this.newEntry = true;
		// initialize fields
		for(int i = 0; i < entry.length; i++)
			entry[i] = "";
	}
	
	public AsciiDBEntry(AsciiDBHandler model, String[] entry){
		this.handler = model;
		this.entry = entry;
		this.newEntry = false;
	}
	
	public String Get(String columnName){
		int id = handler.GetColumnId(columnName);
		if (id != -1)
			return entry[id];
		return "";
	}
	
	public void Put(String columnName, String data){
		// find column and put in data
		// replace entry and column seperator characters
		int id = handler.GetColumnId(columnName);
		if (id != -1)
			entry[id] = data.replaceAll(AsciiDB.REGEX_REPLACE," ");
	}
	
	@Override
	public String toString(){
		String result = "";
		for(String row : entry)
			result += row + "\t";
		return result;
	}
	
	public void Save() throws IOException{
		if(newEntry){
			handler.SaveEntry(this);
			newEntry = false;			
		} else {
			System.out.println("AsciiDB: Could not save. "
					+ "Entry was already in Database.");
		}
	}
	
	public String pack(){
		String result = "";
		for(int i = 0; i < entry.length - 1; i++){
			result += entry[i] + AsciiDB.COLUMN_SEPARATOR;
		}
		result += entry[entry.length - 1] + AsciiDB.ENTRY_SEPARATOR;
		return result;				
	}
}
