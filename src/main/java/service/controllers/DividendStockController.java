package service.controllers;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import service.HTMLUnitDataSource;
import service.models.DividendData;
import service.models.DividendHistory;
import service.models.FundamentalData;
import service.models.StockData;

/**
 * Controller class for all the dividend stock service calls.
 * Results will be cached for the day to reduce slow HTMLUnit requests.
 */

@RestController
@CacheConfig(cacheNames={"stockData", "fundamentalData", "dividendData", "dividendHistory"})
public class DividendStockController {

	@Autowired
	private HTMLUnitDataSource dataSource;
	
    private final AtomicLong counter = new AtomicLong();
    private static final Logger log = LoggerFactory.getLogger(DividendStockController.class);
    
    /**
     * Returns general information about the stock with the given ticker symbol.
     * 
     * @param symbol
     */
    @Cacheable("stockData")
    @RequestMapping(value = "/stocks/{symbol}", method = RequestMethod.GET)
    public ResponseEntity<StockData> getStockOverview(@PathVariable("symbol") String symbol) {
    	log.debug("Getting uncached stock data for:" + symbol);
    	
    	counter.incrementAndGet();

        StockData data = dataSource.createStockData(symbol.toUpperCase());
        
    	if (data != null)
    		return ResponseEntity.status(HttpStatus.OK).body(data);
    	else
    		return ResponseEntity.notFound().build();
    }
    
    /**
     * Returns dividend information about the stock with the given ticker symbol.
     * 
     * @param symbol
     */
    @Cacheable("dividendData")
    @RequestMapping(value = "/stocks/{symbol}/dividends/data", method = RequestMethod.GET)
    public ResponseEntity<DividendData> getDividendData(@PathVariable("symbol") String symbol) {
    	log.debug("Getting uncached dividend data for:" + symbol);
    	
    	counter.incrementAndGet();
    	
        DividendData data = dataSource.createDividendData(symbol.toUpperCase());
        
    	if (data != null)
    		return ResponseEntity.status(HttpStatus.OK).body(data);
    	else
    		return ResponseEntity.notFound().build();
    }
    
    /**
     * Returns the history of dividend payments for the stock with the given ticker symbol.
     * 
     * @param symbol
     */
    @Cacheable("dividendHistory")
    @RequestMapping(value = "/stocks/{symbol}/dividends/history", method = RequestMethod.GET)
    public ResponseEntity<DividendHistory> getDividendHistory(@PathVariable("symbol") String symbol) {
    	log.debug("Getting uncached dividend history for:" + symbol);
    	
    	counter.incrementAndGet();
    	
        DividendHistory history = dataSource.createDividendHistory(symbol.toUpperCase());
        
    	if (history != null)
    		return ResponseEntity.status(HttpStatus.OK).body(history);
    	else
    		return ResponseEntity.notFound().build();
    }
    
    /**
     * Returns fundamentals about the stock with the given ticker symbol.
     * 
     * @param symbol
     */
    @Cacheable("fundamentalData")
    @RequestMapping(value = "/stocks/{symbol}/fundamentals", method = RequestMethod.GET)
    public ResponseEntity<FundamentalData> getFundamentals(@PathVariable("symbol") String symbol) {
    	log.debug("Getting uncached fundamental data for:" + symbol);
    	
    	counter.incrementAndGet();
    	
        FundamentalData data = dataSource.createFundamentalData(symbol.toUpperCase());
        
    	if (data != null)
    		return ResponseEntity.status(HttpStatus.OK).body(data);
    	else
    		return ResponseEntity.notFound().build();
    }
    
    /**
     * Returns the uncached use count for debugging.
     */
    @RequestMapping(value = "/uncachedUseCount", method = RequestMethod.GET)
    public long getUncachedUseCount() {
        return counter.get();
    }
}
