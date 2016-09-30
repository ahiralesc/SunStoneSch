// $Id$

/*
 * "Teikoku Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2007 by the
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
package org.ggf.drmaa;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Indicate the value for the {@link JobTemplate#getTransferFiles()} property.
 * 
 * This denotes whether the three available input/output streams for a job are
 * to be transferred before and after execution.
 * 
 * @see JobTemplate
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class FileTransferMode
		implements Serializable, Cloneable {

	/**
	 * The serial version unique identifier of this class.
	 * 
	 * @see Serializable
	 */
	final private static long serialVersionUID = 5440017534037373588L;

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(FileTransferMode.class);

	/**
	 * Denotes whether to transfer input stream files.
	 * 
	 */
	private boolean transferInputStream;

	/**
	 * Denotes whether to transfer output stream files.
	 * 
	 */
	private boolean transferOutputStream;

	/**
	 * Denotes whether to transfer error stream files.
	 * 
	 */
	private boolean transferErrorStream;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public FileTransferMode() {
		this.transferInputStream = false;
		this.transferOutputStream = false;
		this.transferErrorStream = false;
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param transferInputStream
	 *            Denotes whether to transfer input stream files.
	 * @param transferOutputStream
	 *            Denotes whether to transfer output stream files.
	 * @param transferErrorStream
	 *            Denotes whether to transfer error stream files.
	 */
	public FileTransferMode(boolean transferInputStream,
			boolean transferOutputStream, boolean transferErrorStream) {
		this.transferInputStream = transferInputStream;
		this.transferOutputStream = transferOutputStream;
		this.transferErrorStream = transferErrorStream;
	}

	/**
	 * Returns whether to transfer input stream files.
	 * 
	 * @return <code>true</code>, if the input stream files are to be
	 *         transferred; <code>false</code>, otherwise.
	 */
	public boolean getTransferInputStream() {
		return this.transferInputStream;
	}

	/**
	 * Sets whether to transfer input stream files.
	 * 
	 * @param transferInputStream
	 *            Denotes whether to transfer input stream files.
	 */
	public void setTransferInputStream(boolean transferInputStream) {
		this.transferInputStream = transferInputStream;
	}

	/**
	 * Returns whether to transfer output stream files.
	 * 
	 * @return <code>true</code>, if the output stream files are to be
	 *         transferred; <code>false</code>, otherwise.
	 */
	public boolean getTransferOutputStream() {
		return this.transferOutputStream;
	}

	/**
	 * Sets whether to transfer output stream files.
	 * 
	 * @param transferOutputStream
	 *            Denotes whether to transfer output stream files.
	 */
	public void setTransferOutputStream(boolean transferOutputStream) {
		this.transferOutputStream = transferOutputStream;
	}

	/**
	 * Returns whether to transfer error stream files.
	 * 
	 * @return <code>true</code>, if the error stream files are to be
	 *         transferred; <code>false</code>, otherwise.
	 */
	public boolean getTransferErrorStream() {
		return this.transferErrorStream;
	}

	/**
	 * Sets whether to transfer error stream files.
	 * 
	 * @param transferErrorStream
	 *            Denotes whether to transfer error stream files.
	 */
	public void setTransferErrorStream(boolean transferErrorStream) {
		this.transferErrorStream = transferErrorStream;
	}

}
