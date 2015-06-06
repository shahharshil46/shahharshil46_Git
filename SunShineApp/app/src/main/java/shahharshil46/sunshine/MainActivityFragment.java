package shahharshil46.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayAdapter<String> weekForecastArrayAdapter;
    ListView forecastListView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        String[] weekForecastData = {
                "Today - Sunny 88 / 63",
                "Tommorrow - Foggy 70 / 46",
                "Weds - Cloudy 72 / 63",
                "Thurs - Rainy 64 / 51",
                "Fri - Foggy 70 / 46",
                "Sat - Sunny 76 / 68",
                "Sun - Foggy 78 / 50"
        };

        ArrayList<String> fakeDataList = new ArrayList<String>();
        fakeDataList.addAll(Arrays.asList(weekForecastData));

        weekForecastArrayAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview, fakeDataList);

        forecastListView = (ListView) view.findViewById(R.id.litsview_forecast);
        forecastListView.setAdapter(weekForecastArrayAdapter);

        return view;
    }
}

