package service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * Controller class for all the dividend stock service calls.
 * 
 * Data is pulled via HTMLUnit from Dividata.com pages. Results will be cached for the day to reduce slow HTMLUnit requests.
 * Originally the plan was to pull data from Tradier's service, but they do not provide dividend data under free subscription.
 * I cannot find any other free service providing dividend history, hence the Dividata page parsing.
 * Unfortunately, Dividata does not give IDs to their form elements, so the parsing is rather fragile.
 */

@RestController
@CacheConfig(cacheNames={"stockData", "fundamentalData", "dividendData", "dividendHistory"})
public class DividendStockController {

	private final String DIVIDATA_URL = "https://dividata.com/stock/";
    private final AtomicLong counter = new AtomicLong();
    private static final Logger log = LoggerFactory.getLogger(DividendStockController.class);
    
    /**
     * Returns general information about the stock with the given ticker symbol.
     * 
     * @param symbol
     */
    @Cacheable("stockData")
    @RequestMapping("/stocks/{symbol}")
    public StockData getStockOverview(@PathVariable("symbol") String symbol) {
    	log.debug("Getting uncached stock data for:" + symbol);
    	
    	counter.incrementAndGet();

        return createStockData(symbol.toUpperCase());
    }
    
    /**
     * Returns dividend information about the stock with the given ticker symbol.
     * 
     * @param symbol
     */
    @Cacheable("dividendData")
    @RequestMapping("/stocks/{symbol}/dividends/data")
    public DividendData getDividendData(@PathVariable("symbol") String symbol) {
    	log.debug("Getting uncached dividend data for:" + symbol);
    	
    	counter.incrementAndGet();
    	
        return createDividendData(symbol.toUpperCase());
    }
    
    /**
     * Returns the history of dividend payments for the stock with the given ticker symbol.
     * 
     * @param symbol
     */
    @Cacheable("dividendHistory")
    @RequestMapping("/stocks/{symbol}/dividends/history")
    public DividendHistory getDividendHistory(@PathVariable("symbol") String symbol) {
    	log.debug("Getting uncached dividend history for:" + symbol);
    	
    	counter.incrementAndGet();
    	
        return createDividendHistory(symbol.toUpperCase());
    }
    
    /**
     * Returns fundamentals about the stock with the given ticker symbol.
     * 
     * @param symbol
     */
    @Cacheable("fundamentalData")
    @RequestMapping("/stocks/{symbol}/fundamentals")
    public FundamentalData getFundamentals(@PathVariable("symbol") String symbol) {
    	log.debug("Getting uncached fundamental data for:" + symbol);
    	
    	counter.incrementAndGet();
    	
        return createFundamentalData(symbol.toUpperCase());
    }
    
    /**
     * Returns the uncached use count for debugging.
     */
    @RequestMapping("/uncachedUseCount")
    public long getUncachedUseCount() {
        return counter.get();
    }
    
    /**
     * Uses HTMLUnit to pull data from Dividata.com
     * @param symbol
     * @return
     */
    private StockData createStockData(String symbol) {
    	StockData stockData = new StockData();
    	WebClient webClient = createWebClient();
    	
    	try {
	    	if (symbol != null && symbol.trim() != "") {
	    		stockData.setSymbol(symbol);

	    		HtmlPage page = webClient.getPage(DIVIDATA_URL.concat(symbol));
	    		
	    		if (page != null)
	    		{
	    			HtmlSpan span = page.getFirstByXPath("//li[text()='Sector']/span");
		    		if (span != null) {
			    		stockData.setSector(span.getTextContent());
		    		}
		    		
		    		span = page.getFirstByXPath("//li[text()='Industry']/span");
		    		if (span != null) {
			    		stockData.setIndustry(span.getTextContent());
		    		}
		    		
		    		span = page.getFirstByXPath("//li[text()=' Exchange']/span");
		    		if (span != null) {
		    			stockData.setExchange(span.getTextContent());
		    		}
		    		
		    		HtmlElement h2 = page.getFirstByXPath("//h2");
		    		if (h2 != null) {
		    			stockData.setName(h2.getTextContent());
		    		}
	    		}
	    	}
    	}
    	catch (Exception e) {
    		log.debug("exception:"+e.toString());
    	}
    	finally {
    		webClient.close();
    	}
    	
    	return stockData;
    }
    
