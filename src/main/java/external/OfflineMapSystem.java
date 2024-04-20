package external;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.Profile;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;

import model.TransportMode;

import java.util.Arrays;
import java.util.Locale;

/**
 * {@link OfflineMapSystem} is an implementation of the graphhopper {@link MapSystem} for our purposes of providing
 * directons for {@link model.User}s to {@link model.Event}s.
 */

public class OfflineMapSystem implements MapSystem{
    private final GraphHopper hopper;

    public OfflineMapSystem() {
        this.hopper = createGraphHopperInstance();
    }

    static GraphHopper createGraphHopperInstance(){

        GraphHopper hopper = new GraphHopper();

        hopper.setOSMFile("./scotland-latest.osm.pbf");
        hopper.setGraphHopperLocation("./GraphHopperCache");

        hopper.setProfiles(Arrays.asList(
                new Profile("car").setVehicle("car").setWeighting("shortest"),
                new Profile("bike").setVehicle("bike").setWeighting("shortest"),
                new Profile("foot").setVehicle("foot").setWeighting("shortest"),
                new Profile("wheelchair").setVehicle("wheelchair").setWeighting("shortest")
        ));


        hopper.importOrLoad();

        return hopper;
    }
    @Override
    public GHPoint convertToCoordinates(String location) {
        String[] location_numeric = location.split("\\s+");
        double latitude = Double.parseDouble(location_numeric[0]);
        double longitude = Double.parseDouble(location_numeric[1]);
        return new GHPoint(latitude, longitude);
    }

    @Override
    public boolean isPointWithinMapBounds(GHPoint point) {
        return this.hopper.getBaseGraph().getBounds().contains(point.getLat(), point.getLon());
    }

    @Override
    public ResponsePath routeBetweenPoints(TransportMode transportMode, GHPoint startPoint, GHPoint endPoint) {
        GHRequest req = new GHRequest(startPoint, endPoint).setProfile(String.valueOf(transportMode));

        GHResponse rsp = hopper.route(req);

        return rsp.getBest();
    }

    @Override
    public Translation getTranslation() {

        return hopper.getTranslationMap().getWithFallBack(Locale.UK);
    }

    @Override
    public void close() {
        hopper.close();
    }

    /*@Override
    public boolean isValidAddress(String address) {

        String[] addressSplit = address.split("\\s+");

        if (addressSplit.length != 2) {
            return false;
        }
        try {
            double lat = Double.parseDouble(addressSplit[0]);
            double lng = Double.parseDouble(addressSplit[1]);
            return true;
        }
        catch (NumberFormatException e) {
            // Input string contains non-numeric values
            return false;
        }
    }*/ //would be useful to reduce code duplication in UpdateConsumerProfileCommand, RegisterConsumerCommand,
        // CreateEventCommand
}
