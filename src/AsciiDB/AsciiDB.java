package AsciiDB;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class AsciiDB {
	public static final String ENTRY_SEPARATOR  = "~";
	public static final String COLUMN_SEPARATOR = "`";
	public static final String REGEX_REPLACE 	 = "["
												+ ENTRY_SEPARATOR
												+ COLUMN_SEPARATOR
												+ "]";
	
	private String dbFolder;	
	private LinkedList<AsciiDBHandler> dbHandler;	
	private boolean initialized;
	
	public AsciiDB(String folderName){
		dbFolder = folderName;
		dbHandler = new LinkedList<AsciiDBHandler>();
		initialized = false;
	}	

	public int RegisterModel(String dbName, String[] columnNames){
		if(!initialized){
			// check parameters
			if(dbName.isEmpty() || columnNames.length < 1)
				throw new IllegalArgumentException("AsciiDB: You need a real database"
						+ " name and at least one column name");
			// create new database handler
			int databaseId = dbHandler.size();
			dbHandler.add(new AsciiDBHandler(dbName, columnNames));
			System.out.println("AsciiDB: New Database registered! ID=" + databaseId
					+ " Name=" + dbName);
			return databaseId;
		}
		System.out.println("AsciiDB: Could not register new Database anymore. "
				+ "AsciiDB already initialized.");
		return -1;
	}
	
	public void Initialize() throws IOException{
		// check if already initialized
		if(initialized){
			System.out.println("AsciiDB: Could not initialize. "
					+ "Database already initialized.");
			return;
		}
		
		// check if Databases are registered
		if(dbHandler.size() < 1){
			System.out.println("AsciiDB: Could not initialize. "
					+ "No Databases are registered.");
			return;
		}
		
		// create Folder Handler and check if it exists
		File folder = new File(dbFolder);
		if(!folder.exists() || !folder.isDirectory()){
			// try to create folder
			if(!folder.mkdirs())
				throw new IOException("AsciiDB: Could not create DB Folder: "
										+ dbFolder);
			else
				System.out.println("AsciiDB: Created Database Folder: "
										+ dbFolder);
		}
		// check if folder is read and writeable
		if(!folder.canWrite() || !folder.canRead()){
			throw new IOException("AsciiDB: Could not read or write to DB Folder: " 
									+ dbFolder);
		}
		// create File Handler for each DB Handler
		for(AsciiDBHandler db : dbHandler){
			File dbFile = new File(dbFolder + File.separator + db.Name);
			
			if(!dbFile.exists() || dbFile.isDirectory()){
				// try to create DB File
				if(!dbFile.createNewFile()){
					throw new IOException("AsciiDB: Could not create DB File: " 
											+ dbFile.getAbsolutePath());
				}
				else
					System.out.println("AsciiDB: Created Database File: "
											+ dbFile.getAbsolutePath());
			}
			// check if DB File is read and writeable
			if(!dbFile.canWrite() || !dbFile.canRead()){
				throw new IOException("AsciiDB: Could not read or write to DB File: " 
										+ dbFile.getAbsolutePath());
			}			
			// save File Handler
			db.FileHandler = dbFile;
			
			// read in database content
			db.Read();

		}
		// Ascii DB is initialized now
		initialized = true;
	}
	
	private AsciiDBHandler getDBHandler(int id){
		if(id < 0 || id >= dbHandler.size())
			throw new IndexOutOfBoundsException("AsciiDB: Wrong id: " + id
					+ ". Must be between 0 and " + dbHandler.size() + ".");
		return dbHandler.get(id);
	}
	
	public List<AsciiDBEntry> GetDatabase(int databaseId){
		return getDBHandler(databaseId).GetEntries();
	}
	
	public AsciiDBEntry CreateEntry(int databaseId){
		if(!initialized)
			throw new IllegalStateException("AsciiDB: Could not Create Entry."
					+ " Need Initialization first!");
		return new AsciiDBEntry(getDBHandler(databaseId));	
	}
	
	public void DumpDatabase(int databaseId){
		getDBHandler(databaseId).Dump();
	}
}
