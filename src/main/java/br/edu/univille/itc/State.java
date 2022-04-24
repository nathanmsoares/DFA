package br.edu.univille.itc;

import java.util.HashMap;
import java.util.Map;

public class State {

    private Map<String, State> statusConnected = new HashMap<>();
    private String stateName;
    private boolean isInicial;
    private boolean isAccept;

    public State(String stateName) {
        this.stateName = stateName;
    }

    public State() {
    }

    public Map<String, State> getStatusConnected() {
        return statusConnected;
    }

    public void setStatusConnected(Map<String, State> statusConnected) {
        this.statusConnected = statusConnected;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public boolean isInicial() {
        return isInicial;
    }

    public void setInicial(boolean inicial) {
        isInicial = inicial;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }
}
