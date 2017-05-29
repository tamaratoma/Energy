package fowler.energybilling;

public class BusinessSite extends Site {

	BusinessSite(Zone zone) {
		super(zone);
		// TODO Auto-generated constructor stub
	}

	private int lastReading;
	private Reading[] _readings = new Reading[1000];
	private static final double START_RATE = 0.09;
	static final double END_RATE = 0.05;
	static final int END_AMOUNT = 1000;

	public void addReading(Reading newReading) {
		_readings[++lastReading] = newReading;
	}

	private Dollars charge(int usage) {
		Dollars result = baseCharge();
		result = result.plus(fuelCharge());
		result = result.plus(taxes());
		return result;
	}

	protected Dollars baseCharge() {
		if (lastUsage() == 0)
			return new Dollars(0);
		double constant = START_RATE - belowLimitRate();
		double chargeBelowLimit = usageBelowLimit() * belowLimitRate();
		double chargeAboveLimit = usageAboveLimit() * END_RATE;
		return new Dollars(constant + chargeBelowLimit + chargeAboveLimit);
	}

	protected static double belowLimitRate() {
		return ((END_RATE * END_AMOUNT) - START_RATE) / (END_AMOUNT - 1);
	}

	protected int usageAboveLimit() {
		return Math.max(lastUsage() - END_AMOUNT, 0);
	}

	protected int usageBelowLimit() {
		return Math.min(END_AMOUNT, lastUsage());
	}

	protected Dollars taxes() {
		return new Dollars(taxTable().value((int) taxable().getAmount()));
	}

	protected Dollars taxable() {
		return baseCharge().plus(fuelCharge());
	}

	protected RateTable taxTable() {
		double[] tableData = { 0.07, 50, 0.06, 75, 0.05 };
		return new RateTable(tableData);
	}

	@Override
	protected Dollars fuelChargeTaxes() {
		// TODO Auto-generated method stub
		return null;
	}
}
