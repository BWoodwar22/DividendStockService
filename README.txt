This project is a REST service that supplies data about the dividend stock with the provided ticker symbol.
I was unable to find any previously existing free web service that supplies full dividend history (which is the main data I was personally interested in using), so this project fills that gap.  However, without an official exchange connection, my only source for the data was to scrape values from an existing web page (dividata.com), and this was done via HTMLUnit.

The available service endpoints are:
/stocks/{symbol}
/stocks/{symbol}/dividends/data
/stocks/{symbol}/dividends/history
/stocks/{symbol}/fundamentals

Notes:
-This project is intended as a demo work of an AWS hosted Spring boot REST web service, along with use of caching and HTMLUnit.  

-The fact that data comes from HTMLUnit reading the source webpage means that requests are fairly slow on first call.  Results are cached for the day (since the data is daily granularity) to greatly speed up future requests.

Todo: Add additional fields returned for each endpoint, JUnit tests, robustness, and cleanup.