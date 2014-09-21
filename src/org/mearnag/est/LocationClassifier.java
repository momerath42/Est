package org.mearnag.est;

class LocationClassifier {
	public static double classify(Double lat, Double lon, float acc, String ssid, int sstrength, int isPluggedIn, int batLevel)
	{
		Object[] s = new Object[7];

		s[0] = lon;
		s[1] = lat;
		s[2] = new Double(acc);
		s[3] = ssid;
		s[4] = new Double(sstrength);
		s[5] = new Double(isPluggedIn);
		s[6] = new Double(batLevel);

		try {
			return classify(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	public static double classify(Object[] i)
	throws Exception {

		double p = Double.NaN;
		p = LocationClassifier.N7986528(i);
		return p;
	}
	static double N7986528(Object []i) {
		double p = Double.NaN;
		if (i[1] == null) {
			p = 2;
		} else if (((Double) i[1]).doubleValue() <= 43.0379915) {
			p = 2;
		} else if (((Double) i[1]).doubleValue() > 43.0379915) {
			p = LocationClassifier.N617d059(i);
		} 
		return p;
	}
	static double N617d059(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= -87.90032625) {
			p = LocationClassifier.N56d58210(i);
		} else if (((Double) i[0]).doubleValue() > -87.90032625) {
			p = 0;
		} 
		return p;
	}
	static double N56d58210(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= -87.906680525) {
			p = LocationClassifier.Ndb585711(i);
		} else if (((Double) i[0]).doubleValue() > -87.906680525) {
			p = 1;
		} 
		return p;
	}
	static double Ndb585711(Object []i) {
		double p = Double.NaN;
		if (i[1] == null) {
			p = 1;
		} else if (((Double) i[1]).doubleValue() <= 43.03888041) {
			p = 1;
		} else if (((Double) i[1]).doubleValue() > 43.03888041) {
			p = 2;
		} 
		return p;
	}
}
