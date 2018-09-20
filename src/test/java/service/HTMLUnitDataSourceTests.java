package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import service.models.DividendData;
import service.models.DividendHistory;
import service.models.FundamentalData;
import service.models.StockData;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HTMLUnitDataSourceTests {
	
	@Autowired
	HTMLUnitDataSource dataSource;

	@Test
	public void testCreateStockDataExists() {
		StockData result = dataSource.createStockData("AAPL");
		
		assertNotNull(result);
		assertEquals("AAPL", result.getSymbol());
		assertEquals("Apple Inc", result.getName());
		assertEquals("Consumer Electronics", result.getIndustry());
		assertEquals("NASDAQ", result.getExchange());
		assertEquals("Technology", result.getSector());
		
	}
	
	@Test
	public void testCreateDividendDataExists() {
		DividendData result = dataSource.createDividendData("AAPL");
		
		assertNotNull(result);
		assertNotNull(result.getLastDividend());
		assertNotNull(result.getLastPayDate());
	}
	
	@Test
	public void testCreateDividendHistoryExists() {
		DividendHistory result = dataSource.createDividendHistory("AAPL");
		
		assertNotNull(result);
		assertNotNull(result.getDividends());
		assertFalse(result.getDividends().isEmpty());
		assertTrue(LocalDate.of(1987, Month.NOVEMBER, 17).equals(result.getDividends().get(0).getDate()));
		assertTrue(new BigDecimal("0.080").equals(result.getDividends().get(0).getDividend()));
	}
	
	@Test
	public void testCreateFundamentalDataExists() {
		FundamentalData result = dataSource.createFundamentalData("AAPL");
		
		assertNotNull(result);
		assertNotNull(result.getLastClose());
		assertNotNull(result.getLastOpen());
		assertNotNull(result.getPeRatio());
		assertTrue(result.getVolume() >= 0);
	}
	
	@Test
	public void testCreateStockDataDoesntExist() {
		StockData result = dataSource.createStockData("AAAPL");
		
		assertNull(result);
	}
	
	@Test
	public void testCreateDividendDataDoesntExist() {
		DividendData result = dataSource.createDividendData("AAAPL");
		
		assertNull(result);
	}
	
	@Test
	public void testCreateDividendHistoryDoesntExist() {
		DividendHistory result = dataSource.createDividendHistory("AAAPL");
		
		assertNull(result);
	}
	
	@Test
	public void testCreateFundamentalDataDoesntExist() {
		FundamentalData result = dataSource.createFundamentalData("AAAPL");
		
		assertNull(result);
	}

}