    /**
     * Uses HTMLUnit to pull data from Dividata.com
     * @param symbol
     * @return
     */
    private DividendData createDividendData(String symbol) {
    	DividendData dividendData = new DividendData();
    	WebClient webClient = createWebClient();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
    	
    	try {
	    	if (symbol != null && symbol.trim() != "") {
	    		dividendData = new DividendData();
	    		
	    		HtmlPage page = webClient.getPage(DIVIDATA_URL.concat(symbol));
	    		
	    		if (page != null)
	    		{
	    			HtmlSpan span = page.getFirstByXPath("//li/abbr[text()='52 Week Dividend']/following-sibling::span");
		    		if (span != null) {
		    			BigDecimal dividend = new BigDecimal(span.getTextContent().replace("$", ""));
		    			dividendData.setEstimatedForwardAnnualDividend(dividend);
		    			
		    			//The yield % currently reported on Dividata is off by 100x and lacks the precision to scale it up,
		    			//so will calculate yield value ourselves
		    			span = page.getFirstByXPath("//li[text()=' Last Close']/span");
			    		if (span != null) {
				    		BigDecimal price = new BigDecimal(span.getTextContent().replace("$", ""));
				    		BigDecimal yield = dividend.scaleByPowerOfTen(2).divide(price, 2, RoundingMode.HALF_EVEN);
				    		
				    		dividendData.setEstimatedForwardAnnualYield(yield);
			    		}
		    		}
		    		
		    		span = page.getFirstByXPath("//li/abbr[text()='Last Dividend']/following-sibling::span");
		    		if (span != null) {
		    			dividendData.setLastDividend(new BigDecimal(span.getTextContent().replace("$", "")));
		    		}
		    		
		    		span = page.getFirstByXPath("//li/abbr[text()='Last Ex-Dividend Date']/following-sibling::span");
		    		if (span != null) {
		    			dividendData.setLastExDividendDate((LocalDate.parse(span.getTextContent(), formatter)));
		    		}
		    		
		    		span = page.getFirstByXPath("//li/abbr[text()='Pay Date']/following-sibling::span");
		    		if (span != null) {
		    			dividendData.setLastPayDate((LocalDate.parse(span.getTextContent(), formatter)));
		    		}
		    		
		    		span = page.getFirstByXPath("//li/abbr[text()='Years Paying']/following-sibling::span");
		    		if (span != null) {
		    			if (!span.getTextContent().equalsIgnoreCase("N/A")) {
		    				dividendData.setYearsPaying(new Integer(span.getTextContent()));
		    			}
		    		}
	    		}
	    	}
    	}
    	catch (Exception e) {
    		log.debug("exception:"+e.toString());
    	}
    	finally {
    		webClient.close();
    	}
    	
    	return dividendData;
    }
    
    /**
     * Uses HTMLUnit to pull data from Dividata.com
     * @param symbol
     * @return
     */
    private DividendHistory createDividendHistory(String symbol) {
    	DividendHistory dividendHistory = new DividendHistory();
    	WebClient webClient = createWebClient();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    	
    	try {
	    	if (symbol != null && symbol.trim() != "") {
	    		HtmlPage page = webClient.getPage(DIVIDATA_URL.concat(symbol).concat("/dividend"));
	    		
	    		if (page != null)
	    		{
	    			HtmlTable table = page.getFirstByXPath("//table");
	    			boolean header = true;
	    			
	    			for (HtmlTableRow row : table.getRows()) {
	    				if (!header) {
	    					dividendHistory.addDividendPayment(
	    							LocalDate.parse(row.getCell(0).getTextContent(), formatter), 
	    							new BigDecimal(row.getCell(1).getTextContent().replace("$", "")));
	    				}
	    				else {
	    					header = false;
	    				}
	    			}
	    		}
	    	}
		}
		catch (Exception e) {
			log.debug("exception:"+e.toString());
		}
		finally {
			webClient.close();
		}
    	
    	return dividendHistory;
    }
    
    /**
     * Uses HTMLUnit to pull data from Dividata.com
     * @param symbol
     * @return
     */
    private FundamentalData createFundamentalData(String symbol) {
    	FundamentalData fundamentalData = new FundamentalData();
    	WebClient webClient = createWebClient();
    	
    	try {
	    	if (symbol != null && symbol.trim() != "") {
	    		HtmlPage page = webClient.getPage(DIVIDATA_URL.concat(symbol));
	    		
	    		if (page != null)
	    		{
	    			HtmlSpan span = page.getFirstByXPath("//li[text()=' Last Close']/span");
		    		if (span != null) {
			    		fundamentalData.setLastClose(new BigDecimal(span.getTextContent().replace("$", "")));
		    		}
		    		
	    			span = page.getFirstByXPath("//li[text()=' Last Open']/span");
		    		if (span != null) {
			    		fundamentalData.setLastOpen(new BigDecimal(span.getTextContent().replace("$", "")));
		    		}
	    			
	    			span = page.getFirstByXPath("//li[text()=' P/E Ratio']/span");
		    		if (span != null) {
			    		fundamentalData.setPeRatio(new BigDecimal(span.getTextContent()));
		    		}
		    		
	    			span = page.getFirstByXPath("//li[text()=' Volume']/span");
		    		if (span != null) {
			    		fundamentalData.setVolume(new Double(span.getTextContent().replaceAll(",", "")));
		    		}
	    		}
	    	}
		}
		catch (Exception e) {
			log.debug("exception:"+e.toString());
		}
		finally {
			webClient.close();
		}
    	
    	return fundamentalData;
    }
    
    private WebClient createWebClient() {
    	WebClient webClient = new WebClient();
    	webClient.getOptions().setCssEnabled(false);
    	webClient.getOptions().setJavaScriptEnabled(false);
    	
    	return webClient;
    }
}
