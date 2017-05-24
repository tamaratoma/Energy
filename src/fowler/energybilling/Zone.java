package fowler.energybilling;

import java.util.Calendar;

public class Zone {
	private String name;
	private Calendar summerEnd;
	private Calendar summerStart;
	private double winterRate;
	private double summerRate;

	public Zone(String name, double summerRate, double winterRate, Calendar summerStart, Calendar summerEnd) {
		this.name = name;
		this.summerRate = summerRate;
		this.winterRate = winterRate;
		this.summerStart = summerStart;
		this.summerEnd = summerEnd;
	};

	public Zone persist() {
		Registry.add(this);
		return this;
	}

	public DateRange summer() {
		return new DateRange(summerStart, summerEnd);
	}

	double summerFraction(DateRange usagePeriod) {
		DateRange periodInSummer = usagePeriod.intersection(summer());
		return (double) periodInSummer.length() / usagePeriod.length();
	}

	Dollars baseCharge(int usage, DateRange usagePeriod) {
		return new Dollars((usage * summerRate * summerFraction(usagePeriod))
				+ (usage * winterRate * (1 - summerFraction(usagePeriod))));
	}

	public static Zone get(String name) {
		return Registry.get(name);
	}

	public Calendar getSummerEnd() {
		return summerEnd;
	}

	public Calendar getSummerStart() {
		return summerStart;
	}

	public double getWinterRate() {
		return winterRate;
	}

	public double getSummerRate() {
		return summerRate;
	}

	public String getName() {
		return this.name;

	}

}
