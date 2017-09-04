package com.gem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONFileToMap {
	private static Map<String, String> jsonMapIn;
	private static Map<String, String> jsonMapOut;

	public static void main(String args[]) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			// read JSON from a file en.json origin
			jsonMapIn = mapper.readValue(
					new File(
							"C:\\Users\\Diego\\Desktop\\Languages\\en.json"),
					new TypeReference<Map<String, String>>() {
					});
			// File json out
			jsonMapOut = mapper.readValue(
					new File(
							"C:\\Users\\Diego\\Desktop\\Languages\\ko.json"),
					new TypeReference<Map<String, String>>() {
					});

			Map<String, String> jsonMapTemp = new HashMap<String, String>();
			// Map to jsonMapTemp: different keys from file en.json to file
			// a.json
			mapToJSON(jsonMapIn, jsonMapOut, jsonMapTemp);

			// file diffKey.json have keys and file a.json haven't
			try {
				mapper.writeValue(
						new File(
								"C:\\Users\\Diego\\Desktop\\Languages\\Temp\\diffKey.json"),
						jsonMapTemp);
			} catch (IOException ie) {
				ie.printStackTrace();
			}
			
			Map<String, String> jsonMapNeedTranslate = new HashMap<String, String>();

			// replace some values with old language to new
			for (Map.Entry<String, String> elementTemp : jsonMapTemp.entrySet()) {
				if (takeEnKey(jsonMapIn, elementTemp.getValue()) != null) {
					String tempValue = takeEnKey(jsonMapIn, elementTemp.getValue());
					if (takeAValue(jsonMapOut, tempValue) != null) {
						jsonMapTemp.replace(elementTemp.getKey(), elementTemp.getValue(),
								takeAValue(jsonMapOut, tempValue));
					}
				}
			}

			try {
				// file merge with both of translated and not translate.
				mapper.writeValue(
						new File(
								"C:\\Users\\Diego\\Desktop\\Languages\\Temp\\merge.json"),
						jsonMapTemp);
			} catch (IOException ie) {
				ie.printStackTrace();
			}

			// Remove keys not translate yet
			for (Iterator<Map.Entry<String, String>> it = jsonMapTemp.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = it.next();
				for (Iterator<Map.Entry<String, String>> it1 = jsonMapIn.entrySet().iterator(); it1.hasNext();) {
					Map.Entry<String, String> entry1 = it1.next();
					if (entry.getValue().equals(entry1.getValue())) {
						jsonMapNeedTranslate.put(entry.getKey(), entry.getValue());
						it.remove();
						break;
					}
				}
			}

			// Add file A.json to jsonMapTemp
			for (Map.Entry<String, String> elementA : jsonMapOut.entrySet()) {
				jsonMapTemp.put(elementA.getKey(), elementA.getValue());
			}

			try {
				// List keys translated
				mapper.writeValue(
						new File(
								"C:\\Users\\Diego\\Desktop\\Languages\\ko.json"),
						jsonMapTemp);
				// List keys need Translate
				mapper.writeValue(
						new File(
								"C:\\Users\\Diego\\Desktop\\Languages\\koNeedTranslate.json"),
						jsonMapNeedTranslate);
			} catch (IOException ie) {
				ie.printStackTrace();
			}

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	private static void mapToJSON(Map<String, String> jsonMapX, Map<String, String> jsonMapY,
			Map<String, String> jsonMapZ) {
		for (Map.Entry<String, String> elementX : jsonMapX.entrySet()) {
			int flags = 0;
			for (Map.Entry<String, String> elementY : jsonMapY.entrySet()) {
				if (elementX.getKey().equals(elementY.getKey())) {
					flags = 1;
				}
			}
			if (flags == 0) {
				// System.out.println(elementIn.getKey());
				jsonMapZ.put(elementX.getKey(), elementX.getValue());
			}
		}
	}

	// Take the key element of en.json(compare value in element of temp.json
	// with key en.json)
	private static String takeEnKey(Map<String, String> jsonMapIn, String tempValue) {
		for (Map.Entry<String, String> elementIn : jsonMapIn.entrySet()) {
			if (elementIn.getValue().equals(tempValue)) {
				return elementIn.getKey();
			}
		}
		return null;
	}

	// Take the value of a.json to replace value in temp.json
	private static String takeAValue(Map<String, String> jsonMapOut, String enKey) {
		for (Map.Entry<String, String> elementA : jsonMapOut.entrySet()) {
			if (elementA.getKey().equals(enKey)) {
				return elementA.getValue();
			}
		}
		return null;
	}
}