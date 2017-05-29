package fowler.energybilling;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public abstract class Site {

	protected Zone _zone;
	private Vector _readings = new Vector();
	// abstract int dayOfYear(Calendar arg);

	protected Dollars taxes() {
		Dollars taxable = baseCharge().plus(fuelCharge());
		Dollars result = new Dollars(taxable.min(new Dollars(50)).times(0.07));
		if (taxable.isGreaterThan(new Dollars(50))) {
			result = new Dollars(result.plus(taxable.min(new Dollars(75)).minus(new Dollars(50)).times(0.06)));
		}
		;
		if (taxable.isGreaterThan(new Dollars(75))) {
			result = new Dollars(result.plus(taxable.minus(new Dollars(75)).times(0.05)));
		}
		;
		return result;
	}

	protected static final double TAX_RATE = 0.05;

	Site(Zone zone) {
		_zone = zone;
	}

	private int firstUnusedReadingsIndex() {
		return _readings.size();
	}

	protected int lastUsage() {
		return lastReading().amount() - previousReading().amount();
	}

	public Reading lastReading() {
		return (Reading) _readings.lastElement();
	}

	public Reading previousReading() {
		return (Reading) _readings.elementAt(_readings.size() - 2);
	}

	public Dollars charge() {
		return baseCharge().plus(fuelCharge()).plus(taxes());
	}

	protected Dollars baseCharge() {
		double result = usageInRange(0, 100) * 0.03;
		result += usageInRange(100, 200) * 0.05;
		result += usageInRange(200, Integer.MAX_VALUE) * 0.07;
		return new Dollars(result);
	}

	protected int usageInRange(int start, int end) {
		if (lastUsage() > start)
			return Math.min(lastUsage(), end) - start;
		else
			return 0;
	}

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

	protected static final double FUEL_CHARGE_RATE = 0.0175;

	/*
	 * protected Dollars fuelCharge() { return new Dollars(lastUsage() *
	 * FUEL_CHARGE_RATE); }
	 */

	protected Dollars fuelCharge() {
		return new Dollars(lastUsage() * 0.0175);
	}

	public DateRange lastPeriod() {
		Calendar calendar = previousReading().date();
		calendar.add(Calendar.DATE, 1);
		return new DateRange(calendar, lastReading().date());
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

	/*
	 * public void addReading(Reading newReading) throws
	 * IncorrectReadingException { if (isNotLatestReading(newReading)) throw new
	 * IncorrectReadingException("Reading is before previous reading");
	 * _readings.addElement(newReading); }
	 */

	private boolean isNotLatestReading(Reading arg) {
		return !_readings.isEmpty() && arg.date().before(lastReading().date());
	}

}
