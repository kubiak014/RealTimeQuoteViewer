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
    in Console run, under base directory or project run: "gradle build"
</p>


