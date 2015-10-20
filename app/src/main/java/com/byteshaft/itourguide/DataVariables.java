package com.byteshaft.itourguide;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class DataVariables {
    
    static String[] one = new String[] {"Minar-e-Pakistan",
            "A public monument located in Iqbal Park which is one of the largest urban parks in Lahore.",
            "31.592513", "74.309799"};
    static String[] two = new String[] {"Baadshahi Mosque",
            "Commissioned by the sixth Mughal Emperor Aurangzeb. Constructed between 1671 and 1673.",
            "31.588563", "74.311620"};
    static String[] three = new String[] {"Lahore Fort",
            "Locally referred to as Shahi Qila, is a citadel in the city of Lahore.",
            "31.587890", "74.315288"};
    static String[] four = new String[] {"Lahore Zoo", "Established in 1872, it is one of the oldest zoos in the world.",
            "31.556294", "74.326646"};

    static String[] five = new String[] {"Daata Darbaar", "One of the oldest Muslim shrines in South Asia.",
            "31.578971", "74.305495"};

    static String[] six = new String[] {"Wazir Khan Mosque", "It is famous for its extensive faience tile work. It has been described as 'a mole on the cheek of Lahore'.",
            "31.583239", "74.324118"};

    static String[] seven = new String[] {"Shalimaar Garden", "A Mughal garden complex located in Lahore.",
            "31.587034", "74.382186"};

    static String[] eight = new String[] {"KFC", "KFC IS HERE", "30.195454", "71.442381"};

    static String[][] array = {one, two, three, four, five, six, seven, eight};

    public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();

    static {

        for (int i = 0; i < array.length; i++) {

            String name = DataVariables.array[i][0];
            String storedLat = DataVariables.array[i][2];
            String storedLon = DataVariables.array[i][3];
            BAY_AREA_LANDMARKS.put(name, new LatLng(Double.valueOf(storedLat), Double.valueOf(storedLon)));
        }
    }
}
