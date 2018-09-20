package service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import service.models.DividendData;
import service.models.DividendHistory;
import service.models.FundamentalData;
import service.models.StockData;

/**
 * A source for the dividend data returned by the service.  In this case, from using HTMLUnit to read Dividata.com.
 * 
 * Note: Dividata only has entries for dividend stocks, not all stocks.  Non dividend stocks will return a 404.
 * 
 * Originally the plan was to pull data from Tradier's service, but they do not provide dividend data under free subscription.
 * I cannot find any other free service providing dividend history, hence the Dividata page parsing.
 * Unfortunately, Dividata does not give IDs to their form elements, so the parsing is rather fragile.
 */
@Service
public class HTMLUnitDataSource {
	private static final String DIVIDATA_URL = "https://dividata.com/stock/";
	private static final DateTimeFormatter MDYFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
	private static final DateTimeFormatter MDYCommaFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final Logger log = LoggerFactory.getLogger(HTMLUnitDataSource.class);
	
    /**
     * Uses HTMLUnit to pull data from Dividata.com
     * @param symbol
     * @return
     */
    public StockData createStockData(String symbol) {
    	WebClient webClient = createWebClient();
    	
    	try {
	    	if (symbol != null && symbol.trim() != "") {
	    		HtmlPage page = webClient.getPage(DIVIDATA_URL.concat(symbol));
	    		
	    		if (page != null)
	    		{
	    			StockData stockData = new StockData();
	    			stockData.setSymbol(symbol);
	    			
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
		    		
		    		return stockData;
	    		}
	    	}
    	}
    	catch (FailingHttpStatusCodeException e ) {
    		return null;
    	}
    	catch (Exception e) {
    		log.debug("exception:"+e.toString());
    	}
    	finally {
    		webClient.close();
    	}
    	
    	return null;
    }
    
    /**
     * Uses HTMLUnit to pull data from Dividata.com
     * @param symbol
     * @return
     */
    public DividendData createDividendData(String symbol) {
    	WebClient webClient = createWebClient();
    	
    	try {
	    	if (symbol != null && symbol.trim() != "") {
	    		HtmlPage page = webClient.getPage(DIVIDATA_URL.concat(symbol));
	    		
	    		if (page != null)
	    		{
	    			DividendData dividendData = new DividendData();
	    			
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
		    			dividendData.setLastExDividendDate((LocalDate.parse(span.getTextContent(), MDYFormatter)));
		    		}
		    		
		    		span = page.getFirstByXPath("//li/abbr[text()='Pay Date']/following-sibling::span");
		    		if (span != null) {
		    			dividendData.setLastPayDate((LocalDate.parse(span.getTextContent(), MDYFormatter)));
		    		}
		    		
		    		span = page.getFirstByXPath("//li/abbr[text()='Years Paying']/following-sibling::span");
		    		if (span != null) {
		    			if (!span.getTextContent().equalsIgnoreCase("N/A")) {
		    				dividendData.setYearsPaying(new Integer(span.getTextContent()));
		    			}
		    		}
		    		
		    		return dividendData;
	    		}
	    	}
    	}
    	catch (FailingHttpStatusCodeException e ) {
    		return null;
    	}
    	catch (Exception e) {
    		log.debug("exception:"+e.toString());
    	}
    	finally {
    		webClient.close();
    	}
    	
    	return null;
    }
    
    /**
     * Uses HTMLUnit to pull data from Dividata.com
     * @param symbol
     * @return
     */
    public DividendHistory createDividendHistory(String symbol) {
    	WebClient webClient = createWebClient();
    	
    	try {
	    	if (symbol != null && symbol.trim() != "") {
	    		HtmlPage page = webClient.getPage(DIVIDATA_URL.concat(symbol).concat("/dividend"));
	    		
	    		if (page != null)
	    		{
	    			DividendHistory dividendHistory = new DividendHistory();
	    			
	    			HtmlTable table = page.getFirstByXPath("//table");
	    			boolean header = true;
	    			
	    			for (HtmlTableRow row : table.getRows()) {
	    				if (!header) {
	    					dividendHistory.addDividendPayment(
	    							LocalDate.parse(row.getCell(0).getTextContent(), MDYCommaFormatter), 
	    							new BigDecimal(row.getCell(1).getTextContent().replace("$", "")));
	    				}
	    				else {
	    					header = false;
	    				}
	    			}
	    			
	    			return dividendHistory;
	    		}
	    	}
		}
    	catch (FailingHttpStatusCodeException e ) {
    		return null;
    	}
		catch (Exception e) {
			log.debug("exception:"+e.toString());
		}
		finally {
			webClient.close();
		}
    	
    	return null;
    }
    
    /**
     * Uses HTMLUnit to pull data from Dividata.com
     * @param symbol
     * @return
     */
    public FundamentalData createFundamentalData(String symbol) {
    	WebClient webClient = createWebClient();
    	
    	try {
	    	if (symbol != null && symbol.trim() != "") {
	    		HtmlPage page = webClient.getPage(DIVIDATA_URL.concat(symbol));
	    		
	    		if (page != null)
	    		{
	    			FundamentalData fundamentalData = new FundamentalData();
	    			
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
		    		
		    		return fundamentalData;
	    		}
	    	}
		}
    	catch (FailingHttpStatusCodeException e ) {
    		return null;
    	}
		catch (Exception e) {
			log.debug("exception:"+e.toString());
		}
		finally {
			webClient.close();
		}
    	
    	return null;
    }
    
    /**
     * Create an HTMLUnit WebClient with scripting disabled to increase performance
     * @return
     */
    public WebClient createWebClient() {
    	WebClient webClient = new WebClient();
    	webClient.getOptions().setCssEnabled(false);
    	webClient.getOptions().setJavaScriptEnabled(false);
    	
    	return webClient;
    }
}
