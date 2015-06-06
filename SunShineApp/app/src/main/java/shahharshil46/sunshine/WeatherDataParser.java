package shahharshil46.sunshine;

/**
 * Created by HOME on 07-06-2015.
 */
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataParser {

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    static String LOG_TAG = WeatherDataParser.class.getName();
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        // TODO: add parsing code here
        JSONObject reader = new JSONObject(weatherJsonStr);
        JSONArray list = reader.optJSONArray("list");
        JSONObject temp = list.getJSONObject(dayIndex).getJSONObject("temp");
        double maxTemp = temp.getDouble("max");
        Log.d(LOG_TAG, "MaxTemp is " + maxTemp);
        return maxTemp;
    }

}
