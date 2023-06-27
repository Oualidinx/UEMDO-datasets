/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.pfv.spmf.algorithms.episodes.upemdo;

import ca.pfv.spmf.tools.MemoryLogger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author oualid
 */
public class AlgoUPEMDO {

    private long startExecutionTime;
    private long endExecutionTime;
    private List<ProbabilisticEpisode> ProbabilisticFrequentEpisodes;
    private List<String> ValidRules;
    private int CandidateProbabilisticEpisodesCount;
    private int nbFrequent;
    private int maxsize;

    public AlgoUPEMDO() {
        this.nbFrequent = 0;
        this.CandidateProbabilisticEpisodesCount = 0;
        this.ProbabilisticFrequentEpisodes = new ArrayList<ProbabilisticEpisode>();
        this.ValidRules = new ArrayList<String>();
        this.maxsize = 1;

    }

    public double ExpectedSupport(List<ProbabilisticOccurrence> occurrences) {
        double sum = 0;
        for (ProbabilisticOccurrence occurrence : occurrences) {
            sum = sum + occurrence.getProbability();
        }
        return sum;
    }

    /**
     *
     * @param events event type list
     * @return
     */
    public boolean isInjective(List<String> events) {
        if (events.isEmpty()) {
            return true;
        }
        String event = events.get(events.size() - 1);
        events = events.subList(0, events.size() - 1);
        if (events.contains(event)) {
            return false;
        }
        return isInjective(events);
    }

    /**
     *
     * @param alpha The ProbabilisticEpisode to grow
     * @param singleEvent single ProbabilisticEpisode to grow alpha by.
     * @return the set of probabilistic distinct occurrences of the new ProbabilisticEpisode
     * composed by alpha and the single event ProbabilisticEpisode "beta"
     */
    public List<ProbabilisticOccurrence> DistinctOccurrenceRecognition(
            ProbabilisticEpisode alpha,
            ProbabilisticEpisode singleEvent) {
        List<ProbabilisticOccurrence> oc_1 = alpha.getOccurrences(), oc_2 = singleEvent.getOccurrences(), new_occurrences = new ArrayList<>();
        List<Integer> timestamps;
        int i = 0, j = 0;
        boolean found;
        int taille_1 = oc_1.size(), taille_2 = oc_2.size();
        ProbabilisticOccurrence I1, I2, tempOccurrence;
        List<String> events;
        while (i < taille_1) {
            I1 = oc_1.get(i);
            found = false;
            while ((j < taille_2) && (!found)) {
                I2 = oc_2.get(j);
                tempOccurrence = new ProbabilisticOccurrence();
                
                events = new ArrayList<>(I1.getEvents());
                events.add(I2.getEvents().get(0));
                tempOccurrence.setEvents(events);
                events = null;
                
                timestamps = new ArrayList<>(I1.allTimeStamps());
                timestamps.add(I2.allTimeStamps().get(0));
                tempOccurrence.setTimeStamps(timestamps);
                timestamps = null;
                
                tempOccurrence.setProbability(I1.getProbability());
                tempOccurrence.setProbabilityWith(I2.getProbabilities().get(0));
                for (int t = 0; t < new_occurrences.size(); t++) {
                    if (new_occurrences.get(t).isDistinct(tempOccurrence)) {
                        found = true;
                    } else {
                        if (new_occurrences.get(t).allTimeStamps().contains(I2.allTimeStamps().get(0))) {
                            j = j + 1;
                        } else {
                            i = i + 1;
                        }
                        found = false;
                        break;
                    }
                }

                if (found || new_occurrences.isEmpty()) {
                    new_occurrences.add(tempOccurrence);
                    j = j + 1;
                    i = i + 1;
                    found = true;
                }
                
                if (i >= taille_1) {
                    break;
                } else {
                    I1 = oc_1.get(i);
                }
                tempOccurrence = null;
            }
            if (j >= taille_2) {
                break;
            }
        }
        return new_occurrences;
    }

