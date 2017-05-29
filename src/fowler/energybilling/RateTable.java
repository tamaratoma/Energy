package fowler.energybilling;

public class RateTable {

	private double[] _rates;
	private int[] _limits;

	public RateTable(double[] arg) {
		int arrayLengths = arg.length / 2 + 1;
		_rates = new double[arrayLengths];
		_limits = new int[arrayLengths];
		int argIndex = 0;
		for (int i = 0; i < (arrayLengths - 1); i++) {
			_rates[i] = arg[argIndex++];
			_limits[i] = (int) arg[argIndex++];
		}
		;
		_rates[arrayLengths - 1] = arg[arg.length - 1];
		_limits[arrayLengths - 1] = Integer.MAX_VALUE;
	}

	private double[] _table;

	private int usageInRange(int amount, int start, int end) {
		if (amount > start)
			return Math.min(amount, end) - start;
		else
			return 0;
	}

	public Dollars value(int amount) {
		double result = 0;
		result = usageInRange(amount, 0, _limits[0]) * _rates[0];
		for (int i = 1; i < _rates.length; i++)
			result += usageInRange(amount, _limits[i - 1], _limits[i]) * _rates[i];
		return new Dollars(result);
	}

}
