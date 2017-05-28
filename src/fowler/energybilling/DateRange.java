package fowler.energybilling;

import java.util.Calendar;
import java.util.Date;

public class DateRange {
	
	private Date _end;
	private Date _start;
	
	public DateRange(Date start, Date end){
		_start = start;
		_end = end;
	}
	
	public Date end() {
		return _end;
		}
		public Date start() {
		return _start;
		}
		

}
