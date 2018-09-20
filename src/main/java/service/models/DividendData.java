package service.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DividendData {
	private LocalDate lastPayDate;
	private LocalDate lastExDividendDate;
	private BigDecimal lastDividend;
	private BigDecimal estimatedForwardAnnualDividend;
	private BigDecimal estimatedForwardAnnualYield;
	private int yearsPaying;
	
	public DividendData() {
		
	}
	
	public LocalDate getLastPayDate() {
		return lastPayDate;
	}
	public void setLastPayDate(LocalDate lastPayDate) {
		this.lastPayDate = lastPayDate;
	}
	public LocalDate getLastExDividendDate() {
		return lastExDividendDate;
	}
	public void setLastExDividendDate(LocalDate lastExDividendDate) {
		this.lastExDividendDate = lastExDividendDate;
	}
	public BigDecimal getLastDividend() {
		return lastDividend;
	}
	public void setLastDividend(BigDecimal lastDividend) {
		this.lastDividend = lastDividend;
	}
	public BigDecimal getEstimatedForwardAnnualDividend() {
		return estimatedForwardAnnualDividend;
	}
	public void setEstimatedForwardAnnualDividend(BigDecimal estimatedForwardAnnualDividend) {
		this.estimatedForwardAnnualDividend = estimatedForwardAnnualDividend;
	}
	public BigDecimal getEstimatedForwardAnnualYield() {
		return estimatedForwardAnnualYield;
	}
	public void setEstimatedForwardAnnualYield(BigDecimal estimatedForwardAnnualYield) {
		this.estimatedForwardAnnualYield = estimatedForwardAnnualYield;
	}
	public int getYearsPaying() {
		return yearsPaying;
	}
	public void setYearsPaying(int yearsPaying) {
		this.yearsPaying = yearsPaying;
	}
}
