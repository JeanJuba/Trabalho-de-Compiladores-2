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
public class EstadoNodo {
    private String naoTerminalEsquerda; 
    private List<String> producaoDividida;

    public String getNaoTerminalEsquerda() {
        return naoTerminalEsquerda;
    }

    public void setNaoTerminalEsquerda(String naoTerminalEsquerda) {
        this.naoTerminalEsquerda = naoTerminalEsquerda;
    }

    public List<String> getProducaoDividida() {
        return producaoDividida;
    }

    public void setProducaoDividida(List<String> producaoDividida) {
        this.producaoDividida = producaoDividida;
    }
    
}
