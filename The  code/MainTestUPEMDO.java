package ca.pfv.spmf.algorithms.episodes.upemdo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainTestUPEMDO {
	static double MINSUP = 0.0001;
	static double MINCONF = 0.1;

	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		System.out.println(filename);
		URL url = MainTestUPEMDO.class.getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
    public static void main(String[] args) throws IOException , RuntimeException{
        String dataset = "db";
        //String pathInput = System.getProperty("user.dir")+"/src/ca/pfv/spmf/test/datasets"+datasetDir+dataset;
        String pathInput = fileToPath(dataset+".txt");
        AlgoUPEMDO algo = new AlgoUPEMDO();
	    Map<String, List<ProbabilisticOccurrence>> singleEpisodeEvent;
	    System.out.println("MINSUP = " + MINSUP + "\nMINCONF=" + MINCONF + "\nSEQUENCE " + dataset);
        //try {
            File file = new File(System.getProperty("user.dir")+"/src/ca/pfv/spmf/test/datasets/2_output_minsup_2000_" +dataset+".txt" );
        	System.out.println(file.getPath());
            FileWriter writer;
            singleEpisodeEvent = algo.scanSequence(pathInput, true, true);
            System.out.print("Frequent Episodes generation...");
            List<ProbabilisticEpisode> probFrequentEpisodes = algo.FrequentEpisodes(MINSUP, singleEpisodeEvent);
            System.out.print("finished! \n");
            writer = new FileWriter(file);
            writer.write("frequent episode generation \n");
            for (ProbabilisticEpisode e : probFrequentEpisodes) {
                writer.write(e + " " + e.getExpectedSupport() + "\n");
                for(ProbabilisticOccurrence occ:e.getOccurrences()) {
                	writer.write("\t("+occ.getStart()+","+occ.getProbability()+") , ("+occ.getEnd()+")\n");
                }
            }
            writer.write(algo.printStats());
            writer.close();
            Runtime.getRuntime().gc();
            /*
            System.out.println("Episode rules generation with naive approach: ");
            List<String> res_rules = algo.EpisodeRules(probFrequentEpisodes, MINCONF);
            file = new File(System.getProperty("user.dir")+"/src/ca/pfv/spmf/test/datasets/1_output_naive_rules_minconf_" + MINCONF + "_minsup_"+ MINSUP + "_" + dataset);
            writer = new FileWriter(file);
            writer.write("Episode rules generation with naive approach \n");
            writer.write("minconf=" + MINCONF+"\n");
            for (String e : res_rules) {
                writer.write(e + "\n");
            }
            System.out.println ("done!");
            writer.write(algo.printStats());
            System.out.println("Number of rules: "+res_rules.size());
            writer.write("\nNumber of rules: "+res_rules.size()+"\n");
            writer.close();
            Runtime.getRuntime().gc();
            
            file = new File(System.getProperty("user.dir")+"/src/ca/pfv/spmf/test/datasets/1_output_pruned_rules_minconf_" + MINCONF + "_minsup_"+ MINSUP + "_" + dataset);
            writer = new FileWriter(file);
            writer.write("episode rules generation with pruning\n");
            System.out.print("Episode rules generation with pruning");	
            res_rules = algo.EpisodeRulesWithPruning(probFrequentEpisodes, MINCONF);
            writer.write("minconf=" + MINCONF+"\n");
            for (String e : res_rules) {
                writer.write(e + "\n");
            }
            System.out.println(" \t done!");
            writer.write(algo.printStats());
            writer.write("\nNumber of rules: "+res_rules.size()+"\n");
            System.out.println("Number of rules: "+res_rules.size());
            writer.close();
            Runtime.getRuntime().gc();*/
         /*} catch (IOException e) {
             e.printStackTrace();
         }*/
         System.out.println("Finished...");
    }
}
