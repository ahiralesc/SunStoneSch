* Sun stone runtime configuration

This short tutorial assumes Eclipse IDE for Java Developers version Neon Release 4.6.0
Build id: 20160613-1800. However it has been tested in other IDE and non IDE environments 
successfully.

The simulator (Sun Stone Scheduler, STS) is run as a *java application*

* Clone project from Github https://github.com/ahiralesc/SunStoneSch

* Configuration in Eclipse
1. Clone project from Github https://githubA.com/ahiralesc/SunStoneSch. For cloning instructions see https://help.github.com/articles/cloning-a-repository/
2. Install MAVEN. See http://toolsqa.com/java/maven/how-to-install-maven-eclipse-ide/
3. *Select + right click project name*. Select *Configure* + *Convert to Maven project".
2. STS runs as a stand alone java application. To prepare the execution context select *Run* + *Run Configurations* from the main menu and double click on the *Java Application* label (left panel)
3. Fill in the following fields in the *Main* and *Arguments" tabs:
3.1. Name : <a name label>
3.1. Project : <a project name label>
3.2. Main class: de.irf.it.rmg.core.teikoku.Bootstrap
3.3. -m de.irf.it.rmg.core.teikoku.runtime.SimulationManager -d "C:\<path to the project>\tGSF" "config.properties"

During the execution of the application practically no output should be produced. After execution, a file with results is created for each simulated machine. 
Such files are stored in /results directory.
