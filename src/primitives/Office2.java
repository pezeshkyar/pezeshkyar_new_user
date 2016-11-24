package primitives;

public class Office2 {
	public int id;
	public String doctorUsername;
	public String doctorName;
	public String doctorLastName;
	public int cityId;
	public String city;
	public int provinceId;
	public String province;
	public int specId;
	public String spec;
	public int subspecId;
	public String subSpec;
	public String address; 
	public String tellNo; 
	public double latitude; 
	public double longitude; 
	public String biograophy;
	public int timeQuantum;
	public boolean isMyOffice;
	
	public Office2() {
		super();
	}
	
	public Office2(Office o) {
		this.address = o.address;
		this.biograophy = o.biograophy;
		this.city = o.city;
		this.cityId = o.cityId;
		this.doctorLastName = o.doctorLastName;
		this.doctorName = o.doctorName;
		this.doctorUsername = o.doctorUsername;
		this.id = o.id;
		this.latitude = o.latitude;
		this.longitude = o.longitude;
		this.province = o.province;
		this.provinceId = o.provinceId;
		this.spec = o.spec;
		this.specId = o.specId;
		this.subSpec = o.subSpec;
		this.subspecId = o.subspecId;
		this.tellNo = o.tellNo;
		this.timeQuantum = o.timeQuantum;
	}
}
