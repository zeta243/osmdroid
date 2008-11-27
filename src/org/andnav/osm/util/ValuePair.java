package org.andnav.osm.util;

public class ValuePair implements Comparable<ValuePair>{
	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	final int a,b;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public ValuePair(final int[] reuse) {
		a = reuse[0];
		b = reuse[1];
	}
	
	public ValuePair(final int pA, final int pB) {
		a = pA;
		b = pB;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public int[] toArray() {
		return new int[]{a,b};
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public boolean equals(Object o) {
		return o instanceof ValuePair 
			&& a == ((ValuePair)o).a
			&& b == ((ValuePair)o).b;
	}

	@Override
	public int compareTo(ValuePair another) {
		if(a != another.a)
			return a - another.a;
		else if(b != another.b)
			return b - another.b;
		else
			return 0;
	}

	// ===========================================================
	// Methods
	// ===========================================================
}
