package fowler.energybilling;

import java.util.Calendar;
import java.util.Date;

public abstract class Site {

	protected Zone _zone;
	protected Reading[] readings = new Reading[1000];

	abstract int dayOfYear(Date arg);

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

	protected Dollars charge(Calendar start, Calendar end) {
		Dollars result;
		result = baseCharge(start, end);
		result = result.plus(taxes(result));
		result = result.plus(fuelCharge());
		result = result.plus(fuelChargeTaxes());
		return result;
	}

	abstract protected Dollars baseCharge(Calendar start, Calendar end);

	abstract protected Dollars fuelChargeTaxes();

	public Dollars charge() {
		Calendar end = lastReading().date();
		Calendar start = nextDay(previousReading().date());
		return charge(start, end);
	}

	protected Calendar nextDay(Calendar arg) {
		// foreign method - should be in Date
		Calendar result = Calendar.getInstance();
		result.setTime(arg.getTime());
		result.add(Calendar.DATE, 1);
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
}
