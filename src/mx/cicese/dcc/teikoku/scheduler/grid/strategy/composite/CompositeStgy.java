package mx.cicese.dcc.teikoku.scheduler.grid.strategy.composite;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.scheduler.grid.strategy.Admissible;
import mx.cicese.dcc.teikoku.scheduler.grid.strategy.StgyType;
import mx.cicese.dcc.teikoku.scheduler.grid.strategy.Strategy;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import edu.uci.ics.jung.graph.Hypergraph;


public abstract class CompositeStgy implements Strategy {
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
	public CompositeStgy() {
		this.jobFilteringStgy = new Admissible();
		type = StgyType.COMPOSITE;
	}
	
	
	/**
	 * Binds the strategy to the given site and sets strategy name
	 */
	public void bind(Site site, String name) {
		this.name = name;
		this.gridInfBroker = (GridInformationBroker) site.getInformationBroker();
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
	
	public abstract AllocationEntry schedule(Job job, JobControlBlock jcb);
	
	public abstract void setRanking(Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb);
	
	//public void setSite(Site site);
	
	public abstract void initialize();
	
} 
