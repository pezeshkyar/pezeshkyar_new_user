package primitives;

public class Triple<A, B, C> {
		A first;
	    B second;
	    C third;
	    public Triple(A first, B second, C third) {
	        this.first = first;
	        this.second = second;
	        this.third = third;
	    }
		public A getFirst() {
			return first;
		}
		public void setFirst(A first) {
			this.first = first;
		}
		public B getSecond() {
			return second;
		}
		public void setSecond(B second) {
			this.second = second;
		}
		public C getThird() {
			return third;
		}
		public void setThird(C third) {
			this.third = third;
		} 
	

}