    /**
     *
     * @param minsup the support threshold
     * @param singleEventProbabilisticEpisodes list of candidate ProbabilisticEpisodes of size 1
     * @return the list of frequent ProbabilisticEpisode under distinct occurrences-based
     * frequency
     * @throws IOException
     */
    public List<ProbabilisticEpisode> FrequentEpisodes(double minsup,
            Map<String, List<ProbabilisticOccurrence>> singleEventProbabilisticEpisodes)
            throws IOException {
        MemoryLogger.getInstance().reset();
        List<ProbabilisticEpisode> f_ProbabilisticEpisode = new ArrayList<>();
        List<ProbabilisticEpisode> f_size_1 = new ArrayList<>();
        this.startExecutionTime = System.currentTimeMillis();
        Object[] t_episodes = singleEventProbabilisticEpisodes.keySet().toArray();
        this.CandidateProbabilisticEpisodesCount = t_episodes.length;
        double t_sup;
        List<ProbabilisticOccurrence> occurrences;
        ProbabilisticEpisode t_epi, alpha;
        ArrayList<String> t_events;
        for (int i=0; i < t_episodes.length; i++) {
            t_sup = ExpectedSupport(singleEventProbabilisticEpisodes.get(t_episodes[i].toString()));
            if (t_sup >= minsup) {
                t_events = new ArrayList<>();
                Collections.addAll(t_events, StrToList(t_episodes[i].toString()));
                t_epi = new ProbabilisticEpisode(t_events);
                occurrences = singleEventProbabilisticEpisodes.get(t_episodes[i].toString());
                t_epi.setOccurrences(occurrences);
                t_epi.setExpectedSupport(t_sup);
                nbFrequent = nbFrequent + 1;
                f_size_1.add(t_epi);
                f_ProbabilisticEpisode.add(t_epi);
            }
        }
        int i = 0, j=0, k=0;
        while (i < f_ProbabilisticEpisode.size()) {
            alpha = f_ProbabilisticEpisode.get(i);
            while (j < f_size_1.size()) {
                t_events = new ArrayList<>();
                Collections.addAll(t_events, StrToList(alpha.toString()));
                t_events.add(t_events.size(), StrToList(f_ProbabilisticEpisode.get(j).toString())[0]);
                t_epi = new ProbabilisticEpisode(t_events);
                t_events = null;
                this.CandidateProbabilisticEpisodesCount++;
                t_sup = t_epi.getExpectedSupport();
                if (isInjective(t_epi.getEvents())) {
                    occurrences = DistinctOccurrenceRecognition(alpha, f_size_1.get(j));
                    //t_epi.setOccurrences(newOccurrences);
                    t_sup = ExpectedSupport(occurrences);
                    //t_epi.setExpectedSupport();
                    if (t_sup >= minsup) {
                        t_epi.setOccurrences(occurrences);
                        t_epi.setExpectedSupport(t_sup);
                        f_ProbabilisticEpisode.add(t_epi);
                        alpha = t_epi;
                        nbFrequent = nbFrequent +1;
                        if (t_epi.getEvents().size() >= this.maxsize) {
                            this.maxsize = t_epi.getEvents().size();
                        }
                        t_epi =null;
                    }
                }
                j++;
            }
            k = k +1;
            j = k +1;
            i++;
        }
        this.endExecutionTime = System.currentTimeMillis();
        MemoryLogger.getInstance().checkMemory();
        setFrequentProbabilisticEpisodes(f_ProbabilisticEpisode);
        return f_ProbabilisticEpisode;
    }

