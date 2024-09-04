package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
This class was written by ChatGPT
 */

public class TeleporterToPointMap {
    private final Map<Teleporter, Point> map;

    public TeleporterToPointMap() {
        map = new HashMap<>();
    }

    public void addTeleporter(Teleporter teleporter) {
        Point point = new Point(teleporter.x, teleporter.y);
        map.put(teleporter, point);
    }

    public Point getPoint(Teleporter teleporter) {
        return map.get(teleporter);
    }

    public Set<Teleporter> allTeleporters() {
        return map.keySet();
    }

    public int numTeleporters() {
        return map.size();
    }
}


