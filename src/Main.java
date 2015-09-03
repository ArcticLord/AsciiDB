import java.io.IOException;

import AsciiDB.AsciiDB;
import AsciiDB.AsciiDBEntry;


public class Main {
	public static void main(String... args){

		System.out.println("Ascii Database Test");
		
		// Create new Instance of Ascii Database
		AsciiDB adb = new AsciiDB("C:\\asciiDB\\databases");
		// Register a model of the database setup
		int testDBid = adb.RegisterModel("test", new String[] {"id", "author", "comment"});
		
		// Initialze Ascii Database
		try {
			adb.Initialize();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		// Dump the full content
		adb.DumpDatabase(testDBid);
		
		// Create some Entries
		for(int i = 0; i < 100; i++){
			AsciiDBEntry newEntry = adb.CreateEntry(testDBid);
			
			newEntry.Put("id", "" + i);
			newEntry.Put("author", "author" + i);
			newEntry.Put("comment", "" + (100 - i) + " bottles of beer on the wall!");
			
			try {
				newEntry.Save();
			} catch(IOException e){
				e.printStackTrace();
				return;
			}
		}
		
		// Dump the full content again
		adb.DumpDatabase(testDBid);
		
		// and search specific entry
		for(AsciiDBEntry entry : adb.GetDatabase(testDBid)){
			if("42".equals(entry.Get("id"))){
				System.out.println("Found it! " + entry);
			}
		}
	}
}
