package exitcode;

import javafx.application.*;

import org.json.JSONArray;
import org.json.JSONObject;

import exitcode.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Internal dependancies: Main, Logger, LoadingScreen

public class Patreon {
    private static String creatorID = ""; // Remove before commiting publicly!

    public static void updatePatronList() {
        
        JSONArray pledges;
        Main.patrons.clear();
        if (Patreon.patreonIsAvailable()) {
            LoadingScreen.setLoadingBar(0.25);
            LoadingScreen.setTaskText("Fetching Patron List");
            pledges = getJSON("https://api.patreon.com/oauth2/api/" ,"campaigns/844621/pledges", creatorID).getJSONArray("data");

            for(int i = 0; i < pledges.length(); i++) {
                if (pledges.getJSONObject(i).getJSONObject("attributes").getInt("amount_cents") >= 500) {
                    Main.patrons.add(
                            getJSON("https://www.patreon.com/api/user/", 
                                pledges.getJSONObject(i).getJSONObject("relationships").getJSONObject("patron").getJSONObject("data").getInt("id"))
                                    .getJSONObject("data").getJSONObject("attributes").getString("full_name")
                    );
                }
            }
            LoadingScreen.setLoadingBar(1.0);
        } else {
            LoadingScreen.setLoadingBar(1.0);
            Main.patrons.add("Could not connect to Patreon");
            Logger.warn("No internet connection");
        }
    }

    private static boolean patreonIsAvailable() {
        LoadingScreen.setTaskText("Checking Internet Connection");
        try {
            final URL url = new URL("https://www.patreon.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private static JSONObject getJSON(String prefix, Integer suffix) { // Move to ExitParser later
		try
		{
			URL url = new URL(prefix.concat(suffix.toString()));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = stream.readLine()) != null) {
				result.append(line);
			}
			stream.close();
			return new JSONObject(result.toString());
		} 
		catch (MalformedURLException e) {}
		catch (IOException e) {}
		return null;
    }

    private static JSONObject getJSON(String prefix, String suffix, String _accessToken) { // Move to ExitParser later
		try {
			URL url = new URL(prefix.concat(suffix));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Bearer ".concat(_accessToken));
			BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = stream.readLine()) != null)
			{
				result.append(line);
			}
			stream.close();
			return new JSONObject(result.toString());
		} catch (Exception e) {
			/* Ignore these specific exceptions */
		}
		return null;
	}
}