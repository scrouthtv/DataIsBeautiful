package elevationLanduse;

import java.io.IOException;
import java.net.URL;

import utils.PbfReader;

public class Main {
    public static void main(String[] args) {
        try {
            String latestBz2 = "https://ftp5.gwdg.de/pub/misc/openstreetmap/planet.openstreetmap.org/planet/planet-latest.osm.bz2";
            String latestPbf = "https://ftp5.gwdg.de/pub/misc/openstreetmap/planet.openstreetmap.org/pbf/planet-latest.osm.pbf";
            URL planet = new URL(latestPbf);
            PbfReader pbf = new PbfReader(planet.openConnection().getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}