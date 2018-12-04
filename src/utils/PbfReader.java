package utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import crosby.binary.osmosis.OsmosisReader;

public class PbfReader implements Sink {
    private AtomicInteger nodes = new AtomicInteger(0), rels = new AtomicInteger(0);
    private boolean started = false;
    private HashMap<String, PrintWriter> outs = new HashMap<String, PrintWriter>();
    
    public PbfReader(InputStream inp) {
        //inp = new FileInputStream("c:\\tmp\\germany.osm.pbf"); // Germany
        OsmosisReader osm = new OsmosisReader(inp);
        PbfReader pbfR = this;
        osm.setSink(pbfR);
        osm.run();
        System.out.println(pbfR.nodes + "   " + pbfR.rels);
        for(PrintWriter out : pbfR.outs.values())
            out.close();
    }
    
    @Override
    public void initialize(Map<String, Object> arg0) {}
    
    @Override
    public void process(EntityContainer entityContainer) {
        if(!started) {
            started = true;
            System.out.println("Processing started @ " + System.currentTimeMillis());
        }
        if(entityContainer instanceof NodeContainer) {
            //NodeContainer nC = (NodeContainer) entityContainer;
            //Node n = nC.getEntity();
            nodes.incrementAndGet();
        } else if(entityContainer instanceof WayContainer) {
            Way way = ((WayContainer) entityContainer).getEntity();
            String landuse = null;
            float ele = Float.MIN_VALUE;
            try {
                for(Tag tag : way.getTags()) {
                    if(tag.getKey().equals("landuse"))
                        landuse = tag.getValue();
                    else if(tag.getKey().equals("ele"))
                        ele = parseEle(tag.getValue());
                }
                if(landuse != null && ele != Float.MIN_VALUE) {
                    if(!outs.containsKey(landuse))
                        outs.put(landuse, new PrintWriter("C:\\tmp\\data\\" + landuse + ".dat"));
                    outs.get(landuse).println(ele);
                }
            } catch(NumberFormatException | FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        } else if(entityContainer instanceof RelationContainer) {
            rels.incrementAndGet();
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
            float f;
            //System.out.print(ex.getMessage());
            ele = ele.replaceAll("m", "").replaceAll(",", ".")
                    .replaceAll("ca.", "").replaceAll("NN.", "")
                    .replaceAll("NN", "");
            if(ele.contains("ft"))
                f = Float.parseFloat(ele.replaceAll("ft", "")) * 0.3048f;
            else if(ele.matches("[0-9]+\\s*(-)\\s*[0-9]+")) {
                String[] nums = ele.split("-");
                f = (Float.parseFloat(nums[0]) + Float.parseFloat(nums[1])) / 2.0f;
            } else if(ele.matches("\\D*") || ele.contains("'")) // Unter der Erde, No, no, ...
                return Float.MIN_VALUE;
            else
                f = Float.parseFloat(ele);
            //System.out.println(" => " + f);
            return f;
        }
    }
}
