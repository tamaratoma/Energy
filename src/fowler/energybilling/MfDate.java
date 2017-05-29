package fowler.energybilling;

import java.util.Calendar;
import java.util.Date;

public class MfDate implements Cloneable {

	/*
	 * public MfDate(Date arg) { super(arg.getTime()); }
	 * 
	 * public MfDate(String dateString) { super(dateString); };
	 */

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// should not happen
			throw new InternalError(e.toString());
		}
	}

	public MfDate copy() {
		return (MfDate) clone();
	}

	public MfDate nextDay() {
		MfDate result = copy();
		result.setDate(result.getDate() + 1);
		return result;
	}

	public static MfDate earliest(MfDate arg1, MfDate arg2) {
		return (arg1.before(arg2)) ? arg1 : arg2;
	}

	public static MfDate latest(MfDate arg1, MfDate arg2) {
		return (arg1.after(arg2)) ? arg1 : arg2;
	}

	int dayOfYear() {
		int result = daysToStartOfMonth() + getDate();
		if (isLeapYear() & this.after(new MfDate())) {
			result++;
		}
		return result;
	}

	boolean isLeapYear() {
		return (getYear() % 4 == 0) && ((getYear() % 100 != 0) || ((getYear() + 1900) % 400 == 0));
	}

	private int daysToStartOfMonth() {
		int[] monthNumbers = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
		return monthNumbers[getMonth()];
	}

	private int getMonth() {
		// TODO Auto-generated method stub
		return 0;
	}

	private void requireSameYear(MfDate arg) {
		if (getYear() != arg.getYear())
			throw new IllegalArgumentException("Arguments must be in same year");
	}

	public int minus(MfDate arg) {
		return (getYear() == arg.getYear()) ? dayOfYear() - arg.dayOfYear() : // shortcut
				daysSince1901() - arg.daysSince1901();
	}

	public int daysSince1901() {
		if (getYear() < 1)
			throw new IllegalArgumentException();
		int result;
		int yearIndex = getYear() - 1;
		result = yearIndex * 365;
		result += yearIndex / 4; // ordinary leap years
		result += (yearIndex + 300) / 400; // leap centuries
		result -= yearIndex / 100; // non-leap centuries
		result += dayOfYear() - 1;
		return result;
	}

	private int getYear() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean before(MfDate arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean after(MfDate arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getDate() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setDate(int i) {
		// TODO Auto-generated method stub

	}

}
