package fowler.energybilling;

import java.util.Calendar;
import java.util.Date;

public class Site {

	protected Zone _zone;
	protected Reading[] readings = new Reading[1000];

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

	private Dollars charge(int fullUsage, Calendar start, Calendar end) {
		return new Dollars();
	}

	public Dollars charge() {
		Calendar end = lastReading().date();
		Calendar start = nextDay(previousReading().date());
		return charge(lastUsage(), start, end);
	}

	private Calendar nextDay(Calendar arg) {
		// foreign method - should be in Date
		Calendar result = Calendar.getInstance();
		result.setTime(arg.getTime());
		result.add(Calendar.DATE, 1);
		return result;
	}

	private int lastUsage() {
		return lastReading().amount() - previousReading().amount();
	};

	private Reading previousReading() {
		return readings[firstUnusedReadingsIndex() - 2];
	}

	private Reading lastReading() {
		return readings[firstUnusedReadingsIndex() - 1];
	};
}
