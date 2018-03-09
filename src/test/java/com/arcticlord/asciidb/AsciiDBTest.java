package com.arcticlord.asciidb;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


import java.util.List;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AsciiDBTest{
	
	// content of an dummy database for the tests
	private static final String ES = AsciiDB.ENTRY_SEPARATOR;
	private static final String CS = AsciiDB.COLUMN_SEPARATOR;
	private static final String DB_FILE_CONTENT = 
			"10" + CS + "20" + CS + "30" + ES +
			"11" + CS + "21" + CS + "31" + ES +
			"12" + CS + "22" + CS + "32" + ES +
			"13" + CS + "23" + CS + "33" + ES +
			"14" + CS + "24" + CS + "34" + ES;
	
	/*
	 * A TemporaryFolder with @Rule gets created for each single test and
	 * deleted afterwards. We need this for the create test.
	 * A TemporaryFolder with @ClassRule is created before the first test
	 * and is deleted after the last one. We need it here for all the other
	 * tests that do some operations on an dummy database created in the 
	 * before method.
	 */
	
	@Rule
	public TemporaryFolder createTestFolder = new TemporaryFolder();
	
	@ClassRule
	public static TemporaryFolder readTestFolder = new TemporaryFolder();	
	
	/**
	 * Creates a dummy database as file that can be used
	 * in all tests.
	 */
	@Before
	public void initializeDummyDB(){
		File file =  new File(readTestFolder.getRoot().getAbsolutePath() + 
				File.separator + "test");
		try {
			FileWriter fileWriter = new FileWriter(file, false);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			writer.write(DB_FILE_CONTENT);
			writer.close();
		} catch (IOException e) {
			Assert.fail("Could not create dummy database file. " 
					+ e.getMessage());
		}
	}
	
	/**
	 * Creates a simple database, puts in some entries and
	 * then checks the created content of the file. 
	 */
	@Test
	public void createTest() {		
		AsciiDB adb = new AsciiDB(createTestFolder.getRoot().getAbsolutePath());
		int dbid = adb.RegisterModel("test", new String[] {"one", "two", "three"});
		try {
			adb.Initialize();
		} catch (IOException e) {
			Assert.fail("Could not create database file. " 
					+ e.getMessage());
		}
		for(int i = 0; i < 5; i++){
			AsciiDBEntry newEntry = adb.CreateEntry(dbid);
			
			newEntry.Put("one", "1" + i);
			newEntry.Put("two", "2" + i);
			newEntry.Put("three", "3" + i);
			
			try {
				newEntry.Save();
			} catch(IOException e){
				Assert.fail("Could not append entry to database file. " 
						+ e.getMessage());
			}
		}
		assertTrue("Database file content is not as expected.", 
				checkDBFile( new File (createTestFolder.getRoot().getAbsolutePath()
						+ File.separator + "test")));
	}
	
	/**
	 * Loads the dummy database and checks if its complete means
	 * if we have five entries in it.
	 */
	@Test
	public void readTest(){		
		AsciiDB adb = new AsciiDB(readTestFolder.getRoot().getAbsolutePath());
		int dbid = adb.RegisterModel("test", new String[] {"one", "two", "three"});
		try {
			adb.Initialize();
		} catch (IOException e) {
			Assert.fail("Dummy database file could not be loaded. " 
					+ e.getMessage());
		}
		int count = adb.GetAllEntries(dbid).size();
		
		assertEquals(5, count);		
	}
	
	/**
	 * Loads the dummy database and checks if we can find
	 * a special entry in it.
	 */
	@Test
	public void findTest(){
		AsciiDB adb = new AsciiDB(readTestFolder.getRoot().getAbsolutePath());
		int dbid = adb.RegisterModel("test", new String[] {"one", "two", "three"});
		try {
			adb.Initialize();
		} catch (IOException e) {
			Assert.fail("Dummy database file could not be loaded. " 
					+ e.getMessage());
		}
		List<AsciiDBEntry> result = adb.FindEntries(dbid, "two", "22");
		assertEquals(1, result.size());
		assertEquals("12", result.get(0).Get("one"));
		assertEquals("32", result.get(0).Get("three"));
	}
	
	/**
	 * Opens database file and checks if its
	 * content is correct.
	 * @param file File of the dummy database
	 * @return True if file content is correct
	 */
	private boolean checkDBFile(File file){
		Scanner scanner = null;
		String content = "";
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			Assert.fail("Database file could not be found. " 
					+ e.getMessage());
		}
		while(scanner.hasNext()) {
			content += scanner.next();
		}
		scanner.close();
		return content.equals(DB_FILE_CONTENT);
	}
}
