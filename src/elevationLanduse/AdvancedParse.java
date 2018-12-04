package elevationLanduse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class AdvancedParse {
    public static void parsePlanetStream(InputStream in) throws NoSuchElementException {
        Scanner sc = new Scanner(in);
        
        //sc.useDelimiter("<changeset>");
        while(sc.hasNext()) System.out.println(sc.nextLine());
        //sc.useDelimiter("");
        //System.out.println(sc.nextLine());
    }
    
    // THIS IS NOT WORKING; THE ONE BELOW AS WELL NOT WORKING
    public static InputStream streamPbfPlanet(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
        Scanner sc = new Scanner(in);
        File f = new File("C:/tmp/latest.pbf");
        f.createNewFile();
        FileWriter out = new FileWriter(f);
        int lines = 0;
        while(lines < 1024*1024 && sc.hasNext()) {
            String line = sc.nextLine();
            lines += line.length();
            out.write(line);
        }
        return null;
    }
    
    public static InputStream streamBzPlanet(URL url) throws IOException, CompressorException {
        URLConnection urlConnection = url.openConnection();
        BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
        CompressorInputStream cis = new CompressorStreamFactory().createCompressorInputStream(in);
        return cis;
    }
}
