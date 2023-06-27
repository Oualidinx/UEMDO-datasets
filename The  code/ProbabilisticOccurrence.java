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

public class ProbabilisticOccurrence {

    private long start;
    private long end;
    private double probability;
    private List<Double> probabilities;
    private List<Integer> timestamps;
    private List<String> events;

    public ProbabilisticOccurrence() {
        this.start = 0;
        this.end = 0;
        this.probability = 1;//the product
        this.events = new ArrayList<>();
        this.timestamps = new ArrayList<>();
        this.probabilities = new ArrayList<>();
    }

    public int getTimeStamp(String _event) {

        return this.timestamps.get(events.indexOf(_event));
    }

    public double getProbability(int index) {
        if (index >= this.probabilities.size()) {
            return 1;
        }
        return this.probabilities.get(index);
    }

    public List<Integer> allTimeStamps() {
        return this.timestamps;
    }

    public int length() {
        return this.events.size();
    }

    public void insertTimeStamp(int t) {
        this.timestamps.add(t);
    }

    public void addEvent(String event_type) {
        this.events.add(event_type);
    }

    public List<String> getEvents() {
        return this.events;
    }

    public void setEvents(List<String> _events) {
        this.events = _events;
    }

    public void setTimeStamps(List<Integer> _timeStamps) {

        this.timestamps = _timeStamps;
    }

    /**
     * Tests whether two occurrences are distinct or not
     *
     * @param _occurrence the occurrence to be tested with the one who call the
     * method
     * @return true if the two occurrences are distinct, false otherwise
     */
    public boolean isDistinct(ProbabilisticOccurrence _occurrence) {
        //System.out.println(_occurrence);
        List<Integer> l = _occurrence.allTimeStamps();
        for (int i = 0; i < this.timestamps.size(); i++) {
            if (l.contains(timestamps.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Given a timestamp, this method tests if it exists in the vector of
     * timestamps of this occurrence
     *
     * @param timestamp the time stamp of an event
     * @return boolean whether that timestamp already exists in the timestamps
     * vector or not
     */
    @SuppressWarnings("unlikely-arg-type")
	public boolean Contains(long timestamp) {
        return this.timestamps.contains(timestamp);
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStart() {
        long min = this.timestamps.get(0);
        for (int index = 1; index < this.timestamps.size(); index++) {
            if (this.timestamps.get(index) <= min) {
                min = this.timestamps.get(index);
            }
        }
        this.start = min;
        return this.start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    /**
     *
     * @param prob update the probability of the hole occurrence with another
     * probability
     */
    public void setProbabilityWith(double prob) {
        this.probability = this.probability * prob;
    }
    /**
     * 
     * @param prob the probability of the occurrence
     */
    public void setProbability(double prob){
        this.probability = prob;
    }

    /**
     *
     * @param event the event to search its probabilty
     * @return the probability of the given event as string
     */
    public double getProbability(String event) {
        return this.probabilities.get(this.events.indexOf(event));
    }

    public double getProbability() {
        return this.probability;
    }

    /**
     *
     * @param prob a new probability to add into the set of events of the
     * occurrence
     */
    public void insertProb(double prob) {
        this.probabilities.add(prob);
        this.setProbability(prob);
    }

    public void setProbabilities(List<Double> probs) {
        this.probabilities = probs;
        double _product = 1f;
        for (int i = 0; i < probs.size(); i++) {
            _product = _product * probs.get(i);
        }
        this.probability = _product;
    }

    public List<Double> getProbabilities() {
        return this.probabilities;
    }

    public long getEnd() {
        long max = this.timestamps.get(0);
        for (int index = 1; index < this.timestamps.size(); index++) {
            if (this.timestamps.get(index) >= max) {
                max = this.timestamps.get(index);
            }
        }
        this.end = max;
        return this.end;
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < events.size(); ++i) {
            str = str + "(" + this.events.get(i) + " , " + String.valueOf(this.timestamps.get(i)) + ")";
        }
        str = str + " #PROB " + String.valueOf(this.probability);

        return str;
    }
}
