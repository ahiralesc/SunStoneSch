// $Id$

/*
 * "Teikoku Workflow Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2007 by the:
 *   Departamento de Ciencias Computacionales,
 *   Centro de Investigacon Cientifica y de Educacion Superior de Ensenada (CICESE),
 *   Mexico
 *    
 *   Facultad de Ciencias, 
 *   Ciencias Computacionales,
 *   Universidad Autonoma de Baja California (UABC), 
 *   Mexico
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *   Free Software Foundation, Inc.,
 *   51 Franklin St, Fifth Floor,
 *   Boston, MA 02110, USA
 */

package mx.cicese.dcc.teikoku.workload.swf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.exceptions.WorkloadException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.WorkloadFilter;
import de.irf.it.rmg.core.teikoku.workload.WorkloadSource;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.teikoku.workload.swf.SWFConstants;
import edu.uci.ics.jung.graph.Graph;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import mx.cicese.dcc.teikoku.workload.job.CompositeJobUtils;
import mx.cicese.dcc.teikoku.workload.job.Precedence;



public class CSWFParser implements WorkloadSource {
	
	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(CSWFParser.class);

	/**
	 * Holds the path of the source file this parser reads.
	 * 
	 */
	private URL url;

	/**
	 * Holds the reader to access the input file.
	 * 
	 */
	private LineNumberReader trace;

	/**
	 * Holds the current batch length (see {@link #parse()}).
	 * 
	 */
	private long batchLength;

	/**
	 * Holds the filters used to
	 * 
	 */
	private WorkloadFilter[] filters;

	/**
	 * Holds a list of up-to-now already parsed and validated jobs, as a
	 * {@link Queue} view.
	 * 
	 */
	private Queue<Job> jobs;

	/**
	 * Holds a list of up-to-now already parsed and validated jobs, as a
	 * {@link Queue} view.
	 * 
	 */
	private boolean hasReadHeader;
	
		
	
	/**
	 * Creates a new instance of this class.
	 * 
	 */
	private CSWFParser() {
		this.batchLength = 1000;
		this.hasReadHeader = false;
		this.jobs = new LinkedList<Job>();
	}
	

