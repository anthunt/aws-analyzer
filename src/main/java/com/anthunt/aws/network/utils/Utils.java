package com.anthunt.aws.network.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import software.amazon.awssdk.services.ec2.model.Tag;

public class Utils {

	public static String getNameFromTags(List<Tag> tags) {
		String name = "Unknown";
		for (Tag tag : tags) {
			if("Name".equals(tag.key())) {
				name = tag.value();
			}
		}
		return name;
	}
	
	public static String decodeB64URL(String encodedURL) throws UnsupportedEncodingException {
		return URLDecoder.decode(new String(Base64.getDecoder().decode(encodedURL), "utf8"), "utf8");
	}
	
	public static String encodeB64URL(String plainURL) throws UnsupportedEncodingException {
		return Base64.getEncoder().encodeToString(URLEncoder.encode(plainURL, "utf8").getBytes());
	}

    // Member variables (properties about the object)
    public final static String[] mColors = {
            "#39add1", // light blue
            "#3079ab", // dark blue
            "#c25975", // mauve
            "#e15258", // red
            "#f9845b", // orange
            "#838cc7", // lavender
            "#7d669e", // purple
            "#53bbb4", // aqua
            "#51b46d", // green
            "#e0ab18", // mustard
            "#637a91", // dark gray
            "#f092b0", // pink
            "#b7c0c7"  // light gray
    };

    // Method (abilities: things the object can do)
    public static Color getColor() {
        // Randomly select a fact
        Random randomGenerator = new Random(); // Construct a new Random number generator
        int randomNumber = randomGenerator.nextInt(mColors.length);
        return hexStringToColor(mColors[randomNumber]);
    }
    
    public static Color hexStringToColor(String hexARGB) {
    	int[] rgb = hexStringToARGB(hexARGB);
    	return new Color(hexARGB, rgb[1], rgb[2], rgb[3], rgb[0]);
    }
    
    /**
     *
     * Hex color string to int[] array converter
     *
     * @param hexARGB should be color hex string: #AARRGGBB or #RRGGBB
     * @return int[] array: [alpha, r, g, b]
     * @throws IllegalArgumentException
     */
    public static int[] hexStringToARGB(String hexARGB) throws IllegalArgumentException {
    	
        if (!hexARGB.startsWith("#") || !(hexARGB.length() == 7 || hexARGB.length() == 9)) {

            throw new IllegalArgumentException("Hex color string is incorrect!");
        }

        int[] intARGB = new int[4];

        if (hexARGB.length() == 9) {
            intARGB[0] = Integer.valueOf(hexARGB.substring(1, 3), 16); // alpha
            intARGB[1] = Integer.valueOf(hexARGB.substring(3, 5), 16); // red
            intARGB[2] = Integer.valueOf(hexARGB.substring(5, 7), 16); // green
            intARGB[3] = Integer.valueOf(hexARGB.substring(7), 16); // blue
        } else hexStringToARGB("#FF" + hexARGB.substring(1));

        return intARGB;
    }
    
}
