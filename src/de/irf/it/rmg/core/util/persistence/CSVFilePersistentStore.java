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
package de.irf.it.rmg.core.util.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.util.FileHelper;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class CSVFilePersistentStore extends AbstractPersistentStore {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(CSVFilePersistentStore.class);

	/**
	 * TODO: not yet commented
	 * 
	 */
	final private static String FILE_EXTENSION = ".csv";

	/**
	 * TODO: not yet commented
	 * 
	 */
	final private static char CSV_SEPARATOR = ' ';

	/**
	 * TODO: not yet commented
	 * 
	 */
	final private static String DATE_FORMAT = "MMddHHmmssS";
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private PrintWriter writer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.persistence.AbstractPersistentStore#initialize()
	 */
	@Override
	public void initialize() throws InitializationException {
		super.initialize();
		File outputFile = null;
		/*
		 * create a corresponding File object from the given URL
		 */
		try {
			outputFile = new File(super.getUrl().toURI());
			/*
			 * add the proper extension to the given file name
			 */
			String outputFileName = outputFile.getParent() + File.separator
					+ outputFile.getName();
			if (!super.isAllowOverwrite()) {
				/*
				 * since overwriting is not allowed, append a timestamp to the
				 * given filename
				 */
				outputFileName += "#"
						+ new SimpleDateFormat(DATE_FORMAT).format(new Date()); 
			} // if
			outputFileName += FILE_EXTENSION;
			outputFile = new File(outputFileName);
		} // try
		catch (URISyntaxException e) {
			String msg = "initialization failed: incorrect URL format";
			log.error(msg, e);
			throw new InitializationException(msg, e);
		} // catch

		/*
		 * check whether the parent directories exist and, if not, create them
		 */
		if (FileHelper.ensureWritableDirectory(outputFile.getParentFile())) {
			/*
			 * initialize a writer for the given file
			 */
			try {
				FileWriter fw = new FileWriter(outputFile);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw, true);
				this.writer = pw;
			} // try
			catch (IOException e) {
				String msg = "problems during I/O: " + e.getMessage();
				log.error(msg, e);
				throw new InitializationException(msg, e);
			} // catch
		} // if
		else {
			String msg = "directory for output file ("
					+ super.getUrl()
					+ ") not available: creation failed, path is no directory or not writable";
			log.error(msg);
			throw new InitializationException(msg);
		} // if
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.util.persistence.PersistentStore
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.persistence.PersistentStore#makePersistent(java.lang.Object[])
	 */
	public void makePersistent(Object... values) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			sb.append(CSV_SEPARATOR);
			sb.append(values[i].toString());
		} // for
		this.writer.println(sb.toString());
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Ephemeral
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Ephemeral#commence()
	 */
	public void commence() {
		// do nothing here.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Ephemeral#terminate()
	 */
	public void terminate() {
		this.writer.close();
	}

	public String getOutputFileName() {
		// TODO: not yet implemented.
		return null;
	}

	public void setOutputFileName(String name) {
		// TODO: not yet implemented.
	}
}
