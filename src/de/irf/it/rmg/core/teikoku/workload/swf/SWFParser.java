/*
 * // $Id$ //
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2009 by the
 *   Robotics Research Institute (Section Information Technology)
 *   at TU Dortmund University, Germany
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
// $Id$

/*
 * "Teikoku Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2006 by the
 *   Robotics Research Institute (Information Technology Section)
 *   Dortmund University, Germany
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
package de.irf.it.rmg.core.teikoku.workload.swf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
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

/**
 * A text file parser for HPC job traces stored in the <a
 * href="http://www.cs.huji.ac.il/labs/parallel/workload/swf.html">Standard
 * Workload Format (SWF)</a> by Dror G. Feitelson.
 * 
 * This class understands SWF up to version 2.2 with both header (comments) and
 * body (trace entry) support. Note that, currently, only traces with
 * <code>;Preemption: [No|Yes]</code> job entries are processed correctly: the
 * <code>;Preemption: Double</code> lines will probably result in parsing
 * errors (depending on the structure of the summary and part line entries). The
 * <code>;Preemption: TS</code> type is ignored and will be (due to the lack
 * of details on the format) interpreted as <code>;Preemption: No</code>.
 * 
 * Header entries are currently parsed, but never stored; non-existant or empty
 * headers are handled gracefully (as far as possible).
 * 
 * Regarding body entries, a list of already parsed and validated jobs is kept
 * and can be accessed via the {@link #getJobs()} method. During validation, few
 * rudimentary semantic checks are performed; especially, it is checked whether
 * 
 * <pre>
 *             Requested Time &lt; Run Time | ;AllowOverUse: False
 * </pre>
 * 
 * is true; then,
 * 
 * <pre>
 *             Run Time := Requested Time
 * </pre>
 * 
 * is performed, since most overuse-forbidding systems kill jobs which run
 * longer than requested. Furthermore, this parser always sets this field to the
 * value of 'numberOfAllocatedProcessors', i.e.
 * 
 * <pre>
 *             Requested Number Of Processors := Number Of Allocated Processors
 * </pre>
 * 
 * since many of the public available workloads do not contain sane values for
 * the 'requestedNumberOfProcessors' field. Also, it is tested whether
 * 
 * <pre>
 *             Requested Number of Processors &lt; [;MaxProcs|;MaxNodes] 
 * </pre>
 * 
 * is true (testing is done in order, whichever header field is available -- if
 * none is set, jobs are accepted per default as valid); otherwise, such jobs
 * are ignored. Finally, all jobs with a <code>Run Time</code> value of
 * <code>0</code> are ignored, too (since they don't influence the schedule).
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class SWFParser
		implements WorkloadSource {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(SWFParser.class);

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
	 * Creates a new instance of this class.
	 * 
	 */
	private SWFParser() {
		this.batchLength = 10000;
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
	public SWFParser(URL source)
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
	public SWFParser(URL source, int batchLength)
			throws IOException {
		this(source);
		log.debug("using batch length of " + batchLength);
		this.batchLength = batchLength;
	}

	/**
	 * Parses a batch of lines (see {@link #SWFParser(URL, int)}) from this
	 * parser's file, using the current batch length. Every parsed line is
	 * checked for validity and, if correct, added as a new {@link Job} object
	 * to the {@link Queue} of jobs (see {@link #getJobs()}).
	 * 
	 * @throws IOException
	 *             iif an I/O error occurs during parsing.
	 */
	public void parse()
			throws IOException {
		this.trace.reset();
		for (int i = 0; i < this.batchLength; i++) {
			String currentLine = this.trace.readLine();
			this.markCurrentPosition();
			if (currentLine != null) {
				log.debug("parsing line " + this.trace.getLineNumber());
				this.parseLine(currentLine.trim());
			} // if
			else {
				break;
			} // else
		} // for
	}

	/**
	 * Marks the current position in the trace to be able to continue parsing
	 * from a certain point in the underlying file.
	 * 
	 * @throws IOException
	 *             iif an I/O error occurs during parsing.
	 */
	private void markCurrentPosition()
			throws IOException {
		this.trace.mark(Short.MAX_VALUE);
	}

	/**
	 * Parses a single line, dispatching it to the correct handling methods
	 * depending on its type (header/body).
	 * 
	 * @param currentLine
	 *            The input line to be parsed.
	 * @throws IOException
	 *             iif an I/O error occurs during parsing.
	 */
	private void parseLine(String currentLine) {
		if (currentLine.startsWith(SWFConstants.COMMENT_DELIMITER)) {
			this.checkForHeaderLine(currentLine.substring(1).trim());
		} // if
		else {
			this.checkForJobLine(currentLine.trim());
		} // else
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
	 * Parses body lines and adds new {@link Job} objects to the queue of parsed
	 * jobs (see {@link #getJobs()}) accordingly, iif validity is given.
	 * 
	 * @param lineToParse
	 *            The input line to be parsed.
	 */
	private void checkForJobLine(String lineToParse) {
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

		job.setJobNumber(Long.parseLong(tokenizer.nextToken()));
		job.setSubmitTime(Long.parseLong(tokenizer.nextToken()));
		job.setWaitTime(Long.parseLong(tokenizer.nextToken()));
		job.setRunTime(Long.parseLong(tokenizer.nextToken()));
		job.setNumberOfAllocatedProcessors(Integer.parseInt(tokenizer
				.nextToken()));
		job.setAverageCPUTimeUsed(Double.parseDouble(tokenizer
				.nextToken()));
		job.setUsedMemory(Integer.parseInt(tokenizer.nextToken()));
		job.setRequestedNumberOfProcessors(Integer.parseInt(tokenizer
				.nextToken()));
		job.setRequestedTime(Long.parseLong(tokenizer.nextToken()));
		job.setRequestedMemory(Integer.parseInt(tokenizer.nextToken()));
		job.setStatus(Byte.parseByte(tokenizer.nextToken()));
		job.setUserID(Short.parseShort(tokenizer.nextToken()));
		job.setGroupID(Short.parseShort(tokenizer.nextToken()));
		job.setExecutableApplicationNumber(Long.parseLong(tokenizer
				.nextToken()));
		job.setQueueNumber(Byte.parseByte(tokenizer.nextToken()));
		job.setPartitionNumber(Byte.parseByte(tokenizer.nextToken()));
		job.setPrecedingJobNumber(Long.parseLong(tokenizer.nextToken()));
		job.setThinkTimeFromPrecedingJob(Double.parseDouble(tokenizer
				.nextToken()));

		/*
		 * if the parsed job is valid, add it to parsed job queue
		 */
		
		// XXX: use SWFHeader for creating workload filters, if applicable
		if (applyFilters(job)) {
			log.debug("inserting valid entry (" + job + ") into job list");
			this.jobs.offer(job);
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
	// de.irf.it.rmg.core.teikoku.workload.WorkloadSource
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.workload.WorkloadSource#initialize(de.irf.it.rmg.core.teikoku.workload.WorkloadFilter[])
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
		} // try
		catch (IOException e) {
			String msg = "problems during I/O: " + e.getMessage();
			log.error(msg, e);
			throw new InitializationException(msg, e);
		} // catch
	}

	/**
	 * TODO: not yet commented
	 *
	 * @throws WorkloadException
	 */
	private void ensureBufferRefill() throws WorkloadException {
		if (this.jobs.isEmpty()) {
			try {
				this.parse();
			} // try
			catch (IOException e) {
				String msg = "problems during I/O: " + e.getMessage();
				log.error(msg, e);
				throw new WorkloadException(msg, e);
			} // catch
		} // if		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.workload.WorkloadSource#inspectNextJob()
	 */
	public Job inspectNextJob()
			throws WorkloadException {
		this.ensureBufferRefill();
		return this.jobs.peek();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.workload.WorkloadSource#getNextJob()
	 */
	public Job fetchNextJob()
			throws WorkloadException {
		this.ensureBufferRefill();
		return this.jobs.poll();
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("url", this.url).toString();
	}
}
