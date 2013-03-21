package de.jg3d.util;

public class TpsCounter {
	  long[] times;
	  long lastTick;
	  int ptr;
          long calls;
          
	  public TpsCounter(int bufferSize) {
	    times = new long[bufferSize];
	    ptr = 0;
	    calls = 0;
            lastTick = System.currentTimeMillis();
	  }
	  
	  public long tick() {
	    calls++;
            ptr = (ptr+1) % times.length;
	    times[ptr] = System.currentTimeMillis()-lastTick; 
	    lastTick = System.currentTimeMillis();
            return calls;
          }
	  
	  public float get() {
	    int timeSum = 0;
	    for (int i=0; i<times.length; i++)
	      timeSum+=times[i];
	    return (1.0f/((float)timeSum/(float)times.length))*1000.0f;
	  }
	  
          @Override
	  public String toString() {
		return Double.toString(Math.ceil(get() * 100) / 100);  
	  }
}
