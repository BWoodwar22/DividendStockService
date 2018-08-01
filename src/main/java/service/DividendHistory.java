package service;

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
		dividends.sort(new Comparator<DividendPayment>() {
			@Override
			public int compare(DividendPayment o1, DividendPayment o2) {
				if(o2.getDate().isAfter(o1.getDate())) {
					return 1; 
				}
				else if(o2.getDate().isBefore(o1.getDate())) {
					return -1; 
				}
				else return 0;
			}
		});	
		
		return dividends;
	}
	
	public void addDividendPayment(LocalDate date, BigDecimal dividend) {
		dividends.add(new DividendPayment(date, dividend));
	}


	class DividendPayment {
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
