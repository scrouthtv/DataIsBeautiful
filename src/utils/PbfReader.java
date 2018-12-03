package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import crosby.binary.osmosis.OsmosisReader;

public class PbfReader implements Sink {
    private static final String[] eleUnits = new String[] {" m"};
    
    private int nodes = 0, rels = 0;
    
    public static void main(String[] args) {
        InputStream inp;
        try {
            inp = new FileInputStream("c:\\tmp\\germany.osm.pbf"); // Germany
            OsmosisReader osm = new OsmosisReader(inp);
            PbfReader pbfR = new PbfReader();
            osm.setSink(pbfR);
            osm.run();
            System.out.println(pbfR.nodes + "   " + pbfR.rels);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void initialize(Map<String, Object> arg0) {}
    
    @Override
    public void process(EntityContainer entityContainer) {
        if(entityContainer instanceof NodeContainer) {
            NodeContainer nC = (NodeContainer) entityContainer;
            Node n = nC.getEntity();
            nodes++;
        } else if(entityContainer instanceof WayContainer) {
            Way way = ((WayContainer) entityContainer).getEntity();
            String landuse = null;
            Float ele = null;
            try {
                for(Tag tag : way.getTags()) {
                    if(tag.getKey().equals("landuse"))
                        landuse = tag.getValue();
                    else if(tag.getKey().equals("ele"))
                        ele = parseEle(tag.getValue());
                }
                if(landuse != null && ele != null) {
                    System.out.println(ele + ": " + landuse);
                }
            } catch(NumberFormatException ex) {
                System.out.println(ex.getMessage());
            }
        } else if(entityContainer instanceof RelationContainer) {
            rels++;
        } else {
            System.out.println("Unknown entity: " + entityContainer.getClass() + ", skipping");
        }
    }
    
    @Override
    public void complete() {}
    
    @Override
    public void close() {}
    
    private static float parseEle(String ele) {
        try {
            return Float.parseFloat(ele);
        } catch(NumberFormatException ex) {
            System.out.println(ex.getMessage());
            for(String s : eleUnits)
                ele = ele.replace(s, "");
            Float f = Float.parseFloat(ele);
            return f;
        }
    }
}
