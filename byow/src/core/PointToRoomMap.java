package core;

import java.util.HashMap;
import java.util.Map;

/*
This class was written by ChatGPT
 */

public class PointToRoomMap {
    private final Map<Point, Integer> pointToRoomMap;

    public PointToRoomMap() {
        pointToRoomMap = new HashMap<>();
    }

    //adds a new Point-roomID pair to the map
    public void addTile(int x, int y, int roomID) {
        Point point = new Point(x, y);
        pointToRoomMap.put(point, roomID);
    }

    // Gets the roomID associated with a specific Point
    public Integer getID(int x, int y) {
        return pointToRoomMap.get(new Point(x, y));
    }
}


