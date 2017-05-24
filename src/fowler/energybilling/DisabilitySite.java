package fowler.energybilling;

import java.util.Calendar;
import java.util.Date;

public class DisabilitySite extends Site {
	private Reading[] _readings = new Reading[1000];
	private static final Dollars FUEL_TAX_CAP = new Dollars(0.10);
	private static final double TAX_RATE = 0.05;
	private Zone _zone; // zone must be initialized!!!
	private static final int CAP = 200;

	public DisabilitySite(Zone zone) {
		super(zone);

	}

	// public void addReading(Reading newReading) {
	// int i;
	// for (i = 0; _readings[i] != null; i++);
	// _readings[i] = newReading;
	// }

	// JK: usage and date calculation seem to be ok
	public Dollars charge() {
		int i;
		for (i = 0; _readings[i] != null; i++)
			;
		// JK i - 1 is the last reading, i-2 the one before
		int usage = _readings[i - 1].amount() - _readings[i - 2].amount();
		Calendar end = _readings[i - 1].date();
		Calendar start = _readings[i - 2].date();
		start.set(Calendar.DAY_OF_MONTH, (start.get(Calendar.DAY_OF_MONTH) + 1)); // set
																					// to
																					// beginning
																					// of
																					// period
		return charge(usage, start, end);
	}

	protected Dollars charge(int fullUsage, Calendar start, Calendar end) {
		Dollars result;
		result = baseCharge(start, end);
		result = result.plus(taxes(result));
		result = result.plus(fuelCharge());
		result = result.plus(fuelChargeTaxes());
		return result;
	}

	protected Dollars baseCharge(Calendar start, Calendar end) {
		int cappedUsage = Math.min(lastUsage(), CAP);
		Dollars result;
		result = new Dollars((cappedUsage * _zone.getSummerRate() * summerFraction(start, end))
				+ (cappedUsage * _zone.getWinterRate() * (1 - summerFraction(start, end))));
		result = result.plus(new Dollars(Math.max(lastUsage() - cappedUsage, 0) * 0.062));
		return result;
	}

	protected Dollars fuelChargeTaxes() {
		return new Dollars(fuelCharge().times(TAX_RATE).min(FUEL_TAX_CAP));
	}

	private double summerFraction(Calendar start, Calendar end) {
		double summerFraction;
		if (start.after(_zone.getSummerEnd()) || end.before(_zone.getSummerStart()))
			summerFraction = 0;
		else if (!start.before(_zone.getSummerStart()) && !start.after(_zone.getSummerEnd())
				&& !end.before(_zone.getSummerStart()) && !end.after(_zone.getSummerEnd()))
			summerFraction = 1;
		else { // part in summer part in winter
			double summerDays;
			if (start.before(_zone.getSummerStart()) || start.after(_zone.getSummerEnd())) {
				// end is in the summer
				summerDays = dayOfYear(end) - dayOfYear(_zone.getSummerStart()) + 1;
			} else {
				// start is in summer
				summerDays = dayOfYear(_zone.getSummerEnd()) - dayOfYear(start) + 1;
			}
			;
			summerFraction = summerDays / (dayOfYear(end) - dayOfYear(start) + 1);
		}
		;
		return summerFraction;
	}

	int dayOfYear(Calendar arg) {
		int result;
		switch (arg.get(Calendar.MONTH)) {
		case 0:
			result = 0;
			break;
		case 1:
			result = 31;
			break;
		case 2:
			result = 59;
			break;
		case 3:
			result = 90;
			break;
		case 4:
			result = 120;
			break;
		case 5:
			result = 151;
			break;
		case 6:
			result = 181;
			break;
		case 7:
			result = 212;
			break;
		case 8:
			result = 243;
			break;
		case 9:
			result = 273;
			break;
		case 10:
			result = 304;
			break;
		case 11:
			result = 334;
			break;
		default:
			throw new IllegalArgumentException();
		}
		result += arg.get(Calendar.DAY_OF_MONTH);
		// check leap year
		if ((arg.get(Calendar.YEAR) % 4 == 0)
				&& ((arg.get(Calendar.YEAR) % 100 != 0) || ((arg.get(Calendar.YEAR) + 1900) % 400 == 0))) {
			result++;
		}
		return result;
	}

	@Override
	int dayOfYear(Date arg) {
		// TODO Auto-generated method stub
		return 0;
	}

}