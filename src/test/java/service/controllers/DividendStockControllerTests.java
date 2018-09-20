package service.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import service.HTMLUnitDataSource;
import service.controllers.*;
import service.models.*;

@RunWith(SpringRunner.class)
@WebMvcTest(DividendStockController.class)
public class DividendStockControllerTests {
	
    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private HTMLUnitDataSource dataSource;
    
	@Mock
	private StockData stockData = null;
	
	@Mock
	private DividendData dividendData = null;
	
	@Mock
	private DividendHistory dividendHistory = null;
	
	@Mock
	private FundamentalData fundamentalData = null;
    
    private JacksonTester<StockData> jsonStockData;
    private JacksonTester<DividendData> jsonDividendData;
    private JacksonTester<DividendHistory> jsonDividendHistory;
    private JacksonTester<FundamentalData> jsonFundamentalData;
 
    @Before
    public void setup() {
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JacksonTester.initFields(this, mapper);
    }

    @Test
    public void testGetStockDataExists() throws Exception {
    	stockData = new StockData();
    	stockData.setExchange("exchange");
    	stockData.setIndustry("industry");
    	stockData.setName("name");
    	stockData.setSector("sector");
    	stockData.setSymbol("symbol");
    	
        given(dataSource.createStockData("AAPL")).willReturn(stockData);

        MockHttpServletResponse response = mvc.perform(
        		get("/stocks/AAPL")
        		.accept(MediaType.APPLICATION_JSON))
        		.andReturn().getResponse();
 
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonStockData.write(stockData).getJson());
    }
    
    @Test
    public void testGetDividendDataExists() throws Exception {
    	dividendData = new DividendData();
    	dividendData.setEstimatedForwardAnnualDividend(new BigDecimal("2.00"));
    	dividendData.setEstimatedForwardAnnualYield(new BigDecimal("2.50"));
    	dividendData.setLastDividend(new BigDecimal(".50"));
    	dividendData.setLastExDividendDate(LocalDate.of(2018, Month.JUNE, 1));
    	dividendData.setLastPayDate(LocalDate.of(2018, Month.JUNE, 4));
    	dividendData.setYearsPaying(15);
    	
        given(dataSource.createDividendData("AAPL")).willReturn(dividendData);

        MockHttpServletResponse response = mvc.perform(
        		get("/stocks/AAPL/dividends/data")
        		.accept(MediaType.APPLICATION_JSON))
        		.andReturn().getResponse();
 
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonDividendData.write(dividendData).getJson());
    }
    
    @Test
    public void testGetDividendHistoryExists() throws Exception {
    	dividendHistory = new DividendHistory();
    	dividendHistory.addDividendPayment(LocalDate.of(2018, Month.JANUARY, 1), new BigDecimal(".40"));
    	dividendHistory.addDividendPayment(LocalDate.of(2018, Month.APRIL, 1), new BigDecimal(".45"));
    	
        given(dataSource.createDividendHistory("AAPL")).willReturn(dividendHistory);

        MockHttpServletResponse response = mvc.perform(
        		get("/stocks/AAPL/dividends/history")
        		.accept(MediaType.APPLICATION_JSON))
        		.andReturn().getResponse();
 
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonDividendHistory.write(dividendHistory).getJson());
    }
    
    @Test
    public void testGetFundamentalsExists() throws Exception {
    	fundamentalData = new FundamentalData();
    	fundamentalData.setLastClose(new BigDecimal("199.90"));
    	fundamentalData.setLastOpen(new BigDecimal("200.50"));
    	fundamentalData.setPeRatio(new BigDecimal("7.9"));
    	fundamentalData.setVolume(200000);
    	
        given(dataSource.createFundamentalData("AAPL")).willReturn(fundamentalData);

        MockHttpServletResponse response = mvc.perform(
        		get("/stocks/AAPL/fundamentals")
        		.accept(MediaType.APPLICATION_JSON))
        		.andReturn().getResponse();
 
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonFundamentalData.write(fundamentalData).getJson());
    }
    
    @Test
    public void testGetStockDataDoesntExist() throws Exception {
        given(dataSource.createStockData("AAPL")).willReturn(null);

        MockHttpServletResponse response = mvc.perform(
        		get("/stocks/AAPL")
        		.accept(MediaType.APPLICATION_JSON))
        		.andReturn().getResponse();
 
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo("");
    }
    
    @Test
    public void testGetDividendDataDoesntExist() throws Exception {
        given(dataSource.createDividendData("AAPL")).willReturn(null);

        MockHttpServletResponse response = mvc.perform(
        		get("/stocks/AAPL/dividends/data")
        		.accept(MediaType.APPLICATION_JSON))
        		.andReturn().getResponse();
 
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo("");
    }
    
    @Test
    public void testGetDividendHistoryDoesntExist() throws Exception {
        given(dataSource.createDividendHistory("AAPL")).willReturn(null);

        MockHttpServletResponse response = mvc.perform(
        		get("/stocks/AAPL/dividends/history")
        		.accept(MediaType.APPLICATION_JSON))
        		.andReturn().getResponse();
 
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo("");
    }
    
    @Test
    public void testGetFundamentalsDoesntExist() throws Exception {
        given(dataSource.createFundamentalData("AAPL")).willReturn(null);

        MockHttpServletResponse response = mvc.perform(
        		get("/stocks/AAPL/fundamentals")
        		.accept(MediaType.APPLICATION_JSON))
        		.andReturn().getResponse();
 
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo("");
    }
    
}
