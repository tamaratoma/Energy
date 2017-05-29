package fowler.energybilling;

import java.util.Calendar;
import java.util.Date;

public abstract class Site {

	protected Zone _zone;
	protected Reading[] readings = new Reading[1000];

	// abstract int dayOfYear(Calendar arg);

	protected Dollars taxes(Dollars arg) {
		return new Dollars(arg.times(TAX_RATE));
	}

	protected static final double TAX_RATE = 0.05;

	Site(Zone zone) {
		_zone = zone;
	}

	public void addReading(Reading newReading) {

		readings[firstUnusedReadingsIndex()] = newReading;
	}

	private int firstUnusedReadingsIndex() {
		int i = 0;
		while (readings[i] != null) {
			i++;
		}
		return i;
	}

	protected Dollars charge() {
		return baseCharge().plus(taxes()).plus(fuelCharge()).plus(fuelChargeTaxes());
	}

	protected Dollars taxes() {
		return new Dollars(baseCharge().times(TAX_RATE));
	}

	abstract protected Dollars baseCharge();

	abstract protected Dollars fuelChargeTaxes();

	/*
	 * public Dollars charge() { Calendar end = lastReading().date(); Calendar
	 * start = nextDay(previousReading().date()); return charge(start, end); }
	 */

	/*
	 * private Date nextDay (Date arg) { // foreign method - should be in Date
	 * Date result = new Date (arg.getTime()) ; result.setDate(result.getDate()
	 * + 1); return result; }
	 */

	protected Calendar nextDay(Calendar arg) {
		// foreign method - should be in Date
		Calendar result = Calendar.getInstance();
		result.setTime(arg.getTime());
		result.add(Calendar.DATE, 1);
		return result;
	}

	Dollars residentialBaseCharge(int usage) {
		return new Dollars((usage * _zone.getSummerRate() * summerFraction())
				+ (usage * _zone.getWinterRate() * (1 - summerFraction())));
	}

	private MfDate nextDay(MfDate arg) {
		// foreign method - should be on Date
		MfDate result = arg.copy();
		result.setDate(result.getDate() + 1);
		return result;
	}

	protected int lastUsage() {
		return lastReading().amount() - previousReading().amount();
	};

	protected Reading previousReading() {
		return readings[firstUnusedReadingsIndex() - 2];
	}

	protected Reading lastReading() {
		return readings[firstUnusedReadingsIndex() - 1];
	};

	protected static final double FUEL_CHARGE_RATE = 0.0175;

	/*
	 * protected Dollars fuelCharge() { return new Dollars(lastUsage() *
	 * FUEL_CHARGE_RATE); }
	 */

	protected Dollars fuelCharge() {
		return new Dollars(lastUsage() * 0.0175);
	}

	public DateRange lastPeriod() {
		return new DateRange(previousReading().date().nextDay(), lastReading().date());
	}

	protected double summerFraction() {
		DateRange periodInSummer = lastPeriod().intersection(_zone.summer());
		return (double) periodInSummer.length() / lastPeriod().length();
	}

	protected boolean isLastPeriodOutsideSummer() {
		return lastPeriod().start().after(_zone.summer().end()) || lastPeriod().end().before(_zone.summer().start());
	}

	protected int dayOfYear(Calendar end) {
		// TODO Auto-generated method stub
		return 0;
	}

}
