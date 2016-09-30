package mx.cicese.dcc.teikoku.workload.job;

/**
 *	The MemberState enumeration is used to determine the current status of a member 
 *	job of a workflow or composite job. That is, the state that the member job assumes 
 *	during the workflow scheduling process. 
 *	<p>
 *	
 *	The following possibilities are considered: 
 *	<ul>
 *	<li> NOT_EXPLORED 			The member job is independent but has not been released.
 *	<li> EXPLORED_NOT_COMPLETED	The member job is independent and has been released, but may be in a release, queued, or started state.
 *	<li> NOT_AVAILABLE			The member job is not available due to at least one precedence constrait.
 *	<li> COMPLETED				The member job has completed its execution.
 *	</ul>
 *	<p>
 *	A member job transitions from NOT_AVAILABLE to COMPLETED state. 
 *	Other transitions are not valid in this implementation.
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public enum MemberState {
	 /**
	  * The member job is independent but has not been released
	  */
	NOT_EXPLORED(),
	 
	/**
	 * The member job is independent and has been released, but may 
	 * be in a release, queued, or started state.
	 */
	EXPLORED_NOT_COMPLETED(),
	
	/**
	 * The member job is not available due to at least one precedence constrait.
	 */ 
	NOT_AVAILABLE(),
	 
	/**
	 * The member job has completed its execution.
	 */
	COMPLETED();
	 
	/**
	 * Verifies valid state transitions
	 * 
	 *	@param 	source	the source state (current state)
	 *	@return	<code>true</code> if the transition is valid;
	 *			<code>false</code> otherwise
	 */
	 public boolean validateStateChange(MemberState source) {
		 boolean result;
			
		 switch (this) {
				case NOT_AVAILABLE: 
					result = source == null ? true : false;
					break;
					
				case NOT_EXPLORED:
					result = (source == null || NOT_AVAILABLE.equals(source)) ? true : false; 
							
					break;
				
				case EXPLORED_NOT_COMPLETED:
					result = NOT_EXPLORED.equals(source); 
					break;
					
				case COMPLETED:
					result = EXPLORED_NOT_COMPLETED.equals(source);
					break;
				
				default:
					result = false;
					break;
			} // End switch
			return result;
		} // End validateStateChange
}//End MemberState
