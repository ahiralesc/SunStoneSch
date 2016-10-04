import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.LinkedHashMap;



public class CVS2TXT {
	
	private String inputDir = "C:\\Users\\ahiralesc\\Documents\\Results\\WorkflowData010411\\SmallWorkload\\EASY\\CPOP\\"; 
	private String outPutDir = "C:\\workloads\\CPOP\\";
	
	
	/**
	 * The source directory.
	 * 
	 * The source directory must be organized in a set of sub-directories, each named after a strategy.
	 * For example:
	 * 
	 * sourceDir -->  	MaxAR	--> site0-1.cvs, site0-2.cvs, ..., site0-30.cvs
	 * 					MLp		--> site0-1.cvs, site0-2.cvs, ..., site0-30.cvs
	 * 					MPL		--> site0-1.cvs, site0-2.cvs, ..., site0-30.cvs
	 * 
	 * A strategy named sub-directory contains CVS type files. Such files, are produced
	 * by the tGSF (Teikoku Grid Scheduling Framework).
	 */
	//private String siteRootDir = "C:\\Users\\ahiralesc\\Documents\\Results\\ParallelData_08092014";
		
	//private String siteRootDir = "C:\\Users\\ahiralesc\\Documents\\Research\\Analysis\\ParallelData07032016";
	private String siteRootDir = "C:\\Users\\ahiralesc\\Documents\\Research\\Analysis\\ParallelData09212016";
	
	/**
	 * The destination directory.
	 * 
	 * A set of strategy named sub-directories are created in the destination directory. 
	 * 
	 * Assuming that during the simulation metrics A1, A2, and A3 were instrumented, then
	 * each strategy named sub-directory will contain files A1.TXT, A2.TXT, and A3.TXT built 
	 * by concatenating files site0-1.cvs, site0-2.cvs, ..., site0-30.cvs obtained form the 
	 * strategy named sub-directory source directory. For example, 
	 * 
	 * targetDir -->  	MaxAR	--> A1.TXT, A2.TXT, A3.TXT
	 * 					MLp		--> same files as above, but from different source sub-directory
	 * 					MPL		--> same files as above, but from different source sub-directory
	 * 
	 */
	// String siteDestDir = "C:\\Users\\ahiralesc\\Documents\\Results\\PFFP_Analysis_08092014\\Data";
	// String siteDestDir = "C:\\Users\\ahiralesc\\Documents\\Research\\Analysis\\ParallelDataAnalysis07132015\\data07032016";
	String siteDestDir = "C:\\Users\\ahiralesc\\Documents\\Research\\Analysis\\ParallelDataAnalysis09212016";

	/**
	 * The metric header loader
	 */
	private boolean gridMetricHeaderLoaded;
	
	
	/**
	 * The workload header
	 */
	private Vector<String> gridMetricHeaders;
	
	
	
	/**
	 * Class constructor
	 */
	CVS2TXT() {
		gridMetricHeaderLoaded = false;
		gridMetricHeaders = new Vector<String>();
	}
	
	
	/* (no javadoc) */
	public static void main(String args[]) {
		CVS2TXT filter = new CVS2TXT();
		//filter.transformSiteFiles();
		filter.transformGridFiles();
	}
	

	/**
	 * Scans a directory for a set of sub-directories. It assumes that each sub-directory is named after a strategy name.
	 * Each sub-directory must store SWF formatted logs. 
	 * 
	 * Once a strategy sub-directory has been built. The method calls <code> transformGridFiles </code> to translate 
	 * SWF formated logs into a single TXT file. Each strategy TXT file contains the last line of each SWF log in 
	 * the sub-directory.  
	 */
	public void transformGridFiles() {
		
		String dirs[] = this.listSubdirectories(this.siteRootDir);
		
		for(String subDir : dirs) {
				// Prepares the input and output sub-directories
				String inputFiles = siteRootDir + "\\" + subDir + "\\";
				String outputFileDir = siteDestDir + "\\" + subDir + "\\";
				
				//Create the target directory
				boolean success = (new File(outputFileDir)).mkdirs(); 
				if (!success) { 
					System.exit(0); 
				}
				
				this.transformSiteFiles(inputFiles, outputFileDir);
			//}
		}
	}
	
	
		
