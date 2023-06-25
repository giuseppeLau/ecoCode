
class PreferComparisonToZeroCheck {

	/*
	private void forNonCompliant() {
		
		for (int i = 0; i < 5; i++) { // Noncompliant
			// Do Something;
		}
	}
	
	private void whileNonCompliant() {
		
		int i = 0;
		while(i < 5) { // Noncompliant
			// Do Something
			i++;
		}
	}
	*/
	
	private void forCompliant() {
	    
		for (int j = 5; j > 0; j--) { // Compliant
			// Do Something
		}
	}
	
	private void whileCompliant() {
	    
		int i = 5;
		while(i > 0) { // Compliant
			// Do Something
			i--;
		}
	}
}