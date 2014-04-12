import java.awt.geom.Point2D;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GetQuery{
	
	
	ArrayList<Integer> ParsePOIJson(String jsonFile){
		
		ArrayList<Integer> poiVec = new ArrayList<Integer>();
		
		
		
		return poiVec;
		
	}
	
	
	public ArrayList<Integer> POIRectangleQuery(Point2D sw, Point2D ne)
	{
		
		//
		
		String path="document.json";
		ArrayList<Integer> poiVec = ParsePOIJson(path);
		
		return poiVec;
	}

	public static void WriteQuery(String s) throws IOException {
		InputStream input = null;
		 OutputStream output = null;
		 try {
		     input = new URL(s).openStream();
		     output = new FileOutputStream("document.json");
		     byte[] buffer = new byte[1024];
		     for (int length = 0; (length = input.read(buffer)) > 0;) {
		         output.write(buffer, 0, length);
		     }
		     // Here you could append further stuff to `output` if necessary.
		 } finally {
		     if (output != null) try { output.close(); } catch (IOException logOrIgnore) {}
		     if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
		 }
		 
	}

 public static void main(String[] args) throws IOException {
	 
	 String str="https://api.foursquare.com/v2/venues/search?intent=browse&sw=44.3,-112.1&ne=44.5,-112.0&oauth_token=W4H2E1ZGPYEAOBFHLC14JTX40VHKAUV22GFGU1KJ315GOSU1&v=20140411";
		
	 
	 WriteQuery(str);
//	 InputStream input = null;
//	 OutputStream output = null;
//	 try {
//	     input = new URL("https://api.foursquare.com/v2/venues/search?ll=37.7620,-122.435&oauth_token=W4H2E1ZGPYEAOBFHLC14JTX40VHKAUV22GFGU1KJ315GOSU1&v=20140411").openStream();
//	     output = new FileOutputStream("document.json");
//	     byte[] buffer = new byte[1024];
//	     for (int length = 0; (length = input.read(buffer)) > 0;) {
//	         output.write(buffer, 0, length);
//	     }
//	     // Here you could append further stuff to `output` if necessary.
//	 } finally {
//	     if (output != null) try { output.close(); } catch (IOException logOrIgnore) {}
//	     if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
//	 }

 }

}