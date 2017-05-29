package fowler.energybilling;

public interface Cloneable {

	boolean before(MfDate arg2);

	boolean after(MfDate arg2);

	int getDate();
	
	void setDate(int i);

}
