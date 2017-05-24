package fowler.energybilling;


/*
*  Super class for the different site classes.
*/

public class Site {


    protected Zone zone;
    protected Reading[] readings = new Reading[1000];
    
    Site(Zone zone){
          this.zone = zone;
    }
    
    /*
    * New addReading method, pulled up from subclasses.
    * Adds the new reading into the first free spot in the array.
    *  
     */
    public void addReading(Reading newReading){
          
          readings[firstUnusedReadingsIndex()] = newReading;

    }
    
    /*
    *  Finding the first non-null index of the array.
    */
    private int firstUnusedReadingsIndex(){
          
          int i = 0;
          while(readings[i] != null) i++;
          
          return i;
          
    }
    
}