	/**
	 * Creates a new instance of this class, using the given parameters. The
	 * default batch length (see {@link #SWFParser(URL, int)} is set to
	 * <code>100</code>. To overcome this, either use the previously
	 * mentioned constructor or set the appropriate configuration switch (see
	 * {@link SWFParser}).
	 * 
	 * @param source
	 *            The input source {@link URL} of the workload trace.
	 * @throws IOException
	 *             iif an I/O error occurs during source access.
	 * 
	 * @throws IOException
	 */
	public CSWFParser(URL source)
			throws IOException {
		this();
		this.url = source;
		InputStreamReader isr = null;
		if (this.url.getFile().endsWith(".gz")) {
			isr = new InputStreamReader(new GZIPInputStream(this.url
					.openStream()));
		} // if
		else {
			isr = new InputStreamReader(this.url.openStream());
		} // else

		log.debug("using " + this.url
				+ " as the input source for this workload");
		BufferedReader br = new BufferedReader(isr);
		LineNumberReader lnr = new LineNumberReader(br);

		this.trace = lnr;
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param traceFile
	 *            The abstract {@link File} representation of the workload
	 *            trace.
	 * @param batchLength
	 *            The number of elements to parse per call of {@link #parse()}.
	 * @throws FileNotFoundException
	 *             iif the requested file does not exist or could not be opened.
	 * @throws IOException
	 *             iif an I/O error occurs during parsing.
	 */
	public CSWFParser(URL source, int batchLength)
			throws IOException {
		this(source);
		log.debug("using batch length of " + batchLength);
		this.batchLength = batchLength;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.workload.WorkloadSource#getNextJob()
	 */
	public Job fetchNextJob() throws WorkloadException {
		if( this.hasReadHeader == false) {
			this.readHeader(); 
		}
		this.ensureBufferRefill();
		return this.jobs.poll();
	}
	
	
	/**
	 * TODO: not yet commented
	 *
	 * @throws WorkloadException
	 */
	private void ensureBufferRefill() throws WorkloadException {
		if (this.jobs.isEmpty()) {
			try {
				jobs.clear();
				this.parse();
			} // try
			catch (IOException e) {
				String msg = "problems during I/O in parse: " + e.getMessage();
				log.error(msg, e);
				throw new WorkloadException(msg, e);
			} // catch
		} // if		
	}
	
	
	/**
	 * Reads the header of the workload file. Once read it is assumed that the 
	 * body of the workload file contains only CORRECT jobs.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs during parsing.
	 *             
	 * @since 27/06/09 Version for reading content only.
	 */
	private void readHeader() throws WorkloadException{
		try {
				do {
					String currentLine = this.trace.readLine();
					if (currentLine.startsWith(SWFConstants.COMMENT_DELIMITER)) {
						this.checkForHeaderLine(currentLine.substring(1).trim());
						this.markCurrentPosition();
					} else 
						this.hasReadHeader = true;
				}while(!this.hasReadHeader);
				
			} catch (IOException e) {
				String msg = "problems during I/O of header: " + e.getMessage();
				log.error(msg, e);
				throw new WorkloadException(msg, e);
			} // catch
	} 		
		
	
	/**
	 * Parses a batch of lines (see {@link #SWFParser(URL, int)}) from this
	 * parser's file, using the current batch length. All jobs must be
	 * valid, each parsed job (independent or composite) is represented as a 
	 * {@link Job} object and stored to the {@link Queue} of jobs 
	 * (see {@link #getJobs()}).
	 * 
	 * @throws IOException
	 *             iif an I/O error occurs during parsing.
	 *             
	 * @since 21/02/09
	 * @since 27/06/09 Version for reading content only.
	 */
	private void parse() throws IOException {
		
		Vector<SWFJob> B = new Vector<SWFJob>();
		String Jc = new String("initial");
		String Jn = new String("initial");
		this.trace.reset();
		
		do {
			// Decision tree for parsing lines and termination condition
			if(Jc.equals("initial") && Jn.equals("initial")) {
				Jc = this.trace.readLine();
				this.markCurrentPosition();
				Jn = this.trace.readLine();	
				if(Jc == null && Jn == null) {
					jobs.clear();
					return;
				}
			} else {
				if(Jn == null) {
					if (Jc == null)
						jobs.clear();
					this.markCurrentPosition();
					return;
				} else {
					Jc = Jn;
					this.markCurrentPosition();
					Jn = this.trace.readLine();
				} // End if null
			} // End if initial
			
			//Case 0, if Jc is composite and Jn is null
			if(isComposite(Jc) && Jn== null) {
				B.add(parseWorkflowLine(Jc));
				CompositeJob j = buildWorkflow(B);
				B.clear();
				jobs.offer(j);
				this.markCurrentPosition();
				return;
			}
			
			if(isComposite(Jc)) {
				if(isComposite(Jn)) {
					if(jobsBelongToSameSet(Jc, Jn)) {
						B.add(parseWorkflowLine(Jc));
					} else {
						B.add(parseWorkflowLine(Jc));
						CompositeJob j = buildWorkflow(B);
						B.clear();
						jobs.offer(j);
					} //End if Jn & Jc belong to the same composite job
				} else {
					//	Case IV, (Jc is composite) and Jn is independent
					B.add(parseWorkflowLine(Jc));
					CompositeJob j = buildWorkflow(B);
					B.clear();
					jobs.offer(j);
				} //End is Composite(Jn)
			} else {
				// 	Case I, Jc is INDEPENDENT
				SWFJob j = parseIndependentJobLine(Jc);
				if ( j != null)	// Aqui modifique
					jobs.offer(j);
			} // End is Composite(Jc)
		
		} while(  jobs.size() < this.batchLength ); 
	}

	/**
	 * Marks the current position in the trace to be able to continue parsing
	 * from a certain point in the underlying file.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs during parsing.
	 */
	private void markCurrentPosition()
			throws IOException {
		this.trace.mark(Short.MAX_VALUE);
	}


	/**
	 * Parses header or comment lines and sets the representing object's fields
	 * accordingly.
	 * 
	 * @param lineToParse
	 *            The input line to be parsed.
	 */
	private void checkForHeaderLine(String lineToParse) {
		String keyword = null;

		keyword = SWFConstants.HeaderFields.VERSION.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.COMPUTER.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.INSTALLATION.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.ACKNOWLEDGE.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			String value = lineToParse.substring(keyword.length()).trim();
			String[] oldAcknowledgeEntries = null; // fetch old array here
			ArrayUtils.add(oldAcknowledgeEntries, value);
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.INFORMATION.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.CONVERSION.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.MAX_JOBS.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.MAX_RECORDS.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.PREEMPTION.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			String value = lineToParse.substring(keyword.length()).trim();
			if (SWFConstants.HeaderFields.PREEMPTION_NO.equals(value)) {
				// skip usage
			} // if
			else if (SWFConstants.HeaderFields.PREEMPTION_YES.equals(value)) {
				// skip usage
			} // else if
			else if (SWFConstants.HeaderFields.PREEMPTION_DOUBLE.equals(value)) {
				// skip usage
			} // else if
			else if (SWFConstants.HeaderFields.PREEMPTION_TS.equals(value)) {
				// skip usage
			} // else if
		} // if

		keyword = SWFConstants.HeaderFields.UNIX_START_TIME.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.TIME_ZONE.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.TIME_ZONE_STRING.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.START_TIME.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.END_TIME.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.MAX_NODES.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.MAX_PROCS.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.MAX_RUNTIME.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.MAX_MEMORY.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.ALLOW_OVERUSE.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.MAX_QUEUES.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.QUEUES.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.QUEUE.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			String value = lineToParse.substring(keyword.length()).trim();
			String[] oldQueueEntries = null; // fetch old array here
			ArrayUtils.add(oldQueueEntries, value);
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.MAX_PARTITIONS.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.PARTITIONS.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.PARTITION.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			String value = lineToParse.substring(keyword.length()).trim();
			String[] oldPartitionEntries = null; // fetch old array here
			ArrayUtils.add(oldPartitionEntries, value);
			// skip usage
		} // if

		keyword = SWFConstants.HeaderFields.NOTE.getIdentifier();
		if (lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			String value = lineToParse.substring(keyword.length()).trim();
			String[] oldNoteEntries = null; // fetch old array here
			ArrayUtils.add(oldNoteEntries, value);
			// skip usage
		} // if
	}

		
	/**
	 * TODO: not yet commented
	 * 
	 * @param job
	 * @return
	 */
	private boolean applyFilters(Job job) {
		boolean result = true;
		for (WorkloadFilter filter : filters) {
			result = filter.apply(job, true);
			if (!result) {
				break;
			} // if
		} // for
		return result;
	}  

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.teikoku.workload.WorkloadSource
	// -------------------------------------------------------------------------

	/*
	 * Reads the header
	 * 
	 * @see de.irf.it.rmg.teikoku.workload.WorkloadSource#initialize(de.irf.it.rmg.teikoku.workload.WorkloadFilter[])
	 */
	public void initialize(WorkloadFilter[] filters)
			throws InitializationException {
		this.filters = filters;

		try {
			this.markCurrentPosition();
			String currentLine = this.trace.readLine().trim();
			while (currentLine != null) {
				if (currentLine.startsWith(";")) {
					this.checkForHeaderLine(currentLine);
				} // if
				else {
					this.trace.reset();
					break;
				} // else
				this.markCurrentPosition();
				currentLine = this.trace.readLine();
			} // while
			this.hasReadHeader = true;
		} // try
		catch (IOException e) {
			String msg = "problems during I/O: " + e.getMessage();
			log.error(msg, e);
			throw new InitializationException(msg, e);
		} // catch
	}

		
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.workload.WorkloadSource#inspectNextJob()
	 */
	public Job inspectNextJob() throws WorkloadException {
		this.ensureBufferRefill();
		return this.jobs.peek();
	}

	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("url", this.url).toString();
	}
	
