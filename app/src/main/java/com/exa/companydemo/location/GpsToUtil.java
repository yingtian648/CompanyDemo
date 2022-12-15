package com.exa.companydemo.location;

/**
 * GPS坐标转换成高德坐标
 * @author pjh
 */
public class GpsToUtil {

	private static double pi = 3.14159265358979324;
	private static double a = 6378245.0;
	private static double ee = 0.00669342162296594323;
	private final static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	public static double[] wgs2bd(double lat, double lon) {
		double[] wgs2gcj = wgs2gcj(lat, lon);
		double[] gcj2bd = gcj2bd(wgs2gcj[0], wgs2gcj[1]);
		return gcj2bd;
	}

	public static double[] gcj2bd(double lat, double lon) {
		double x = lon, y = lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		double bd_lon = z * Math.cos(theta) + 0.0065;
		double bd_lat = z * Math.sin(theta) + 0.006;
		return new double[] { bd_lat, bd_lon };
	}

	public static double[] bd2gcj(double lat, double lon) {
		double x = lon - 0.0065, y = lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		double gg_lon = z * Math.cos(theta);
		double gg_lat = z * Math.sin(theta);
		return new double[] { gg_lat, gg_lon };
	}
	
	/**
	 * WGS转2GCJ
	 * @param lat
	 * @param lon
	 * @return
	 * @Author : pjh. create at 2021年1月21日 上午10:42:42
	 */
	public static double[] wgs2gcj(double lat, double lon) {
		double dLat = transformLat(lon - 105.0, lat - 35.0);
		double dLon = transformLon(lon - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		double mgLat = lat + dLat;
		double mgLon = lon + dLon;
		double[] loc = { mgLat, mgLon };
		return loc;
	}
	    
	/**
	 * WGS先转为dd.dddd 再转为2gcj
	 * @param latStr  DDFF.FFFF
	 * @param lonStr  DDFF.FFFF
	 * @return
	 * @Author : pjh. create at 2021年1月21日 上午10:42:57
	 */
	public static double[] wgs2gcj_dddddd(String latStr, String lonStr) {
        String wmm = latStr.substring(latStr.indexOf(".") - 2);
        String jmm = lonStr.substring(lonStr.indexOf(".") - 2);
        String wdd = latStr.substring(0, latStr.indexOf(".") - 2);
        String jdd = lonStr.substring(0, lonStr.indexOf(".") - 2);
        double wmm_a = Double.parseDouble(wmm) / 60;
        double jmm_a = Double.parseDouble(jmm) / 60;
        // 转成dd.dddd格式
        double lat = Double.parseDouble(wdd) + wmm_a;
        double lon = Double.parseDouble(jdd) + jmm_a;
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        double[] loc = { mgLat, mgLon };
        return loc;
    }

	private static double transformLat(double lat, double lon) {
		double ret = -100.0 + 2.0 * lat + 3.0 * lon + 0.2 * lon * lon + 0.1 * lat * lon
				+ 0.2 * Math.sqrt(Math.abs(lat));
		ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lon * pi) + 40.0 * Math.sin(lon / 3.0 * pi)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(lon / 12.0 * pi) + 320 * Math.sin(lon * pi / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private static double transformLon(double lat, double lon) {
		double ret = 300.0 + lat + 2.0 * lon + 0.1 * lat * lat + 0.1 * lat * lon + 0.1 * Math.sqrt(Math.abs(lat));
		ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lat * pi) + 40.0 * Math.sin(lat / 3.0 * pi)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(lat / 12.0 * pi) + 300.0 * Math.sin(lat / 30.0 * pi)) * 2.0 / 3.0;
		return ret;
	}
	    
	/**
	 * 高德转百度
	 * @param gd_lat
	 * @param gd_lon
	 * @return
	 * @Author : pjh. create at 2021年1月21日 上午10:43:30
	 */
	private static double[] gaoDeToBaidu(double gd_lat, double gd_lon) {
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        double wgLat = z * Math.sin(theta) + 0.006;// 纬度
        double wgLon = z * Math.cos(theta) + 0.0065;// 经度
        double[] loc = { wgLat, wgLon };
        return loc;
    }
	
	
	
	public static void main(String[] args) {
	    long startTime = System.currentTimeMillis();
        // 北斗芯片获取的经纬度为WGS84地理坐标ddmm.mmmm格式
        String w = "3032.4406";
        String wmm = w.substring(w.indexOf(".") - 2);
        String j = "10404.2555";
        String jmm = j.substring(j.indexOf(".") - 2);
        String wdd = w.substring(0, w.indexOf(".") - 2);
        String jdd = j.substring(0, j.indexOf(".") - 2);
        double wmm_a = Double.parseDouble(wmm) / 60;
        double jmm_a = Double.parseDouble(jmm) / 60;
        // 转成dd.dddd格式
        double lat = Double.parseDouble(wdd) + wmm_a;
        double lon = Double.parseDouble(jdd) + jmm_a;
	    double[] a = wgs2gcj(lat, lon);
	    for (double d : a) {
	        System.out.println(d);
        }
	    double[] c = wgs2gcj_dddddd(w, j);
        for (double d : c) {
            System.out.println(d);
        } 
    }
}
