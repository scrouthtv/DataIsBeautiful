package mergeFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {
    private static final int steps = 50; // parse to steps of 50
    private static final String basePath = "c:\\tmp\\data\\"; 
    private static List<File> files = new ArrayList<File>();
    
    public static void main(String[] args) {
        File path = new File(basePath);
        files.addAll(allFiles(path, "dat", 1024));
        printFiles();
        String in;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.print("\nMerged filename [leave empty for finishing]:\n  ");
            in = sc.nextLine();
            if(in == "") break;
            //System.out.println("Available files:");
            printFiles();
            System.out.print("Specify files, seperated with comma:\n  ");
            String fs = sc.nextLine();
            List<File> specFiles = new ArrayList<File>();
            for(int i = 1; i <= files.size(); i++)
                if(arrContains(fs.split(","), Integer.toString(i)))
                    specFiles.add(files.get(i - 1));
            files.removeAll(specFiles);
            
            TreeMap<Integer, Integer> res = new TreeMap<Integer, Integer>();
            int sum = 0;
            for(File f : specFiles) {
                BufferedReader fIn;
                try {
                    fIn = new BufferedReader(new FileReader(f));
                    String line;
                    while((line = fIn.readLine()) != null) {
                        int ele = ((int) Float.parseFloat(line)) / steps;
                        res.put(ele, res.containsKey(ele) ? res.get(ele) + 1 : 1);
                        sum++;
                    }
                    fIn.close();
                } catch (NumberFormatException | IOException ex) {
                    ex.printStackTrace(); // almost impossible
                }
            }
            
            try {
                PrintWriter out = new PrintWriter(basePath + "out\\" + in + ".dat");
                for(Entry<Integer, Integer> e : res.entrySet())
                    out.write(e.getKey() + ":" + ((float) e.getValue() / (float) sum) + "\n");
                out.close();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        } while(true);
        sc.close();
    }
    
    private static void printFiles() {
        int i = 1;
        for(File f : files)
            System.out.println(" [" + i++ + "] " + f.getName());
    }
    
    private static boolean arrContains(String[] arr, String pattern) {
        for(String s : arr)
            if(s.equals(pattern))
                return true;
        return false;
    }
    
    private static List<File> allFiles(File dir, String ext, long minFileSize) {
        List<File> allFiles = new ArrayList<File>();
        for(File f : dir.listFiles()) {
            try {
                String thisExt = f.getName();
                thisExt = thisExt.substring(thisExt.length() - ext.length() - 1, thisExt.length());
                if(thisExt.equalsIgnoreCase("." + ext) && f.length() > minFileSize)
                    allFiles.add(f);
            } catch(StringIndexOutOfBoundsException ex) {
                // skip this "file" - likely a directory
            }
        }
        return allFiles;
    }

}
