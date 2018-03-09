package com.arcticlord.asciidb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

class AsciiDBHandler {
	String Name;
	String[] Columns;
	File FileHandler;
	private LinkedList<AsciiDBEntry> entries;

	AsciiDBHandler(String dbName, String[] columnNames){
		Name = dbName;
		Columns = columnNames;
		FileHandler = null;
		entries = new LinkedList<AsciiDBEntry>();
	}
	
	int GetColumnId(String columnName){
		for(int i = 0; i < Columns.length; i++){
			if(columnName.equals(Columns[i]))
				return i;
		}
		System.out.println("AsciiDB: Could not find column with Name "
				+ columnName + " in Database " + Name);
		return -1;
	}
	
	void SaveEntry(AsciiDBEntry entry) throws IOException{
		// append to DB File
		FileWriter fileWriter = new FileWriter(FileHandler, true);
	    BufferedWriter writer = new BufferedWriter(fileWriter); 
	    writer.write(entry.pack());
	    writer.close();
	    // and add to entry list
	    this.entries.add(entry);	    
	}
	
	List<AsciiDBEntry> GetEntries(){
		return entries;
	}
	
	List<AsciiDBEntry> FindEntries(String columnName, String value){
		int colId = GetColumnId(columnName);
		List<AsciiDBEntry> result = new LinkedList<AsciiDBEntry>();
		if(colId != -1){
			for(AsciiDBEntry entry : entries)
				if(entry.Get(colId).equals(value))
					result.add(entry);
		}
		return result;
	}
	
	// Throws FileNotFoundException but this method is
	// called in Initialize only which itself throws IOExceptions
	// and FileNotFoundException is subclass of IOException.
	// So no one cares.
	// FileNotFoundException should never happen anyway because
	// Initialize checks existence of all Files first before
	// calling Read.
	void Read() throws FileNotFoundException{			
		// use scanner with ENTRY SEPERATOR as delimiter
		// to read each packed entry from database file
		Scanner scanner = new Scanner(FileHandler, "UTF-8");		
		scanner.useDelimiter(AsciiDB.ENTRY_SEPARATOR);
		while(scanner.hasNext()) {
			String packedEntry = scanner.next();
			// split each packed entry in data with column separator
			String[] entryData = packedEntry.split(AsciiDB.COLUMN_SEPARATOR);
			// check consistence with database model
			if(entryData.length == Columns.length){
				// add entry to list
				AsciiDBEntry entry = new AsciiDBEntry(this, entryData);
				entries.add(entry);
			}
			else{
				System.out.println("AsciiDB: Could not unpack Entry " 
						+ packedEntry + " for Database " + Name);
			}
		}
		scanner.close();
	}
	
	void Dump(){
		System.out.println("Dump Ascii Database with Name: " + Name);
		String tableHead = "";
		for(String column : Columns)
			tableHead += column + "\t";
		System.out.println(tableHead);
		for(AsciiDBEntry entry : entries)
			System.out.println(entry);
	}
}
