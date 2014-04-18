import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.catalina.tribes.tipis.AbstractReplicatedMap.MapMessage;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

public class GetQuery {

	/**
	 * Read the FourSquare Categories from the given json file.
	 * 
	 * @param jsonCategoryPath
	 *            , the path of the given json file.
	 * @return, a hash map that map each sub category to the main category.
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static Map<String, String> ReadAllTheCategories(
			String jsonCategoryPath) throws JsonParseException, IOException {
		Map<String, String> categoryMap = new HashMap<String, String>();

		JsonFactory f = new MappingJsonFactory();

		JsonParser jp = f.createParser(new File(jsonCategoryPath));
		jp.nextToken();
		JsonNode jNode = null;
		try {

			jNode = jp.readValueAsTree();

		} catch (JsonProcessingException e) {

			e.printStackTrace();

			return null;

		} catch (IOException e) {

			e.printStackTrace();

			return null;
		}

		jNode = jNode.path("response");

		for (JsonNode mainjNode : jNode.path("categories")) {
			String mainCategory = mainjNode.path("name").asText();

			// sub category
			for (JsonNode subjNode : mainjNode.path("categories")) {
				String subCategory = subjNode.path("name").asText();

				if (categoryMap.containsKey(subCategory) == false) {
					categoryMap.put(subCategory, mainCategory);
				}

				// sub sub category
				for (JsonNode subsubjNode : subjNode.path("categories")) {
					String subsubCategory = subsubjNode.path("name").asText();

					if (categoryMap.containsKey(subsubCategory) == false) {
						categoryMap.put(subsubCategory, mainCategory);
					}
				}
			}
		}

		return categoryMap;
	}

	/**
	 * Analyze the number of POIs in each category.
	 * 
	 * @param poiJsonFile
	 *            , the json file that contains all the POIs within a boundary.
	 * @param categoryMap
	 *            , a hash map that maps the sub categories to the main
	 *            categories.
	 * @return a hash map that has the number of POIs in each main category.
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static Map<String, Integer> ParsePOIJson(File poiJsonFile,
			Map<String, String> categoryMap) throws JsonParseException,
			IOException {

		Map<String, Integer> poiMap = new HashMap<String, Integer>();

		JsonFactory f = new MappingJsonFactory();

		JsonParser jp = f.createParser(poiJsonFile);
		jp.nextToken();
		JsonNode jNode = null;
		try {

			jNode = jp.readValueAsTree();

		} catch (JsonProcessingException e) {

			e.printStackTrace();

			return null;

		} catch (IOException e) {

			e.printStackTrace();

			return null;
		}

		jNode = jNode.path("response");

		for (JsonNode venuejNode : jNode.path("venues")) {

			for (JsonNode cjNode : venuejNode.path("categories")) {

				String subCategoryName = cjNode.path("name").asText();

				String categoryName = categoryMap.get(subCategoryName);

				if (poiMap.containsKey(categoryName) == false) {
					poiMap.put(categoryName, 1);
				} else {
					int value = poiMap.get(categoryName);
					value++;
					poiMap.put(categoryName, value);
				}
			}

		}

		return poiMap;
	}


	/**
	 * Given the url of FourSquare API, write the returned POI json to a
	 * specific file.
	 * 
	 * @param urlStr
	 *            , the url of the FourSquare API
	 * @param poiJsonFilePath
	 *            , the local directory where we want to store the returned json
	 *            data.
	 * @throws IOException
	 */
	public static void WritePOIJsonFile(String urlStr, String poiJsonFilePath)
			throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new URL(urlStr).openStream();
			output = new FileOutputStream(poiJsonFilePath);
			byte[] buffer = new byte[1024];
			for (int length = 0; (length = input.read(buffer)) > 0;) {
				output.write(buffer, 0, length);
			}
			// Here you could append further stuff to `output` if necessary.
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException logOrIgnore) {
				}
			if (input != null)
				try {
					input.close();
				} catch (IOException logOrIgnore) {
				}
		}
	}
	
	
	/**
	 * Given the boundary of a minimal bounding box,return a hash map that has
	 * the number of POIs in each main category.
	 * 
	 * @param swLatitude
	 * @param swLongitude
	 * @param neLatitude
	 * @param neLongitude
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static Map<String, Integer> POIRectangleQuery(double swLatitude,
			double swLongitude, double neLatitude, double neLongitude)
			throws JsonParseException, IOException {

		String str = "https://api.foursquare.com/v2/venues/search?intent=browse&sw="
				+ swLatitude
				+ ","
				+ swLongitude
				+ "&ne="
				+ neLatitude
				+ ","
				+ neLongitude
				+ "&oauth_token=W4H2E1ZGPYEAOBFHLC14JTX40VHKAUV22GFGU1KJ315GOSU1&v=20140411";

		// 1 Get the POI from FourSquare API.
		String poiJsonFilePath = "poi.json";
		WritePOIJsonFile(str, poiJsonFilePath);

		// 2 Read the local categories json file.
		Map<String, String> categoryMap = ReadAllTheCategories("categories.json");

		// 3 Parse the returned POI json file and calculate the POI number in
		// each main category
		Map<String, Integer> categoryNum = ParsePOIJson(new File(
				poiJsonFilePath), categoryMap);

		// Display the result
		for (Entry<String, Integer> entry : categoryNum.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}

		return categoryNum;
	}

	public static void main(String[] args) throws IOException {
		POIRectangleQuery(40.440563,-79.940113,40.453333,-79.914406);
	}
}