	/**
	 * Produces a set of TXT files given a set of CVS files
	 */
	private void transformSiteFiles(String inputFiles, String outputFileDir) {
		Hashtable<Number,Vector<Number>> allData = new Hashtable<Number,Vector<Number>> ();
		String[] listing = getFileListing(inputFiles);
		
		try {
				// Read all values from source files
				for(int i=0; i<listing.length; i++) {
					String bufferedLine = this.tail(inputFiles, listing[i]);
					StringTokenizer lineParser = new StringTokenizer(bufferedLine);
					Vector<Number> values = new Vector<Number>();
					// Parse values in order
					while(lineParser.hasMoreElements()) 
						values.add(Double.valueOf(lineParser.nextToken().trim()));					
					allData.put(i, values);
				}// End for
				
		
				// Write read values to destination files
				for(int i=0; i<this.gridMetricHeaders.size(); i++) {
					String[] gridFileHeader = this.getGridHeader(1, gridMetricHeaders.elementAt(i));
					String targetFileName = outputFileDir + gridMetricHeaders.elementAt(i) + ".txt";
					PrintWriter writter = new PrintWriter(new BufferedWriter(new FileWriter(targetFileName)));
					
					writter.println(gridFileHeader[0]);
					writter.println(gridFileHeader[1]);
					writter.println(gridFileHeader[2]);
					writter.println(gridFileHeader[3]);
					writter.println();
					
					// Write the data to separate files label according to the metric name
					for(int j=0; j<listing.length; j++) {
						Vector<Number> values = allData.get(j);
						Number val = values.elementAt(i);
						writter.println(val.toString());
					}//End for
					writter.flush();
					writter.close();
				}// End for
		
		}catch(IOException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		} // catch
		
	} // transformGridFiles
	
	
	/**
	 * Creates an array with the names of the files and sub-directories in
	 * a target directory.
	 *  
	 * @param subDir, the target directory
	 * @return an array holding the name of the files and sub-directories 
	 */
	private String[] listSubdirectories(String subDir) {
		File dir = new File(subDir); 
		String[] subDirs = dir.list();
		return subDirs;
	}
	
	
	/**
	 * Transforms CVS site files to TXT formatted files
	 * 
	 */
	private void transformSiteFiles() {
		String[] dirListing = getFileListing(this.siteRootDir);
		LinkedHashMap<String,Vector<Number>> allData = new LinkedHashMap<String,Vector<Number>>();
		
		
		// For each directory get its file listing
		for(String dirName : dirListing){
			String strategyDir = this.siteRootDir + "\\" + dirName;
			String[] siteListing = getFileListing(strategyDir);
			int numSites = this.getNumberOfColumns(siteListing);
			int numExperiments = getNumberOfFilesPerSite(siteListing, numSites);
			
			for(int siteId=1; siteId<=numSites; siteId++) {
				for(int experimentId=1; experimentId<=numExperiments; experimentId++){
					String path = new String(siteRootDir +"\\" + dirName + "\\");
					String fileName = new String("site" + siteId + "-" + experimentId + ".csv");
					try {
						// Read once the number of headings within the files
						String bufferedLine = this.tail(path, fileName);
						StringTokenizer lineParser = new StringTokenizer(bufferedLine);
						Vector<Number> values = new Vector<Number>();
						// Parse values in order
						while(lineParser.hasMoreElements()) 
							values.add(Double.valueOf(lineParser.nextToken().trim()));
						allData.put(fileName,values);
					
					}catch(IOException e) {
						System.out.println(e.getMessage());
						System.exit(0);
					} // catch
				}//End for
			}// End for
			
			// Create the target subdirectory; all ancestor directories must exist 
			boolean success = ( new File(siteDestDir + dirName)).mkdir(); 
			if (!success) {  
				System.out.println("Target directory could not be created.");
				System.exit(0);
			}
			
			try {
				// Write values to permanent storage, for each metric
				for(int i=0; i<this.gridMetricHeaders.size(); i++) {
				
					String[] siteFileHeader = this.getSiteHeader(1, gridMetricHeaders.elementAt(i), numSites);
					String targetFileName = this.siteDestDir + dirName + "\\" + gridMetricHeaders.elementAt(i) + "_node.txt";
					PrintWriter writter = new PrintWriter(new BufferedWriter(new FileWriter(targetFileName)));
				
					// Write the site file header
					writter.println(siteFileHeader[0]);
					writter.println(siteFileHeader[1]);
					writter.println(siteFileHeader[2]);
					writter.println(siteFileHeader[3]);
					writter.println();
				
					double[] data = new double[numSites];
					
					for(int experimentId=1; experimentId<=numExperiments; experimentId++) {
						for(int siteId=1; siteId<=numSites; siteId++) {
							String indexName = new String("site" + siteId + "-" + experimentId + ".csv");
							Vector<Number> value = allData.get(indexName);
							data[siteId-1] = value.elementAt(i).doubleValue();
						} //End for
							
						for(int k=0; k<data.length; k++ )
							writter.print(data[k] + " ");
						writter.println();
					}//End for
					
					writter.flush();
					writter.close();
				}// End for 
			}catch(IOException e) {
				System.out.println(e.getMessage());
				System.exit(0);
			} // catch
		}// End for
	}
	
	
	
