package fowler.energybilling;



/*Martin Fowler: The dollars class is a use of the Quantity pattern. It combines the
notion of an amount and a currency. I’m not going to go into too many
details here. Essentially you create dollars objects with a constructor
that has a number for the amount. The class supports some basic arithmetic
operations.
An important part of the dollars class is the fact that it rounds all numbers
to the nearest cent, a behavior which is often very important in
financial systems. As my friend Ron Jeffries told me: “Be kind to pennies,
and they will be kind to you”.
JK: added a round method that is invoked in the plus, minus, times methods
*/

public class Dollars {
	double amount;
	String currency;
	
	public Dollars(){
		this.amount = 0.0;
		this.currency = "USD";
		}

	
	public Dollars(double amount){
		this.amount = amount;
		this.currency = "USD";
		}

	public Dollars(Dollars dollar) {
		this.amount = dollar.amount;
		this.currency = dollar.currency;		
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Dollars plus(Dollars dollars) {
		return new Dollars(this.amount + dollars.amount).round(2);
	}

	public Dollars times(double taxRate) {
	    return new Dollars(this.amount * taxRate).round(2);
	}

	public Dollars minus(Dollars dollars) {
		return new Dollars(this.amount - dollars.amount).round(2);
	}
	
	
	
	public Dollars max(Dollars dollars) {
			 return this.amount >= dollars.amount ? this : dollars;
	}
		
		

	public Dollars min(Dollars dollars) {
		    return this.amount <= dollars.amount ? this : dollars;
	}

	public boolean isGreaterThan(Dollars dollars) {
		return this.amount > dollars.amount;
	}
	

	//c number of decimals to which we want to round
	public Dollars round(int c) {	 
		 int temp = (int)(this.amount * Math.pow(10, c));
		    double amount = temp / Math.pow(10, c);
		    
		    return new Dollars(amount);
		}
}
