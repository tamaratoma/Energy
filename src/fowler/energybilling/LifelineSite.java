package fowler.energybilling;

public class LifelineSite extends Site {
	private Reading[] _readings = new Reading[1000];
	private static final double TAX_RATE = 0.05;

	public LifelineSite() {
		super(null);
	};

	public void addReading(Reading newReading) {
		Reading[] newArray = new Reading[_readings.length + 1];
		System.arraycopy(_readings, 0, newArray, 1, _readings.length);
		newArray[0] = newReading;
		_readings = newArray;
	}

	public Dollars charge() {
		int usage = lastReading().amount() - previousReading().amount();
		return charge(usage);
	}

	private Dollars charge(int usage) {
		Dollars result = baseCharge();
		result = result.plus(taxes(result));
		result = result.plus(fuelCharge());
		return result.plus(fuelChargeTaxes());
	}

	protected Dollars taxes(Dollars base) {
		return new Dollars(baseCharge().minus(new Dollars(8)).max(new Dollars(0)).times(TAX_RATE));
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

	protected Dollars fuelChargeTaxes() {
		throw new AbstractMethodError("undefined fuelChargeTaxes");
	}
}
