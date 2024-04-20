package external;

import com.graphhopper.ResponsePath;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;
import model.TransportMode;

/**
 * {@link MapSystem} is a map system interface using the Graphhopper API to provide
 * routing and mapping functionality.
 */
public interface MapSystem {

    /**
     * Converts a string representing a location to a {@link GHPoint} object.
     *
     * @param location a string representing a location, e.g. "Appleton Tower, Edinburgh"
     * @return a {@link GHPoint} object representing the coordinates of the location
     */
    GHPoint convertToCoordinates(String location);

    /**
     * Checks whether a {@link GHPoint} object is within the bounds of the map.
     *
     * @param point the {@link GHPoint} object to check
     * @return true if the point is within the bounds of the map, false otherwise
     */
    boolean isPointWithinMapBounds(GHPoint point);

    /**
     * Calculates a route between two points using the specified transport mode.
     *
     * @param transportMode the {@link TransportMode} to use for routing
     * @param startPoint the starting point of the route
     * @param endPoint the starting point of the route
     * @return path {@link ResponsePath} which is the path from start to end point
     */
    ResponsePath routeBetweenPoints(TransportMode transportMode , GHPoint startPoint, GHPoint endPoint);

    /**
     * @return translation {@link com.graphhopper.util.Translation}
     */
    Translation getTranslation();

    /**
     * Closes
     */
    void close();

    //boolean isValidAddress(String address);

}
