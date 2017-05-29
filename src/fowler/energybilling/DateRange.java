package fowler.energybilling;

import java.util.Calendar;
import java.util.Date;

public class DateRange {

	private Calendar _end;
	private Calendar _start;

	public DateRange(Calendar start, Calendar end) {
		_start = start;
		_end = end;
	}

	public Calendar end() {
		return _end;
	}

	public Calendar start() {
		return _start;
	}

	public DateRange intersection(DateRange summer) {
		// TODO Auto-generated method stub
		return null;
	}

	public double length() {
		// TODO Auto-generated method stub
		return 0;
	}

}
