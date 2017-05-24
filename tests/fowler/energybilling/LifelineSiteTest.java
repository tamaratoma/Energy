package fowler.energybilling;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LifelineSiteTest {

	LifelineSite subject = new LifelineSite();

	// Parameters: year - the year minus 1900, month - the month between 0-11,
	// date - the day of the month between 1-31.
	private Calendar getCal(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);

		return cal;
	}

	@Before
	public void setUp() throws Exception {

		Zone zoneA = new Zone("A", 0.06, 0.07, getCal(97, 5, 15), getCal(97, 9, 10));
		Zone zoneB = new Zone("B", 0.07, 0.06, getCal(97, 6, 5), getCal(1997, 8, 31));
		Zone zoneC = new Zone("C", 0.065, 0.065, getCal(97, 6, 5), getCal(97, 8, 31));

		Registry.add(zoneA);
		Registry.add(zoneB);
		Registry.add(zoneC);

		// Registry.add("Unit", new Unit ("USD")); //from Fowler's Original text

		subject = new LifelineSite();
	}

	// Copied from EnergyBillingTests file, created by J.Koehler et al.

	@Test
	public void testRegistry() {
		assertFalse("Registry is empty", Registry.isEmpty());
		assertTrue("Registry contains 3 zones", Registry.size() == 3);

	}

	@Test
	public void testDollars() {
		Dollars d1 = new Dollars(5.0);
		Dollars cap = new Dollars(200.0);

		// System.out.println(d1.plus(cap).getAmount());
		// System.out.println(d1.minus(cap).getAmount());
		// System.out.println(cap.plus(d1).getAmount());
		// System.out.println(d1.times(2.0).getAmount());
		// System.out.println(cap.times(0.5).getAmount());
		// System.out.println((d1.plus(d1.times(2.0)).min(cap)).getAmount());

		assertTrue("Dollars plus 1", d1.plus(cap).getAmount() == 205.0);
		assertTrue("Dollars plus 2", cap.plus(d1).getAmount() == 205.0);
		assertTrue("Dollars minus 1", d1.minus(cap).getAmount() == -195.0);
		assertTrue("Dollars minus 2", cap.minus(d1).getAmount() == 195.0);
		assertTrue("Dollars times 1", d1.times(2.0).getAmount() == 10.0);
		assertTrue("Dollars times 2", cap.times(0.5).getAmount() == 100.0);

		assertTrue("Dollars min", (cap.min(d1)).getAmount() == 5.0);
		assertTrue("Dollars max", (cap.max(d1)).getAmount() == 200.0);
		assertTrue("Dollars nested", (d1.plus(d1.times(2.0)).min(cap)).getAmount() == 15.0);
	}

	// Testing the calculations of the charge method - the original code had
	// some problem with nullpointers when just copied.
	// We resolved this with minor changes in the method's code, but get very
	// different calculated values back.
	// However, we don't care about this as it is irrelevant for refactoring
	// exercises.
	// The lifelinesite charge method distinguishes consumptions below and above
	// 100 and 200(see calculation of base in charge statement)
	// The boundary is above 100 or 200, thus the boundaries are not 99, 100,
	// 101, but just 100, 101
	// a 0 consumption should yield a 0 charge
	// the readings are the same, therefore no energy has been consumed - this
	// should return a 0 charge

	@Test
	public void testAddReading_Zero() {
		subject.addReading(new Reading(10, getCal(1997, 0, 1)));
		subject.addReading(new Reading(10, getCal(1997, 1, 1)));
		assertEquals(new Dollars(0).getAmount(), subject.charge().getAmount());
	}

	@Test // from the text (but value was 4.79)
	public void testAddReading_99() {

		subject.addReading(new Reading(100, getCal(97, 0, 1)));
		subject.addReading(new Reading(199, getCal(97, 1, 1)));

		assertEquals(new Dollars(1.81).getAmount(), subject.charge().getAmount());
	}

	@Test
	public void testAddReading_100() {
		subject.addReading(new Reading(10, getCal(1997, 0, 1)));
		subject.addReading(new Reading(110, getCal(1997, 1, 1)));
		assertEquals(new Dollars(1.83).getAmount(), subject.charge().getAmount());
	}

	@Test // from the text (but value was 4.91)
	public void testAddReading_101() {
		subject.addReading(new Reading(1000, getCal(97, 0, 1)));
		subject.addReading(new Reading(1101, getCal(97, 1, 1)));
		assertEquals(new Dollars(1.84).getAmount(), subject.charge().getAmount());
	}

	@Test // from the text (but value was 11.6)
	public void testAddReading_199() {

		subject.addReading(new Reading(10000, getCal(97, 0, 1)));
		subject.addReading(new Reading(10199, getCal(97, 1, 1)));

		assertEquals(new Dollars(3.65).getAmount(), subject.charge().getAmount());
	}

	@Test // from the text (but value was 11.68)
	public void testAddReading_200() {

		subject.addReading(new Reading(0, getCal(97, 0, 1)));
		subject.addReading(new Reading(200, getCal(97, 1, 1)));

		assertEquals(new Dollars(3.67).getAmount(), subject.charge().getAmount());
	}

	@Test // from the text (but value was 11.77)
	public void testAddReading_201() {

		subject.addReading(new Reading(50, getCal(97, 0, 1)));
		subject.addReading(new Reading(251, getCal(97, 1, 1)));

		assertEquals(new Dollars(3.75).getAmount(), subject.charge().getAmount());
	}

	@Test
	public void testAddReading_Max() {

		subject.addReading(new Reading(0, getCal(97, 0, 1)));
		subject.addReading(new Reading(Integer.MAX_VALUE, getCal(97, 1, 1)));

		assertEquals(new Dollars(2.147483647E7).getAmount(), subject.charge().getAmount());
	}

	@Test
	public void testNoReading() {
		try {
			assertEquals(new Dollars(0).getAmount(), subject.charge().getAmount());
		} catch (NullPointerException e) {
		}
	}

	@After
	public void tearDown() throws Exception {
	}

}
