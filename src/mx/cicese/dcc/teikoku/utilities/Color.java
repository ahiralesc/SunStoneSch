package mx.cicese.dcc.teikoku.utilities;

import mx.cicese.dcc.teikoku.workload.job.MemberState;

/**
 *	Color is used by the classic Depth First Search algorithm in Graph 
 * 	theory. A color indicates if a vertex has been explored or not.
 *	<p>
 *	The following color states are defined: 
 *	<ul>
 *	<li> WHITE 		The vertex has not been explored.
 *	<li> GRAY 		The vertex is being explored, one or more adjacent vertices have not been explored.
 *	<li> BLACK		All adjacent vertices have been explored.
 *	</ul>
 *	<p>
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public enum Color {
	/*
	 * Used to label the vertex as not explore.
	 */
	WHITE(),
	
	/*
	 * Used to level a vertex when one or more adjacent 
	 * neighbors have been explored.
	 */
	GRAY(),
	
	/*
	 * Used to level vertex when all adjacent neighbors 
	 * have been explored.
	 */
	BLACK();
	
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
				case WHITE: 
					result = source == null ? true : false;
					break;
					
				case GRAY:
					result = WHITE.equals(source); 
					break;
					
				case BLACK:
					result = GRAY.equals(source);
					break;
				
				default:
					result = false;
					break;
			} // End switch
			return result;
		} // End validateStateChange
}//End Color
