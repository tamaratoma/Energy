package fowler.energybilling;

import static org.junit.Assert.*;


import java.util.Calendar;

import org.junit.*;

import fowler.energybilling.BusinessSite;
import fowler.energybilling.DisabilitySite;
import fowler.energybilling.Dollars;
import fowler.energybilling.LifelineSite;
import fowler.energybilling.Reading;
import fowler.energybilling.Registry;
import fowler.energybilling.Zone;

/* Jana Koehler, Dec 2010, contributions from Florian Graf, June 2011
 * revised January 2015
 * Several things one can do with this example code
 * - rebuild the class hierarchy and improve the charge methods as in Fowler's original sample text
 * - get rid of the deprecated Date class, probably replace it by Calendar
 * - fix the summer days calculation in disabilitysite.charge as it seems to give negative values sometimes
 * - improve on the tests in general, for example set up a suite and have separate test classes, one for each site
 * - look at the Dollars class as our calculated values are different from the one in the text and 
 *   the test assertEquals (new Dollars(11.6), subject.charge()); does not work for our code
 */


//public Date(int year, int month, int date)
//Deprecated. As of JDK version 1.1, replaced by Calendar.set(year + 1900, month, date) or 
//GregorianCalendar(year + 1900, month, date). Allocates a Date object and initializes it so that it represents midnight, 
//local time, at the beginning of the day specified by the year, month, and date arguments.




public class EnergyBillingTests {

	//Parameters: year - the year minus 1900, month - the month between 0-11, date - the day of the month between 1-31.
	private Calendar getCal(int year, int month, int day){
		Calendar cal =  Calendar.getInstance();
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
		// Registry.add("Unit", new Unit ("USD")); from Fowler's Original text

	}

	@Test
	public void dummyTest() {
		String s = "hi";
		assertEquals("it works", "hi", s);
	}

	@Test
	public void testRegistry() {
		assertFalse("Registry is empty", Registry.isEmpty());
		assertTrue("Registry contains 3 zones", Registry.size() == 3);

	}