	/**
	 * Gets the number of columns  
	 * 
	 * @param siteListing
	 * @return
	 */
	private int getNumberOfColumns(String[] siteListing) {
		int maxIntVal = -1;
		
		for(int i=1; i< siteListing.length; i++) {
			int index2 = siteListing[i].lastIndexOf("-");
			String str = siteListing[i].substring(4, index2);
			int number = new Integer(str).intValue();
			if(number > maxIntVal)
				maxIntVal = number;
		}
		
		return maxIntVal;
	}
	
	
	
	
	/**
	 * Get the number of files per experiment
	 * 
	 * @return
	 */
	private int getNumberOfFilesPerSite(String[] siteListing, int n) {
		int total = siteListing.length;
		int num = total / n;
		return num;
	}
	
	
	

	/**
	 * Gets the site header
	 * 
	 * @param type, the minimization or maximization criteria
	 * @param strategy, the name of the strategy
	 * @param numCols, the number of columns in the strategy
	 * @return  the header for the site strategy
	 */
	private String[] getSiteHeader(int type, String strategy, int numCols ) {
		String[] siteHeader = new String[4];
		
		// Type 1 equals minimization
		if(type == 1) {
			siteHeader[0] = "; Main";
			siteHeader[1] = "; Minimize";
			siteHeader[2] = "; " + strategy;
			siteHeader[3] = new Integer(numCols).toString(); 
		}
		
		//Type 2 equals maximization
		if(type == 2) {
			siteHeader[0] = "; Main";
			siteHeader[1] = "; Maximize";
			siteHeader[2] = "; " + strategy;
			siteHeader[3] = new Integer(numCols).toString();
		}
		
		return siteHeader;
	}
	
	
	
	/**
	 * Gets the Grid header
	 * 
	 * @param type, specifies if the strategy objective is to minimize or maximize
	 * @param strategy, the name of the strategy
	 * 
	 * @return
	 */
	private String[] getGridHeader(int type, String strategy) {
		String[] gridHeader = new String[4];
		
		// Type 1 equals minimization
		if(type == 1) {
			gridHeader[0] = "; Main";
			gridHeader[1] = "; Minimize";
			gridHeader[2] = "; " + strategy;
			gridHeader[3] = " 1";
		}
		
		//Type 2 equals maximization
		if(type == 2) {
			gridHeader[0] = "; Main";
			gridHeader[1] = "; Maximize";
			gridHeader[2] = "; " + strategy;
			gridHeader[3] = " 1";
		}
		
		return gridHeader;
	}
	
	
	
	/**
	 * Get the last line from the specified file
	 * 
	 * @param path	the path to the file
	 * @param fileName	the name of the text file
	 * @return	a string holding the last line
	 * 
	 * @throws IOException
	 */
	private String tail(String path, String fileName) throws IOException {
		BufferedReader bfReader = new BufferedReader( new FileReader( path + fileName ) );
		
		String currLine = "";
		String lastLine	= "";

		// Initialize header list with metric names
		if(!gridMetricHeaderLoaded) {
			currLine = bfReader.readLine();
			StringTokenizer parser = new StringTokenizer(currLine);
			while(parser.hasMoreTokens())
				gridMetricHeaders.add(parser.nextToken().trim());
			gridMetricHeaderLoaded = true;
		}//End if
		
		// Get the last line of the trace file
		while (( currLine = bfReader.readLine()) != null)
			lastLine = currLine;	
		bfReader.close();	
		
		return lastLine;
	}

	
	
	/**
	 * Gets a listing of files in the target working directory
	 * 
	 * @param workingDirectory
	 * @return the file listing
	 */
	private String[] getFileListing(String workingDirectory) {
		File directory = new File(workingDirectory);
		String[] listing = directory.list();
		
		if(listing == null) {
			System.err.println("No files provided in working directory");
			System.exit(0);
		} 
				
		return listing;
	} //End getFileListing
	
} //End class
