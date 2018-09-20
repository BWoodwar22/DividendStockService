package service.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DividendHistory {
	private List<DividendPayment> dividends;
	
	public DividendHistory() {
		dividends = new ArrayList<DividendPayment>();
	}
	
	public List<DividendPayment> getDividends() {
		dividends.sort((d1, d2) -> d1.getDate().compareTo(d2.getDate()));
		
		return dividends;
	}
	
	public void addDividendPayment(LocalDate date, BigDecimal dividend) {
		dividends.add(new DividendPayment(date, dividend));
	}


	public class DividendPayment {
		private LocalDate date;
		private BigDecimal dividend;
		
		public DividendPayment() {
			
		}

		public DividendPayment(LocalDate date, BigDecimal dividend) {
			this.date = date;
			this.dividend = dividend;
		}

		public LocalDate getDate() {
			return date;
		}

		public BigDecimal getDividend() {
			return dividend;
		}
	}
}