	@Test
	public void testDollars() {
		Dollars d1 = new Dollars(5.0);
		Dollars cap = new Dollars(200.0);
		
//		System.out.println(d1.plus(cap).getAmount());
//		System.out.println(d1.minus(cap).getAmount());
//		System.out.println(cap.plus(d1).getAmount());
//		System.out.println(d1.times(2.0).getAmount());
//		System.out.println(cap.times(0.5).getAmount());
//		System.out.println((d1.plus(d1.times(2.0)).min(cap)).getAmount());
		
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
	
	
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////	

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
	
	@Test  //from the text
	public void testLifeLineSite0() {
		LifelineSite subject = new LifelineSite();
		subject.addReading(new Reading(10, getCal(97, 0, 1)));
		subject.addReading(new Reading(10, getCal(97, 1, 1)));
		//System.out.println("Life=" + subject.charge().getAmount());
		assertTrue(subject.charge().getAmount() == 0.0);
	}

	@Test //from the text (but value was 4.84)
	public void testLifeLineSite100() {
		LifelineSite subject = new LifelineSite();
		subject.addReading(new Reading(10, getCal(97, 0, 1)));
		subject.addReading(new Reading(110, getCal(97, 1, 2)));
		// System.out.println("100Charge is: "+subject.charge().getAmount());
		assertEquals(new Dollars(1.83).getAmount(), subject.charge().getAmount());
	}
	
	@Test //from the text (but value was 4.79)
	public void testLifeLineSite99() {
		LifelineSite subject = new LifelineSite();
		subject.addReading(new Reading (100, getCal(97,0,1)));
		subject.addReading(new Reading (199, getCal(97,1,1)));
		assertEquals (new Dollars(1.81).getAmount(), subject.charge().getAmount());
		}

	@Test //from the text (but value was 4.91)
	public void testLifeLineSite101() {
		LifelineSite subject = new LifelineSite();
		//http://docs.oracle.com/javase/6/docs/api/java/util/Date.html#parse%28java.lang.String%29
		//System.out.println(new Date("1 Jan 1997")); date string recognition works	
		subject.addReading(new Reading(1000, getCal(97,0,1)));
		subject.addReading(new Reading(1101, getCal(97,1,1)));
		//System.out.println("101Charge is: "+subject.charge().getAmount());
		assertEquals(new Dollars(1.84).getAmount(), subject.charge()
				.getAmount());
	}
	
	@Test //from the text (but value was 11.6)
	public void test199() {
		LifelineSite subject = new LifelineSite();
		subject.addReading(new Reading (10000, getCal(97,0,1)));
		subject.addReading(new Reading (10199, getCal(97,1,1)));
		assertEquals (new Dollars(3.65).getAmount(), subject.charge().getAmount());
		}

	@Test //from the text (but value was 11.68)
	public void testtestLifeLineSite200() {
		LifelineSite subject = new LifelineSite();
		subject.addReading(new Reading(0, getCal(97,0,1)));
		subject.addReading(new Reading(200, getCal(97,1,1)));
		// System.out.println("200Charge is: "+subject.charge().getAmount());
		assertEquals(new Dollars(3.67).getAmount(), subject.charge()
				.getAmount());
	}
	

	@Test //from the text (but value was 11.77)
	public void testLifeLineSite201() {
		LifelineSite subject = new LifelineSite();
		subject.addReading(new Reading(50, getCal(97,0,1)));
		subject.addReading(new Reading(251, getCal(97,1,1)));
		// System.out.println("201Charge is: "+subject.charge().getAmount());
		assertEquals(new Dollars(3.75).getAmount(), subject.charge().getAmount());
	}

	@Test
	public void testLifeLineSiteMax() {
		LifelineSite subject = new LifelineSite();
		subject.addReading(new Reading(0, getCal(97,0,1)));
		subject.addReading(new Reading(Integer.MAX_VALUE,
				getCal(97,1,1)));
		// System.out.println("MaxCharge is: "+subject.charge().getAmount());
		assertEquals(new Dollars(2.147483647E7).getAmount(), subject.charge()
				.getAmount());
	}

	// trying to charge in case of no readings leads to a null pointer exception
	// this can be intended or a bug, the first of the two tests can detect the
	// bug, the second takes the exception as ok

	@Ignore
	// only use when the null pointer has been fixed as an error
	public void testtestNoReadings() {
		LifelineSite subject = new LifelineSite();
		assertEquals(new Dollars(0), subject.charge());
	}

	@Test
	public void testNoReadingsCatchException() {
		LifelineSite subject = new LifelineSite();
		try {
			subject.charge();
			assert (false);
		} catch (NullPointerException e) {
		}
	}

//	@Test
//	public void testDisabilitySite0() {
//		DisabilitySite subject = new DisabilitySite(Registry.get("A"));
//		subject.addReading(new Reading(10, getCal(97, 0, 1)));
//		subject.addReading(new Reading(10, getCal(97, 1, 1)));
//		assertTrue(subject.charge().getAmount() == 0.0);
//	}

	// typical boundaries for the disability site: used more than 200, which is
	// removed by the cap
	// otherwise, some readings within the summer and winter period dates could
	// be added to see if this makes a difference
	// the original code by Fowler only charges 0 in all cases

//	@Test
//	public void testDisabilitySite199Winter() {
//		DisabilitySite subject = new DisabilitySite(Registry.get("A"));
//		subject.addReading(new Reading(100, getCal(97, 0, 1)));
//		subject.addReading(new Reading(299, getCal(97, 1, 1)));
//		// System.out.println("199WinterCharge is: "+subject.charge().getAmount());
//		assertEquals(new Dollars(18.2).getAmount(), subject.charge()
//				.getAmount());
//	}

//	@Test
//	public void testDisabilitySite199Summer() {
//		DisabilitySite subject = new DisabilitySite(Registry.get("B"));
//		subject.addReading(new Reading(300, getCal(97, 6, 5)));
//		subject.addReading(new Reading(499, getCal(97, 8, 31)));
//		// System.out.println("199SummerCharge is: "+subject.charge().getAmount());
//		assertEquals(new Dollars(18.2).getAmount(), subject.charge()
//				.getAmount());
//	}

//	@Test
//	// the summerdays calculation yield -134 for this case - this clearly an
//	// error, but we ignore it for now
//	public void testDisabilitySite199WholeYear() {
//		DisabilitySite subject = new DisabilitySite(Registry.get("A"));
//		subject.addReading(new Reading(20000, getCal(97, 0, 1)));
//		subject.addReading(new Reading(20199, getCal(97, 11, 31)));
//		//System.out.println("199WholeYearCharge is: "+subject.charge().getAmount());
//		assertEquals(new Dollars(17.05).getAmount(), subject.charge().getAmount());
//	}

//	@Test
//	public void testDisabilitySite4000WholeYear() {
//		DisabilitySite subject = new DisabilitySite(Registry.get("B"));
//		subject.addReading(new Reading(1000, getCal(97, 0, 1)));
//		subject.addReading(new Reading(5000, getCal(97, 11, 31)));
//		// System.out.println("4000WholeYearCharge is: "+subject.charge().getAmount());
//		assertEquals(new Dollars(331.1).getAmount(), subject.charge()
//				.getAmount());
//	}
	@Test
	public void testBusinessSite4000WholeYear() {
		BusinessSite subject = new BusinessSite();
		subject.addReading(new Reading(1000, getCal(97, 0, 1)));
		subject.addReading(new Reading(5000, getCal(97, 11, 31)));
		// System.out.println("4000WholeYearCharge is: "+subject.charge().getAmount());
		assertEquals(new Dollars(284.75).getAmount(), subject.charge()
				.getAmount());
	}

	@Test
	public void testBusinessSite0() {
		BusinessSite subject = new BusinessSite();
		subject.addReading(new Reading(10, getCal(97, 0, 1)));
		subject.addReading(new Reading(10, getCal(97, 1, 1)));
		assertTrue(subject.charge().getAmount() == 0.0);
	}

	

	@Test
	public void testBusinessSite199Winter() {
		BusinessSite subject = new BusinessSite();
		subject.addReading(new Reading(100, getCal(97, 1, 1)));
		subject.addReading(new Reading(299, getCal(97, 2, 1)));
		// System.out.println("199WinterCharge is: "+subject.charge().getAmount());
		assertEquals(new Dollars(14.4).getAmount(), subject.charge()
				.getAmount());
	}

	@Test
	public void testBusinessSite199Summer() {
		BusinessSite subject = new BusinessSite();
		subject.addReading(new Reading(300, getCal(97, 6, 15)));
		subject.addReading(new Reading(499, getCal(97, 8, 31)));
		// System.out.println("199SummerCharge is: "+subject.charge().getAmount());
		assertEquals(new Dollars(14.4).getAmount(), subject.charge()
				.getAmount());
	}

//	@Test
//	public void testResidentialSite199Summer() {
//		ResidentialSite subject = new ResidentialSite(Registry.get("A"));
//		subject.addReading(new Reading(300, getCal(97, 5, 15)));
//		subject.addReading(new Reading(499, getCal(97, 7, 31)));
//		// System.out.println("199SummerCharge is: "+subject.charge().getAmount());
//		assertEquals(new Dollars(16.18).getAmount(), subject.charge()
//				.getAmount());
//	}

//	@Test
//	public void testResidentialSite4000WholeYear() {
//		ResidentialSite subject = new ResidentialSite(Registry.get("B"));
//		subject.addReading(new Reading(1000, getCal(97, 0, 1)));
//		subject.addReading(new Reading(5000, getCal(97, 11, 31)));
//		// System.out.println("4000WholeYearCharge is: "+subject.charge().getAmount());
//		assertEquals(new Dollars(346.26).getAmount(), subject.charge()
//				.getAmount());
//	}

	@After
	public void tearDown() throws Exception {
	}

}
