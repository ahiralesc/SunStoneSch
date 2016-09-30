package mx.cicese.dcc.teikoku.scheduler.grid.strategy.rigid;

import de.irf.it.rmg.core.teikoku.site.Site;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.scheduler.grid.strategy.Admissible;
import mx.cicese.dcc.teikoku.scheduler.grid.strategy.StgyType;
import mx.cicese.dcc.teikoku.scheduler.grid.strategy.Strategy;

/**
 * 	Given a rigid job (or parallel) the Rigid Strategy class provides an heuristic
 * 	that selects a target machine based on the heuristic criterion. 
 * 
 * 	Implemented rigid job heuristics include
 * 	<ol>
 * 	 	<li> Random 					@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Rand
 * 		<li> Round robin				@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.RRobin
 * 		<li> Max. Available Resources 	@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MaxAR
 * 		<li> Min. Turn Around  			@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MTA
 * 		<li> Min. Start Time  			@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MST
 * 		<li> PFFP			 			@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.PFFP
 * 		<li> Load Balance Size 			@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.LBal_S
 * 		<li> Min Completion Time 		@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MTC
 * 		<li> MLB			 			@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MLB
 * 		<li> Min. Load per Proc. 		@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MLp
 * 		<li> Min. Parallel Load			@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MPL
 * 	</ol>
 * 
 *  The scheduling method in all strategies implement the following pattern:
 *	<ol>
 *		<li> Get the admissible set of machines, job status data and runtime estimates are poll on such set
 *		<li> Get site or grid layer machine characteristics
 *		<li> Get job properties
 *		<li> Select a target machine, the selection criterion may use site, grid, and/or job properties
 *		<li> Create an allocation entry for the target machine, the allocation entry is contains information 
 *			on: the job to allocate, the target machine, and the job priority (if specified). 
 *	<ol>  	
 */

public abstract class RigidStgy implements Strategy {
	
	/**
	 * The grid information broker
	 */
	protected GridInformationBroker gridInfBroker;
	
	
	/**
	 * A job filtering policy
	 */
	protected Admissible jobFilteringStgy;
		
	
	/**
	 * The strategy name
	 */
	private String name;
	
	
	/**
	 * The strategy type
	 */
	StgyType type;
	
		
	/**
	 * Class constructor
	 */
	public RigidStgy() {
		this.jobFilteringStgy = new Admissible();
		type = StgyType.RIGID;
	}
	
	
	/**
	 * Binds the strategy to the given site and sets strategy name
	 */
	public void bind(Site site, String name) {
		this.name = name;
		this.gridInfBroker = (GridInformationBroker)site.getInformationBroker();
	}
	
	
	/**
	 * Gets the rigid strategy name
	 * 
	 * @return the strategy name
	 */
	public String getName() {
		return this.name;
	}
	
	
	/**
	 * Gets the strategy type
	 * 
	 * @return the strategy type
	 */
	public StgyType getType() {
		return this.type;
	}
	
}