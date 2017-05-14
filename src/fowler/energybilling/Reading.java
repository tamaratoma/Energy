package fowler.energybilling;

import java.util.Calendar;

public class Reading {
	private Calendar _date;
	private int  _amount;

	public Reading(int amount, Calendar date) {
		_amount = amount;
		_date = date;
	}


	public Calendar date() {
		return _date;
	}

		
	public int amount() {
		return _amount;
	}

}
