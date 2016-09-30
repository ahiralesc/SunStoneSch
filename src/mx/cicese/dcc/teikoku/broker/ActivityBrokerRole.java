/**
 * 
 */
package mx.cicese.dcc.teikoku.broker;

/**
 * 
 * 	Used for setting activity broker role.
 * 	<p>
 *  The following roles are defined:
 * 	<ul>
 * 		<li> GRID	
 * 					A centralized grid activity broker with capabilities
 * 					of scheduling independent and composite jobs.
 * 		<li> COMPUTE_SITE
 * 					A site activity broker can only schedule independent jobs,
 * 					some load balancing capabilities might be included.
 * 		<li> DATA_SITE
 * 					Currently not supported.
 * 	</ul>
 * <p>
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public enum ActivityBrokerRole {
	GRID,
	COMPUTE_SITE,
	DATA_SITE
}