	/*
	 * isCompositeJob
	 */
	/* 
	* Returns a Workflow object built using the jobs in compositeJobsBuffer. 
	* The structure of the workflow is defined based on the type of any task
	* in the composite job buffer.   
	* 
	* @return a workflow instance				
	*/
	private CompositeJob buildWorkflow(Vector<SWFJob> B){
		SWFJob job = B.firstElement();
		CompositeJob compositeJob = null;
		WorkflowFactory factory = null;
		
		JobType thisJobType = job.getJobType(); 
		// Build a TREE using vertices in the CompositeJobBuffer
		if(thisJobType.equals(JobType.TREE))
			factory = new TreeFactory();
		
		// Build a DAG using vertices in the CompositeJobBuffer
		if(thisJobType.equals(JobType.DAG)) 
			factory = new DAGFactory();
		
		// Build a CHAIN using vertices in the CompositeJobBuffer
		if(thisJobType.equals(JobType.CHAIN)) 
			factory = new ChainFactory();
		
		// Build a DG using vertices in the CompositeJobBuffer
		if(thisJobType.equals(JobType.DG)) {/* To be implemented */}
			
		compositeJob = factory.getCompositeJob(B);
		
		/*
		 * Initialization of the compositeJob
		 * - Submit time: is equal to the minimum submit time of member jobs
		 * - Requested number of processors
		 * - Job type   
		 * - Color of member jobs, independent jobs are colored white, otherwise black
		 * - Initialize member jobs UUID with the container ID
		 */
		long minSubmitTime = compositeJob.setSubmitTime();
		compositeJob.setRequestedNumberOfProcessors(
				getNumRequestedProc(compositeJob.getStructure()));
		compositeJob.setRequestedTime(		
				getRequestedTime(compositeJob.getStructure()));
		
		compositeJob.setJobType(thisJobType);
		CompositeJobUtils.initialize(compositeJob.getStructure(), compositeJob.getUUID());
				
		/* Set the release time of all member jobs to the composite job release time */
		for(SWFJob j : compositeJob.getStructure().getVertices()){
			j.setSubmitTime(minSubmitTime);
		}
				
		return compositeJob;
	}

	