    /**
     *
     * @param path The path to data set to be analyzed by the algorithm
     * @param isComplex indicates whether the sequence is simple or complex
     * @param withProb If it is an uncertain sequence or not
     * @return the list of ProbabilisticEpisodes of size = 1
     * @throws IOException
     */
    public Map<String, List<ProbabilisticOccurrence>> scanSequence(
            String path,
            boolean isComplex,
            boolean withProb)
            throws IOException {

        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path));
        String line;
        Map<String, List<ProbabilisticOccurrence>> SingleEventProbabilisticEpisode = new HashMap<>();
        ProbabilisticOccurrence occ = new ProbabilisticOccurrence();
        double prob = 0;
        int timeStamp;
        String[] lineSplited;
        List<String> events;
        ProbabilisticEpisode epi;
        while ((line = reader.readLine()) != null) {
            lineSplited = line.split("\\#");
            timeStamp = Integer.parseInt(lineSplited[0]);
            if (lineSplited.length >=  2) {
	            if (isComplex) {
	                String[] _events = lineSplited[1].split(" ");
	                for (String e : _events) {
	                    events = new ArrayList<>();
	                    occ = new ProbabilisticOccurrence();
	                    if (withProb) {
	                        events.add(e.split("\\|")[0]);
	                        prob = Double.parseDouble(e.split("\\|")[1]);
	                        occ.insertProb(prob);
	                        occ.addEvent(e.split("\\|")[0]);
	                    } else {
	                        //System.out.print("\t event: "+e);
	                        events.add(e);
	                        occ.insertProb(1);
	                        occ.addEvent(e);
	                    }
	                    epi = new ProbabilisticEpisode(events);
	                    occ.insertTimeStamp(timeStamp);
	
	                    if (SingleEventProbabilisticEpisode.containsKey(epi.toString())) {
	                        SingleEventProbabilisticEpisode.get(epi.toString()).add(occ);
	                    } else {
	                        SingleEventProbabilisticEpisode.put(epi.toString(), new ArrayList<>());
	                        SingleEventProbabilisticEpisode.get(epi.toString()).add(occ);
	                    }
	                }
	            } else {
	                events = new ArrayList<>();
	                if (withProb) {
	                    events.add(lineSplited[1].split("\\|")[0]);
	                    prob = Double.parseDouble(lineSplited[1].split("\\|")[1]);
	                    occ.insertProb(prob);
	                    occ.addEvent(lineSplited[1].split("\\|")[0]);
	                } else {
	                    events.add(lineSplited[1]);
	                    occ.insertProb(1);
	                    occ.addEvent(lineSplited[1]);
	                }
	                epi = new ProbabilisticEpisode(events);
	
	                occ.insertTimeStamp(timeStamp);
	                if (SingleEventProbabilisticEpisode.containsKey(epi.toString())) {
	                    SingleEventProbabilisticEpisode.get(epi.toString()).add(occ);
	                } else {
	                    SingleEventProbabilisticEpisode.put(epi.toString(), new ArrayList<>());
	                    SingleEventProbabilisticEpisode.get(epi.toString()).add(occ);
	                }
	
	            }
            }else {
            	continue;
            }
        }
        reader.close();
        this.CandidateProbabilisticEpisodesCount = SingleEventProbabilisticEpisode.size();
        return SingleEventProbabilisticEpisode;
    }

    /**
     *
     * @param alpha antecedent
     * @param beta consequent
     * @return the number of occurrence of the rule alpha --> beta (support the
     * ProbabilisticEpisode rule)
     */
    public static double ProbabilisticEpisodeRuleSupport(
            ProbabilisticEpisode alpha,
            ProbabilisticEpisode beta) {
        List<ProbabilisticOccurrence> alpha_occurrences = alpha.getOccurrences();
        List<ProbabilisticOccurrence> beta_occurrences = beta.getOccurrences();
        int i = 0, j = 0, k;
        boolean trouve;
        int taille_1 = alpha_occurrences.size();
        int taille_2 = beta_occurrences.size();
        double rule_support = 0;
        ProbabilisticOccurrence occ_1, occ_2;
        do {
        	occ_1 = alpha_occurrences.get(i);
        	trouve = false;
        	k = j;
        	while ((k < taille_2) && (!trouve)) {
        		occ_2 = beta_occurrences.get(k);
        		if ((occ_1.getStart() < occ_2.getStart()) && (occ_1.getEnd() < occ_2.getEnd())) {
        			rule_support = rule_support + occ_1.getProbability();
        			i = i + 1;
        			trouve = true;
        		}
        		k = k + 1;
        	}
        	j = k;
        }while((j < taille_2) && (i < taille_1));
        /*while (i < taille_1) {
            occ_1 = alpha_occurrences.get(i);
            trouve = false;
            while ((j < taille_2) && (!trouve)) {
                k = j + 1;
                while (k < taille_2) {
                    occ_2 = beta_occurrences.get(k);
                    if (((occ_1.getStart() < occ_2.getStart()) && (occ_1.getEnd() < occ_2.getEnd()))) { // &&(occ_2.getStart() - occ_1.getStart() <= span)
                        rule_support = rule_support + occ_1.getProbability();
                        i = i + 1;
                        trouve = true;
                        break;
                    }
                    k = k + 1;
                }
                j = k;
            }
            if (j >= taille_2) {
                break;
            }
        }*/
        return rule_support;
    }


    /**
     * Pruning ProbabilisticEpisode rules with pruning by applying the properties mentionned in the article
     *
     * @param FrequentProbabilisticEpisodes List of frequent ProbabilisticEpisodes obtained using
     * FrequentProbabilisticEpisodes function mentionned before
     * @param minconf The user defined confidence threshold
     * @param minsup The user defined support threshold
     * @return the set of pruned ProbabilisticEpisode rules
     */
    public List<String> EpisodeRulesWithPruning(
            List<ProbabilisticEpisode> FrequentEpisodes,
            double minconf
    ) { 
    	MemoryLogger.getInstance().reset();
    	this.startExecutionTime = System.currentTimeMillis();
        List<String> valid_rules = new ArrayList<>();
        List<ProbabilisticEpisode> P = new ArrayList<>();
        List<ProbabilisticOccurrence> occurrences;
        ProbabilisticEpisode t_epi;
        List<String> t_events;
        //List<ProbabilisticEpisode> f_copy = FrequentEpisodes;
        for (ProbabilisticEpisode pEpisode : FrequentEpisodes) {
            if (pEpisode.getEvents().size() == 1) {
                t_events = new ArrayList<>();
                Collections.addAll(t_events, StrToList(pEpisode.toString()));
                t_epi = new ProbabilisticEpisode(t_events);
                occurrences = pEpisode.getOccurrences();
                t_epi.setOccurrences(occurrences);
                t_epi.setSupport(occurrences.size());
                P.add(t_epi);
            }else {
            	break;
            }
        }
        
        int k=0, i = 0, j=0;
        double rule_support;
        int size_P = P.size(), size_F = FrequentEpisodes.size();
        ProbabilisticEpisode alpha = null, beta = null, root = null;
        List<String> events;
        float conf;
        boolean stop = false;
        while(i < size_F) {
            alpha = FrequentEpisodes.get(i);
            j = 0;
            for (ProbabilisticEpisode x:P) {
            	k = j;
            	root = null;
            	beta = x;
            	stop = false;
            	while(!stop){
            		boolean found = false;
	            	for(ProbabilisticEpisode gamma: FrequentEpisodes) {
	            		if (gamma.Equals(beta)) {
	            			beta.setOccurrences(gamma.getOccurrences());
	            			found = true; 
	            			break;
	            		}
	            	}                
	            	
	                if (found){
	                    rule_support = ProbabilisticEpisodeRuleSupport(alpha, beta);
	                    conf = ((float) rule_support / (float) alpha.getExpectedSupport());
	                    if (conf >= minconf) {
	                        valid_rules.add(alpha + " => " + beta + " #CONF " + conf*100 + " %");
	                        root = beta;
	                    }else{
	                        beta = root;
	                    }
	                }else{
	                    beta = root;   
	                }
                    if ((k>=size_P)||(beta == null)) {
                    	stop=true;
                    }else {
                    	events = new ArrayList<>();
                        Collections.addAll(events, StrToList(beta.toString()));
                        events.add(P.get(k).getEvent(0));
                        beta=new ProbabilisticEpisode(events);
                        k = k + 1;
                    }                    
                }
            	j = j + 1;
            }
            i = i + 1;
        }
        this.endExecutionTime = System.currentTimeMillis();
        MemoryLogger.getInstance().checkMemory();
        return valid_rules;
    }
    
    /**
     * @param FrequentProbabilisticEpisodes List of frequent ProbabilisticEpisodes obtained using
     * FrequentProbabilisticEpisodes function mentionned before
     * @param minconf The user defined confidence threshold
     * @param minsup The user defined support threshold
     * @return the set of ProbabilisticEpisode rules
     */
    
    public List<String> EpisodeRules(List<ProbabilisticEpisode> ProbabilisticFrequentEpisodes, double minconf) {
        MemoryLogger.getInstance().reset();
        this.startExecutionTime = System.currentTimeMillis();
        List<String> valid_rules = new ArrayList<>();
        float conf;
        double rule_support;
        int i = 0, j=0;
        ProbabilisticEpisode alpha, beta;
        while (i < ProbabilisticFrequentEpisodes.size()) {
        	alpha = ProbabilisticFrequentEpisodes.get(i);
        	j=0;
        	while (j < ProbabilisticFrequentEpisodes.size()) {
            	beta = ProbabilisticFrequentEpisodes.get(j);
        		rule_support = ProbabilisticEpisodeRuleSupport(alpha, beta);
                conf = ((float) rule_support / (float) alpha.getExpectedSupport());
                if (conf >= minconf) {
                    valid_rules.add(alpha.toString() + " => " + beta.toString()+ " #CONF " + conf*100 + " %");
                }
                j++;
                beta = null;
            }
        	i++;
        	alpha = null;
        }
        this.endExecutionTime = System.currentTimeMillis();
        MemoryLogger.getInstance().checkMemory();
        this.ValidRules = valid_rules;
        return valid_rules;
    }
    /**
     * Transform a string into an episode
     * @param string string episode to transform in Object of class Episode
     * @return the created object ( episode )
     */
    public static String[] StrToList(String string) {
        int index_1 = string.indexOf("<");
        String tempString = string.substring(index_1 + 1, string.length() - 1);
        if (tempString.contains(",")) {
            return tempString.split(",");
        }
        return new String[]{tempString};
    }

    public String printStats() {
        System.out.println("=============  UPEMDO - STATS =============");
        System.out.println(" Probabilistic candidates count : " + this.CandidateProbabilisticEpisodesCount);
        System.out.println(" The algorithm stopped at size : " + maxsize);
        System.out.println(" Frequent Probabilistic Episodes count : " + nbFrequent);
        System.out.println(" Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
        System.out.println(" Total time ~ : " + (this.endExecutionTime - this.startExecutionTime) + " ms");
        System.out.println("===================================================");
        return "\n=============  UPEMDO - STATS =============\n"
                + " Probabilistic candidates count : " + this.CandidateProbabilisticEpisodesCount + "\n"
                + " The algorithm stopped at size : " + maxsize + "\n"
                + " Frequent Probabilistic Episodes count : " + nbFrequent + "\n"
                + " Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " mb" + "\n"
                + " Total time ~ : " + (this.endExecutionTime - this.startExecutionTime) + " ms" + "\n"
                + "===================================================";
    }

    public int indexOf(ProbabilisticEpisode alpha, List<ProbabilisticEpisode> ProbabilisticFrequentEpisodes){
        int size_F =  ProbabilisticFrequentEpisodes.size();
        ProbabilisticEpisode beta;
        boolean stop = false, founded = false;
        int i = 0, index = -1;
        //System.out.println("From inside the function=> alpha="+alpha);
        while((i < size_F) && !stop) {
        	//System.out.println("From inside the function=> beta="+FrequentProbabilisticEpisodes.get(i));
        	beta = ProbabilisticFrequentEpisodes.get(i);
        	founded = beta.Equals(alpha);
        	if (founded) {
        		stop = true;
        		index = i;
        	}
        	i = i + 1;	
        }
        return index;
    }

    public void setFrequentProbabilisticEpisodes(List<ProbabilisticEpisode> frequentProbabilisticEpisodes) {
    	ProbabilisticFrequentEpisodes = frequentProbabilisticEpisodes;
    }

    public List<String> getValidRules() {
        return ValidRules;
    }

    public void setValidRules(List<String> validRules) {
        ValidRules = validRules;
    }
}
