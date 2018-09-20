This project is a REST service that supplies data about the dividend stock with the provided ticker symbol.  Non dividend stocks will not be returned.
I was unable to find any previously existing free web service that supplies full dividend history, this project fills that gap.  However, without an official exchange connection, my only free source for the data was to scrape values from an existing web page (dividata.com), and this was done via HTMLUnit.  Additional endpoints also provide relevant data to be used with the dividend history.

The available service endpoints are:
/stocks/{symbol}						--Basic information about the stock
/stocks/{symbol}/dividends/data			--Information about the stock's dividend
/stocks/{symbol}/dividends/history		--A list of the entire dividend payout history for the stock
/stocks/{symbol}/fundamentals			--The stock's fundamentals

A running copy has been deployed to AWS at: http://dividendstockservice-env.gpdfnadfve.us-west-2.elasticbeanstalk.com
Example Use: http://dividendstockservice-env.gpdfnadfve.us-west-2.elasticbeanstalk.com/stocks/AAPL

Notes:
-This project is intended as a simple demo work of a Spring Boot REST web service, along with use of caching and HTMLUnit.

-The fact that data comes from HTMLUnit reading the source webpage means that uncached requests are slower on first call.  Results are cached for each day (since the data is daily granularity) to speed up future requests.