	/* 
	* Stores an WSWF line in the CompositeJobBuffer buffer. Once all member jobs
	* to a workflow are read, the a Graph structure can be built. This function is performed 
	* by the method buildWorkflow.  
	* 
	* @param currentLine 	an WSWF line, read from a file.
	*/
	private SWFJob parseWorkflowLine(String currentLine){
		StringTokenizer lineParser = new StringTokenizer(currentLine);
		
		SWFJob job = new SWFJob(null);
		
		job.setJobNumber(new Long((String) lineParser.nextToken()).longValue());						//1	
		job.setSubmitTime(new Long((String) lineParser.nextToken()).longValue());						//2
		job.setWaitTime(new Long((String) lineParser.nextToken()).longValue());							//3
				
		long  rt = Math.round((new Double((String) lineParser.nextToken())).doubleValue());
		if (rt == 0) rt = 1;
		job.setRunTime(rt); 																			//4
		job.setOriginalRunTime(rt);
		
		job.setNumberOfAllocatedProcessors(new Integer((String) lineParser.nextToken()).intValue());	//5
		job.setAverageCPUTimeUsed(new Double((String) lineParser.nextToken()).doubleValue()); 			//6
		job.setUsedMemory(new Integer((String) lineParser.nextToken()).intValue());						//7
		job.setRequestedNumberOfProcessors(new Integer((String) lineParser.nextToken()).intValue());	//8
		
		rt = Math.round((new Double((String) lineParser.nextToken())).doubleValue());
		if (rt == 0) rt = 1;
		//job.setRunTime(rt);
		job.setRequestedTime(rt);																		//9
		
		job.setRequestedMemory(new Integer((String) lineParser.nextToken()).intValue());				//10
		job.setStatus(new Byte((String) lineParser.nextToken()).byteValue());							//11
		job.setUserID(new Short((String) lineParser.nextToken()).shortValue());							//12
		job.setGroupID(new Short((String) lineParser.nextToken()).shortValue());						//13
		job.setExecutableApplicationNumber(new Long((String) lineParser.nextToken()).longValue());		//14
		job.setQueueNumber(new Byte((String) lineParser.nextToken()).byteValue());						//15
		job.setPartitionNumber(new Byte((String) lineParser.nextToken()).byteValue());					//16
		job.setPrecedingJobNumber(new Long((String) lineParser.nextToken()).longValue());				//17
		job.setThinkTimeFromPrecedingJob(new Double((String) lineParser.nextToken()).doubleValue());	//18
		
		// Composite job attributes
		job.setJobType(((String)lineParser.nextToken()).trim());										//19
		job.setCompositeJobId(new Integer(lineParser.nextToken()).intValue());							//20

		// Successors																					//21
		String sucessors = lineParser.nextToken();
		if( !sucessors.equals("-1") ) {
			StringTokenizer tokens = new StringTokenizer(sucessors,",\t\n\r\f");
			while(tokens.hasMoreElements())
				job.setSuccessor(new Integer((String)tokens.nextElement()));
		}
		
		// Predecessors																				//22
		String predecessors = lineParser.nextToken();
		if( !predecessors.equals("-1") ) {
			StringTokenizer tokens = new StringTokenizer(predecessors,",\t\n\r\f");
			while(tokens.hasMoreElements())
				job.setPredecessor(new Integer((String)tokens.nextElement()));
		}
		
		log.debug("inserting valid entry (" + job + ") into job list");
		return job;
	}

	
	/**
	 * Parses body lines and adds new {@link Job} objects to the queue of parsed
	 * jobs (see {@link #getJobs()}) accordingly, if validity is given.
	 * 
	 * @param lineToParse
	 *            The input line to be parsed.
	 */
	private SWFJob parseIndependentJobLine(String lineToParse){
		StringTokenizer tokenizer = new StringTokenizer(lineToParse);

		/*
		 * create new SWF job object and fill with values from current line
		 */
		
		// XXX: set SWFHeader correctly
		SWFJob job = new SWFJob(null);

		if (tokenizer.countTokens() != SWFConstants.NUMBER_OF_JOB_FIELDS) {
			String msg = "line has missing fields, skipping...";
			log.warn(msg);
		} // if

		job.setJobNumber(Long.parseLong(tokenizer.nextToken()));									// 1
		job.setSubmitTime(Long.parseLong(tokenizer.nextToken()));									// 2
		job.setWaitTime(Long.parseLong(tokenizer.nextToken()));										// 3
		job.setRunTime(Long.parseLong(tokenizer.nextToken()));										// 4
		job.setNumberOfAllocatedProcessors(Integer.parseInt(tokenizer.nextToken()));				// 5
		job.setAverageCPUTimeUsed(Double.parseDouble(tokenizer.nextToken()));						// 6
		job.setUsedMemory(new Integer((String) tokenizer.nextToken()).intValue());					// 7
		job.setRequestedNumberOfProcessors(new Integer((String) tokenizer.nextToken()).intValue());	// 8
		job.setRequestedTime(new Double((String) tokenizer.nextToken()).doubleValue());				// 9
		job.setRequestedMemory(new Integer((String) tokenizer.nextToken()).intValue());				// 10
		job.setStatus(new Byte((String) tokenizer.nextToken()).byteValue());						// 11
		job.setUserID(new Short((String) tokenizer.nextToken()).shortValue());						// 12
		job.setGroupID(new Short((String) tokenizer.nextToken()).shortValue());						// 13
		job.setExecutableApplicationNumber(new Long((String) tokenizer.nextToken()).longValue());	// 14
		job.setQueueNumber(new Byte((String) tokenizer.nextToken()).byteValue());					// 15
		job.setPartitionNumber(new Byte((String) tokenizer.nextToken()).byteValue());				// 16
		job.setPrecedingJobNumber(new Long((String) tokenizer.nextToken()).longValue());			// 17
		job.setThinkTimeFromPrecedingJob(new Double((String) tokenizer.nextToken()).doubleValue());	// 18

		// No need to parse the other values.
		job.setJobType(JobType.INDEPENDENT);
		
		// XXX: use SWFHeader for creating workload filters, if applicable
		if (applyFilters(job)) {
			log.debug("inserting valid entry (" + job + ") into job list");
			//this.jobs.offer(job);
		} else 
			job = null; 
		
		return job; 
	}
	

