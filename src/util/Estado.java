/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.List;

/**
 *
 * @author Usuario
 */
public class Estado {
    private String stateName;
    private String goTo;
    private List<EstadoNodo> value;
    private Estado nextState;
    
    public Estado(String stateName, List<EstadoNodo> value, Estado st) {
        this.stateName = stateName;
        this.value = value;
    }
    
    public Estado(){
        
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getGoTo() {
        return goTo;
    }

    public void setGoTo(String goTo) {
        this.goTo = goTo;
    }
    
    public List<EstadoNodo> getValue() {
        return value;
    }

    public void setValue(List<EstadoNodo> value) {
        this.value = value;
    }

    public Estado getNextState() {
        return nextState;
    }

    public void setNextState(Estado nextState) {
        this.nextState = nextState;
    }
}
