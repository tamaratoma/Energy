package fowler.energybilling;

public class LifelineSite extends Site {
	private Reading[] _readings = new Reading[1000];
	private static final double TAX_RATE = 0.05;

	public LifelineSite() {
		super(null);
	};

	@Override
	public void addReading(Reading newReading) {
		Reading[] newArray = new Reading[_readings.length + 1];
		System.arraycopy(_readings, 0, newArray, 1, _readings.length);
		newArray[0] = newReading;
		_readings = newArray;
	}

	@Override
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

	@Override
	protected Dollars baseCharge() {
		return new Dollars(baseChargeTable().value(lastUsage()));
	}
	protected RateTable baseChargeTable() {
		double [] tableData = { 0.03, 100,
				0.05, 200,
				0.07};
				return new RateTable (tableData);
				
	}

	protected int usageUnder(int limit) {
		return Math.min(lastUsage(), limit);
	}

	protected int usageInRange(int start, int end) {
		if (lastUsage() > start)
			return Math.min(lastUsage(), end) - start;
		else
			return 0;
	}

	@Override
	protected Dollars fuelChargeTaxes() {
		throw new AbstractMethodError("undefined fuelChargeTaxes");
	}
}
