This project is a REST service that supplies data about the dividend stock with the provided ticker symbol.
I was unable to find any previously existing free web service that supplies full dividend history (the data I was personally interested in using), this project fills that gap.  However, without an official exchange connection, my only free source for the data was to scrape values from an existing web page (dividata.com), and this was done via HTMLUnit.  Additional endpoints also provide relevant data to be used with the dividend history.

The available service endpoints are:
/stocks/{symbol}
/stocks/{symbol}/dividends/data
/stocks/{symbol}/dividends/history
/stocks/{symbol}/fundamentals

A running copy has been deployed to AWS at:
http://dividendstockservice-env.gpdfnadfve.us-west-2.elasticbeanstalk.com

Notes:
-This project is intended as a simple demo work of a Spring Boot REST web service, along with use of caching and HTMLUnit.

-The fact that data comes from HTMLUnit reading the source webpage means that uncached requests are fairly slow on first call, especially when only using AWS's free tier.  Results are cached for each day (since the data is daily granularity) to greatly speed up future requests.
