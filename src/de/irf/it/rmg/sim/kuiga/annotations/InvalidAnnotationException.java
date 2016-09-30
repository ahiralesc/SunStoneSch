package de.irf.it.rmg.sim.kuiga.annotations;

import java.io.Serializable;

/**
 * @author <a href="mailto:alexander.papaspyrou@tu-dortmund.de">Alexander Papaspyrou</a>
 * @since 0.2 (veto)
 * @version $Revision$ (as of $Date$ by $Author$)
 *
 */
public final class InvalidAnnotationException extends Exception {
	
	/**
	 * Holds the serialVersionUID of this class.
	 * 
	 * @see Serializable
	 */
	private static final long serialVersionUID = -786375081792004254L;

	public InvalidAnnotationException() {
		super();
	}

	public InvalidAnnotationException(String message) {
		super(message);
	}

	public InvalidAnnotationException(Throwable cause) {
		super(cause);
	}

	public InvalidAnnotationException(String message, Throwable cause) {
		super(message, cause);
	}

}
