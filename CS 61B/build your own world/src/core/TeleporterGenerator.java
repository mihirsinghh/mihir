package core;

public class TeleporterGenerator {
    Position pos;
    TeleporterToPointMap teleporterToPointMap;
    PointToRoomMap pointToRoomMap;
    RoomGenerator roomGen;

    int maxNum;

    public TeleporterGenerator(Position pos, TeleporterToPointMap ttpMap, RoomGenerator roomGen) {
        this.pos = pos;
        this.teleporterToPointMap = ttpMap;
        this.roomGen = roomGen;
        this.pointToRoomMap = roomGen.pointToRoomMap;
        maxNum = roomGen.maxNumRooms / 2;
    }

    public void placeTeleporters() {
        int placed = 0;

        while (placed < maxNum) {
            Teleporter teleporter = new Teleporter(pos);
            if (validLocation(teleporter)) {
                teleporter.create();
                teleporterToPointMap.addTeleporter(teleporter);
                placed += 1;
            }
        }
    }

    /*
    Determines whether this teleporter is being placed in a valid location or not
    A valid location is constituted by the following:
        1. The teleporter is in a different room from all teleporters currently in the world
     */
    public boolean validLocation(Teleporter teleporter) {
        int numValid = 0;

        for (Teleporter eachTeleporter : teleporterToPointMap.allTeleporters()) {
            Point thisPoint = teleporterToPointMap.getPoint(eachTeleporter);
            int thisTeleportersRoomID = pointToRoomMap.getID(thisPoint.getX(), thisPoint.getY());
            int givenTeleportersRoomID = pointToRoomMap.getID(teleporter.x, teleporter.y);

            if (thisTeleportersRoomID != givenTeleportersRoomID) {
                numValid += 1;
            }
        }
        return numValid == teleporterToPointMap.numTeleporters();
    }
}