	/* 
	* Returns the workflow identifyer of the job. 
	* 
	* @param currentLine 	an WSWF line.
	* @return 				determines if the job is composite or independent.	
	*/
	private boolean isComposite(String currentLine) {
		return !currentLine.contains(JobType.INDEPENDENT.toString());
	}
	
	
	/* 
	* Returns the workflow identifyer of the job. 
	* Field number 20 in the WSWF format stores the job identifyer. 
	* 
	* @param currentLine 	an WSWF line.
	* @return 				the workflow identifyer.	
	*/
	private int getWorkflowId(String currentLine) {
		int i = 0, id = -1;
		String token = null;
	
		StringTokenizer lineParser = new StringTokenizer(currentLine);
		while( lineParser.hasMoreElements()){
			token = (String) lineParser.nextToken();
			i++;
			if( i == 20) {
				id = (new Integer(token.trim())).intValue();
				break;
			}
		}
		return id;
	}


	/* 
	* Returns the true if the two given jobs belogns to the same workflow. 
	* 
	* @param currentLine 	an WSWF line in its String representation.
	* @param nextLine	 	an WSWF line in its String representation.
	* 
	* @return 				A boolean 	
	*/
	private boolean jobsBelongToSameSet(String currentLine, String nextLine) {
		
		if (nextLine == null || currentLine == null)
			return false;
		
		int job_1 = getWorkflowId(currentLine);
		int job_2 = getWorkflowId(nextLine);
		return (job_1 == job_2);
	}
	
	
	private int getNumRequestedProc(Graph<SWFJob,Precedence> g) {
		int reqProc = 0;
		for(SWFJob job: g.getVertices()) 
			reqProc += job.getRequestedNumberOfProcessors();
		return reqProc;
	}

	
	private double getRequestedTime(Graph<SWFJob,Precedence> g) {
		double reqTime = 0;
		for(SWFJob job: g.getVertices()) 
			reqTime += job.getRequestedTime();
		return reqTime;
	
	}
}
