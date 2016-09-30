package mx.cicese.dcc.teikoku.workload.job;

/**
 * CompositeVertex is the base interface that all vertex implementations must implement.
 * It allows association of cost and state to a given vertex.
 * <p> 
 * The state models the current status of the member job during the execution of the 
 * workflow or composite job. (see <a href="#MemberState">MemberState</a>)
 * <p>  
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public interface Vertex {
	
	/** 
     * Sets the cost of the vertex
     *
     * @param cost	the cost of the vertex
     */
	public void setCost(long cost);
	
	/** 
     * Gets the cost of the vertex
     *
     * @return		the cost of the vertex
     */
	public long getCost();
	
	/** 
     * Sets the state of the member job
     *
     * @param state		the state of the member job
     */
	public void setMemberState(MemberState state);
	
	/** 
     * Gets the state of the member job
     *
     * @return			the state of the member job
     */
	public MemberState getMemberState();

} //End CompositeVertex
