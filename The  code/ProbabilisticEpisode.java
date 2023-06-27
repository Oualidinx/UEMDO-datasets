/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.pfv.spmf.algorithms.episodes.upemdo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oualid
 */
public class ProbabilisticEpisode {
    
    private List<String> events;
    private int support;
    private List<ProbabilisticOccurrence> occurrences;
    private double expectedSupport;
    public ProbabilisticEpisode() {
        this.events = new ArrayList<>();
        this.support = 0;
        this.occurrences = new ArrayList<>();
    }

    public ProbabilisticEpisode(List<String> _events) {
        this.events = _events;
        this.support = 0;
        this.occurrences = new ArrayList<>();
    }

    public void increaseSupport() {
        this.support++;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public int getSupport() {
        return this.support;
    }

    public void add(ProbabilisticOccurrence _occurrences) {
        this.occurrences.add(_occurrences);
    }

    public void setOccurrences(List<ProbabilisticOccurrence> _occurrences) {
        this.occurrences = _occurrences;
    }

    public List<ProbabilisticOccurrence> getOccurrences() {
        return this.occurrences;
    }

    public List<String> getEvents() {
        return this.events;
    }
    
    public String getEvent(int index) {
		return this.events.get(index);
    }

    public boolean Contains(String event) {
    	return this.events.indexOf(event) >= 0;
    }

    public int getSize() {
        return this.events.size();
    }

    public boolean Equals(ProbabilisticEpisode epi) {
    	
    	if (this.getSize() != epi.getSize())
    		return false;
    	boolean stop = false;
    	int i=0;
    	while((i<epi.getSize()) && !stop) {
    		if (!epi.getEvents().contains(this.getEvent(i))) {
    			stop= true;
    		}
    		i++;
    	}
    	if (stop)
    		return false;
    	
    	return true;
    }

    @Override
    public String toString() {
        String string = "<";
        int key = 0;
        while(key < this.events.size()) {
        //for (String event : this.events) {
            string = string + this.events.get(key);
            if (key < (this.events.size() - 1)) {
                string = string + ",";
            } else {
                string = string + ">";
            }
            key = key + 1;
        }
        return string;
    }

    public void setExpectedSupport(double expectedSupport){
        this.expectedSupport = expectedSupport;
    }

    public double getExpectedSupport() {
        return expectedSupport;
    }

    /**
     *
     * @param event_types the set of event types to compare with
     * @return true if they are identical false otherwise
     */
    public boolean isIdenticalWith(List<String> event_types){
        if (this.events.get(this.events.size()-1) == event_types.get(event_types.size()-1))
                return this.isIdenticalWith(event_types.subList(0,event_types.size()-1));
        return false;
    }
}
