package com.ceazy.poundme;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

public class DateFormatter {
	
	PeriodFormatter formatter;
	
	public DateFormatter() {

	}
	
	public String getTimePassed(long time) {
		Period timePassed = new Period(System.currentTimeMillis() - time);
		return parsePeriod(timePassed);
	}
	
	public String parsePeriod(Period period) {
		int seconds = period.toStandardSeconds().getSeconds();
		if(seconds < 60) {
			return createStringTime(seconds, "s");
		} 
		int minutes = period.toStandardMinutes().getMinutes();
		if(minutes < 60) {
			return createStringTime(minutes, "min");
		}
		int hours = period.toStandardHours().getHours();
		if(hours < 24) {
			return createStringTime(hours, "h");
		}
		int days = period.toStandardDays().getDays();
		if(days < 7) {
			return createStringTime(days, "d");
		}
		int weeks = period.toStandardWeeks().getWeeks();
		if(weeks < 4) {
			return createStringTime(weeks, "w");
		}
		int months = period.getMonths();
		int years = period.getYears();
		if(years < 1) {
			return createStringTime(months, "m");
		} else {
			return createStringTime(years, "y");
		}
	}
	
	public String createStringTime(int time, String unit) {
		StringBuilder builder = new StringBuilder(String.valueOf(time));
		builder.append(unit + " ago");
		return builder.toString();
	}
	

}
