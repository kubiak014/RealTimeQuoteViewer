# RealTimeQuoteViewer
<h2>General comments on assignement's tasks</h2>
<h3>Application Startup Notes:</h3>
<p>
    The Quote Viewer component needs to be started last as it has the continuous looping for price update.
    This order can easily be decoupled by nesting the QuoteViewer into a thread itself.
</p>


<h3>Get the positions from a mock CSV position file (consisting of tickers and number of shares/contracts of tickers in the portfolio)</h3>
<p>
    This was implemented via the arguments passed in command line when starting the app. 
    It will build the portfolio without the corresponding valuation at this stage.
</p>

<h3>Get the security definitions from an embedded database.</h3>
<p>
    Securities are defined through the data.sql file and can be rearranged based of the univers of stock / Options to be covered.
    The portfolio composition itself remain independent from securities definition.
    Securities data are leveraged during the price updates coming from portfolio content update requests
</p>

<h3>Publishes following details in real-time</h3>
<p>
    Portfolio real time updates are done by the QuoteViewer upon receiving a price update if the underlying is found in the portfolio (as stock or option)
    Portfolio NAV is re-calculated upon price updates
</p>

<h3>Third Party library</h3>
<p>
    OpenCSV library for CSV parsing.
</p>

<h2>Running / configuring the app </h2>
<h3>Build with gradle</h3>
<p>
    In Console under base directory or project, run:<br/> 
    <code>gradle build</code>
    <br/><i>Local manual gradle build evidence in Gradle_console_build.PNG</i> 
</p>

<h3>Start the application</h3>
<p>
    In Console under base directory or project, run:<br/> 
    <code>gradlew.bat bootRun --args="./testFiles/portfolioPosition.csv"</code>
    <br/><i>Local startup evidence in  Gradle_console_start.PNG</i>
</p>

<h3>Static data config</h3>
<ul>
    <li>Underlying static data as annual return / return std dev can be updated in the data.sql file to be loaded at startup</li>
    <li>Initial Portfolio has to be passed in the command line as argument</li>
    <li>Strike has not been stored in DB as static data, it is parsed from the security ticker directly in the case of an option security</li>
</ul>

<h2>Further notes</h2>
<p>
    Scaling up market provider can be done by adding an addition argument when starting the program: <br/>
    <code>gradlew.bat bootRun --args="./testFiles/portfolioPosition.csv 10"</code>
</p>

