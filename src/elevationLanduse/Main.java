package elevationLanduse;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.compress.compressors.CompressorException;

public class Main {
    public static void main(String[] args) {
        try {
            String latestBz2 = "https://ftp5.gwdg.de/pub/misc/openstreetmap/planet.openstreetmap.org/planet/planet-latest.osm.bz2";
            String latestPbf = "https://ftp5.gwdg.de/pub/misc/openstreetmap/planet.openstreetmap.org/pbf/planet-latest.osm.pbf";
            URL planet = new URL(latestPbf);
            //.parsePlanetStream(AdvancedParse.streamPbfPlanet(planet));
            AdvancedParse.streamPbfPlanet(planet);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}