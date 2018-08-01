package service;

import java.math.BigDecimal;

public class FundamentalData {
	private BigDecimal lastOpen;
	private BigDecimal lastClose;
	private double volume;
	private BigDecimal PeRatio;
	
	public FundamentalData() {
		
	}

	public BigDecimal getLastOpen() {
		return lastOpen;
	}

	public void setLastOpen(BigDecimal lastOpen) {
		this.lastOpen = lastOpen;
	}

	public BigDecimal getLastClose() {
		return lastClose;
	}

	public void setLastClose(BigDecimal lastClose) {
		this.lastClose = lastClose;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public BigDecimal getPeRatio() {
		return PeRatio;
	}

	public void setPeRatio(BigDecimal peRatio) {
		PeRatio = peRatio;
	}
	
	
}
