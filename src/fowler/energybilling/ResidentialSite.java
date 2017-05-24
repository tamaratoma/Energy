package fowler.energybilling;

import java.util.Calendar;
import java.util.Date;

public class ResidentialSite extends Site {

	private Reading[] _readings = new Reading[1000];
	private static final double TAX_RATE = 0.05;
	private Zone _zone;

	ResidentialSite(Zone zone) {
		super(zone);
	}

	// public void addReading(Reading newReading) {
	// // add reading to end of array
	// int i = 0;
	// while (_readings[i] != null)
	// i++;
	// _readings[i] = newReading;
	// }

	/*
	 * public Dollars charge() { // find last reading int i = 0; while
	 * (_readings[i] != null) i++;
	 * 
	 * int usage = _readings[i - 1].amount() - _readings[i - 2].amount();
	 * Calendar end = _readings[i - 1].date(); Calendar start = _readings[i -
	 * 2].date(); start.set(Calendar.DAY_OF_MONTH,
	 * (start.get(Calendar.DAY_OF_MONTH) + 1)); // set // to // beginning // of
	 * // period return charge(usage, start, end); }
	 */

	public Dollars charge() {
		Calendar end = lastReading().date();
		Calendar start = nextDay(previousReading().date());
		return charge(start, end);
	}

	protected Dollars charge(Calendar start, Calendar end) {
		Dollars result;
		result = baseCharge(start, end);
		result = result.plus(taxes(result));
		result = result.plus(fuelCharge());
		result = result.plus(fuelChargeTaxes());
		return result;
	}

	protected Dollars baseCharge(Calendar start, Calendar end) {
		return new Dollars((lastUsage() * _zone.getSummerRate() * summerFraction(start, end))
				+ (lastUsage() * _zone.getWinterRate() * (1 - summerFraction(start, end))));
	}

	protected Dollars fuelChargeTaxes() {
		return new Dollars(fuelCharge().times(TAX_RATE));
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
		;
		result += arg.get(Calendar.DAY_OF_MONTH);
		// check leap year
		if ((arg.get(Calendar.YEAR) % 4 == 0)
				&& ((arg.get(Calendar.YEAR) % 100 != 0) || ((arg.get(Calendar.YEAR) + 1900) % 400 == 0))) {
			result++;
		}
		;
		return result;
	}

	@Override
	int dayOfYear(Date arg) {
		// TODO Auto-generated method stub
		return 0;
	}
}
