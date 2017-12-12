package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Slr {

    private String inicial;
    private String inicialMod;
    private String[] productions;
    private Map<String, Map<Integer, List<String>>> mapaProducoes;
    //private Map<String, List<String>> mapaCanonico;
    private List<String> entrada;

    List<Estado> canonicos;

    //private Map<String, Estado> mapaGoTo;
    private int contadorEntrada = 0;
    private int indexCounterInterno = 0;
    private int indexCounterExterno = 0;

    private Estado estadoInicial;
    private Estado estadoFinal;
    private EstadoNodo nodo;
    private int indexFimLista = 0;

    //private String comparator;
    public Slr(String[] productions, String[] mensagem) {

        this.productions = productions;
        this.entrada = new ArrayList<>(Arrays.asList(mensagem));
        createMap();
        goTo();
    }

    private void createMap() {
        mapaProducoes = new LinkedHashMap<>();
        inicial = productions[0].substring(0, productions[0].indexOf("->"));
        inicialMod = inicial + "'";
        for (String s : productions) {
            System.out.println(s);
            Map<Integer, List<String>> m = new HashMap<>();
            String[] split = s.substring(s.indexOf("->") + 2, s.length()).split("\\|");

            for (String piece : split) {
                String[] splitStr = piece.split("\\s+");
                m.put(contadorEntrada, new ArrayList<>(Arrays.asList(splitStr)));
                contadorEntrada++;
            }
            mapaProducoes.put(s.substring(0, s.indexOf("->")), m);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="ALGORITMO">
    private void goTo() {
        //Estado est = new Estado();
        initCanonicos();
        System.out.println("\nInicia Estados");
        Estado estado = estadoInicial;
        estadoFinal = estadoInicial;
        do {
            System.out.println("\nGoto: " + estado.getGoTo());
            System.out.println("Estado: " + estado.getStateName());
            if (estado.getValue() != null) {
                for (EstadoNodo n : estado.getValue()) {
                    List<EstadoNodo> nodeList = new ArrayList<>();
                    System.out.println("\n##" + n.getNaoTerminalEsquerda() + " " + n.getProducaoDividida().toString());
                    Estado novoEstado = new Estado(); //Novo estado
                    int dotListIndex = findDotInList(n.getProducaoDividida());

                    if (!isDotEndOfString(n.getProducaoDividida(), dotListIndex)) { //Verifica se o ponto já percorreu a produção
                        List<String> temp = new ArrayList<>(n.getProducaoDividida());
                        System.out.println("Antes ponto movido: " + temp.toString());
                        temp.set(dotListIndex, temp.get(dotListIndex).replace(".", ""));

                        String symbolAfterDot = temp.get(dotListIndex);
                        System.out.println("Local ponto: " + temp.get(dotListIndex));

                        List<EstadoNodo> nodeListGerada = null;
                        EstadoNodo node = new EstadoNodo();
                        if (temp.size() > 1 && dotListIndex + 1 < temp.size()) {
                            temp.set(dotListIndex + 1, "." + temp.get(dotListIndex + 1));
                            System.out.println("Moveu index ponto: " + temp.toString());
                            //nodeListGerada = ntAfterDot(temp, dotListIndex + 1);
                            //String prodAfterDot = temp.get(dotListIndex + 1);
                            //prodAfterDot = prodAfterDot.substring(prodAfterDot.indexOf(".") + 1);
                            node.setNaoTerminalEsquerda(n.getNaoTerminalEsquerda());
                            node.setProducaoDividida(temp); //Produção dividida já com o ponto movido
                            nodeList.add(node);
                            indexFimLista = 0;
                            findNtAfterDotImproved(nodeList, 0);
                        } else {
                            temp.set(dotListIndex, temp.get(dotListIndex) + ".");
                            node.setNaoTerminalEsquerda(n.getNaoTerminalEsquerda());
                            node.setProducaoDividida(temp); //Produção dividida já com o ponto movido
                            nodeList.add(node);
                            System.out.println("Ponto no final: " + temp.toString());
                        }
                        System.out.println("Ponto movido: " + temp.toString());

                        System.out.println("NodeList original: ");
                        for (EstadoNodo nodeOriginal : nodeList) {
                            System.out.println(nodeOriginal.getProducaoDividida().toString());
                        }

                        if (nodeListGerada != null) {
                            System.out.println("NodeList Gerada:");
                            for (EstadoNodo estNode : nodeListGerada) {
                                System.out.println(estNode.getNaoTerminalEsquerda() + estNode.getProducaoDividida().toString());
                                nodeList.add(estNode);
                            }
                        }

                        createStringRepresentation(nodeList);
                        Estado e = findExistingState(createStringRepresentation(nodeList));
                        if (e == null) {
                            novoEstado.setStateName("I" + indexCounterExterno);
                            System.out.println("Novo Estado: " + novoEstado.getStateName());
                            for (EstadoNodo no : nodeList) {
                                System.out.println(no.getNaoTerminalEsquerda() + no.getProducaoDividida().toString());
                            }
                            novoEstado.setGoTo(estado.getStateName() + ", " + symbolAfterDot);
                            indexCounterExterno++;
                            novoEstado.setValue(nodeList);
                            novoEstado.setNextState(null);
                        } else {
                            System.out.println("Reaproveita estado: " + e.getStateName());
                            novoEstado.setGoTo(estado.getStateName() + ", " + symbolAfterDot);
                            novoEstado.setStateName(e.getStateName());
                            novoEstado.setValue(null);
                        }

                        estadoFinal.setNextState(novoEstado);
                        estadoFinal = novoEstado;
                    }
                }
            }
            estado = estado.getNextState();
            System.out.println("===\n");
        } while (estado != null);
    }

    private boolean isDotEndOfString(List<String> lista, int dotListIndex) {
        String str = lista.get(dotListIndex);
        if (str.indexOf(".") == str.length() - 1) {
            return true;
        } else {
            return false;
        }
    }

    private int findDotInList(List<String> lista) {
        for (int i = 0; i < lista.size(); i++) {
            //System.out.println("Lista(i): " + lista.get(i));
            if (lista.get(i).contains(".")) {
                return i;
            }
        }
        return -1;
    }

    private List<EstadoNodo> ntAfterDot(List<String> temp, int dotListIndex) {
        List<EstadoNodo> lista = new ArrayList<>();
        String str = temp.get(dotListIndex);

        if (str.contains(".") && str.indexOf(".") + 1 < str.length()) { //Se existe ponto e ele não está no fim da string
            char symbolAfterDot = str.charAt(str.indexOf(".") + 1); //Pega o simbolo depois do ponto
            System.out.println("Nao terminal procurado: " + symbolAfterDot);
            Map<Integer, List<String>> m = mapaProducoes.get(String.valueOf(symbolAfterDot));
            if (m != null) {
                for (Map.Entry<Integer, List<String>> e : m.entrySet()) {

                    EstadoNodo novoNodo = new EstadoNodo();
                    novoNodo.setNaoTerminalEsquerda(String.valueOf(symbolAfterDot));
                    System.out.println(e.getKey() + e.getValue().toString());
                    List<String> prodSplited = new ArrayList<>(e.getValue());
                    prodSplited.set(0, "." + prodSplited.get(0));
                    System.out.println("A adicionar: " + symbolAfterDot + e.getKey() + prodSplited.toString());
                    novoNodo.setProducaoDividida(prodSplited);
                    lista.add(novoNodo);

                    char charAfterDot = prodSplited.get(0).charAt(1);
                    System.out.println("to be found: " + charAfterDot);
                    if (mapaProducoes.get(String.valueOf(charAfterDot)) != null) {
                        System.out.println("found: " + charAfterDot);
                        for (Map.Entry<Integer, List<String>> map : mapaProducoes.get(String.valueOf(charAfterDot)).entrySet()) {
                            if (!isLoop(charAfterDot, map)) {
                                System.out.println("Não é loop: " + charAfterDot + " = " + map.getValue().get(0).charAt(0));
                                System.out.println("A adicionar 2: " + map.getValue().toString());
                                novoNodo = new EstadoNodo();
                                novoNodo.setNaoTerminalEsquerda(String.valueOf(charAfterDot));
                                prodSplited = new ArrayList<>(map.getValue());
                                prodSplited.set(0, "." + prodSplited.get(0));
                                System.out.println("Deriva: " + charAfterDot + map.getKey() + prodSplited);
                                novoNodo.setProducaoDividida(prodSplited);
                                lista.add(novoNodo);
                            }
                        }
                    }
                }
            }
        }
        return lista;
    }

    private List<EstadoNodo> findNtAfterDotImproved(List<EstadoNodo> nodeList, int indexLastAdded) {
        printNodeLis(nodeList);
        System.out.println("Index procurado: " + indexLastAdded);

        EstadoNodo lastNode = nodeList.get(indexLastAdded);
        System.out.println("CurrentNode: " + lastNode.getNaoTerminalEsquerda() + lastNode.getProducaoDividida().toString());
        String afterPoint = "";
        for (int i = 0; i < lastNode.getProducaoDividida().size(); i++) {
            String pedaco = lastNode.getProducaoDividida().get(i);
            if (pedaco.contains(".")) {
                afterPoint = pedaco.substring(pedaco.indexOf(".") + 1);
            }
        }
        System.out.println("Simbolo após o ponto: " + afterPoint);
        Map<Integer, List<String>> m = mapaProducoes.get(afterPoint);
        if (m != null) {
            System.out.println("Encontrou");
            int countAdded = 0;
            for (Map.Entry<Integer, List<String>> e : m.entrySet()) {
                System.out.println("To be added: " + afterPoint + e.getValue().toString());
                EstadoNodo novoNodo = new EstadoNodo();
                List<String> prodSplit = new ArrayList<>(e.getValue());
                prodSplit.set(0, "." + prodSplit.get(0));
                if (!alreadyInNodeList(nodeList, afterPoint + prodSplit.toString())) {
                    novoNodo.setNaoTerminalEsquerda(afterPoint);
                    novoNodo.setProducaoDividida(prodSplit);
                    nodeList.add(novoNodo);
                    
                } else {
                    System.out.println("Ja existe");
                }
                countAdded++;
            }

            indexFimLista += countAdded;
            System.out.println("Index do fim lista: " + indexFimLista);
            if (countAdded > 0) {
                printNodeLis(nodeList);
                nodeList = findNtAfterDotImproved(nodeList, indexLastAdded + 1);
            }

        } else {
            System.out.println("Não encontrou");
        }

        return nodeList;
    }

    private boolean alreadyInNodeList(List<EstadoNodo> nodeList, String stringRepresentation) {
        System.out.println("StringRepresentation: " + stringRepresentation);
        for (EstadoNodo nodo : nodeList) {
            if (stringRepresentation.equals(nodo.getNaoTerminalEsquerda() + nodo.getProducaoDividida().toString())) {
                System.out.println(nodo.getNaoTerminalEsquerda() + nodo.getProducaoDividida().toString());
                return true;
            }
        }
        return false;
    }

    private void printNodeLis(List<EstadoNodo> nodeList) {
        System.out.println("\n--- NodeList ---");
        nodeList.forEach((e) -> {
            System.out.println(e.getNaoTerminalEsquerda() + e.getProducaoDividida().toString());
        });

    }

    private boolean isLoop(char key, Map.Entry<Integer, List<String>> map) {
        char first = map.getValue().get(0).charAt(0);
        if (first == key) {
            System.out.println("Is loop: " + key + " = " + first);
            return true;
        }
        return false;
    }

    private String createStringRepresentation(List<EstadoNodo> nodos) {
        String represetation = "";
        for (EstadoNodo n : nodos) {

            represetation = represetation + n.getNaoTerminalEsquerda() + n.getProducaoDividida().toString();
        }

        System.out.println("Representação estado em string: " + represetation);
        return represetation;
    }

    private Estado findExistingState(String stringRepresentation) {
        System.out.println("Recebido: " + stringRepresentation);
        Estado estado = estadoInicial;

        while (estado != null) {
            List<EstadoNodo> nodos = estado.getValue();

            String estadoString = "";
            if (nodos != null) {
                for (EstadoNodo node : nodos) {
                    estadoString = estadoString + node.getNaoTerminalEsquerda() + node.getProducaoDividida().toString();
                }

                System.out.println("Encontrado: " + estadoString);
                if (estadoString.equals(stringRepresentation)) {
                    System.out.println("já existe");
                    return estado;
                }
            }
            estado = estado.getNextState();
        }

        return null;
    }

    private void initCanonicos() {
        //Estado estado = new Estado();
        List<EstadoNodo> line = new ArrayList<>();
        estadoInicial = new Estado();
        nodo = new EstadoNodo();
        nodo.setNaoTerminalEsquerda(inicialMod); //S'
        List<String> l = new ArrayList<>();
        l.add(".S");
        nodo.setProducaoDividida(l);

        line.add(nodo);

        mapaProducoes.get(inicial).entrySet().forEach((e) -> {
            nodo = new EstadoNodo();
            nodo.setNaoTerminalEsquerda(inicial);
            List<String> temp = new ArrayList<>(e.getValue());
            temp.set(0, "." + temp.get(0));
            nodo.setProducaoDividida(temp);
            line.add(nodo);
        });
        estadoInicial.setNextState(null);
        estadoInicial.setStateName("I" + indexCounterExterno);
        estadoInicial.setGoTo("I" + indexCounterExterno);
        estadoInicial.setValue(line);
        indexCounterExterno++;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TABELA">
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="PRINT">
    public void printMapa() {
        System.out.println("\n=== MAPA ===");
        for (Map.Entry<String, Map<Integer, List<String>>> e : mapaProducoes.entrySet()) {
            e.getValue().entrySet().forEach((f) -> {
                System.out.println(f.getKey() + e.getKey() + " : " + f.getValue().toString());
            });
        }
    }

    public void printCanonicos() {
        System.out.println("\n=== Caninicos ===");
        Estado estado = estadoInicial;
        System.out.println("\nNome: " + estado.getStateName());
        if (estado.getValue() != null) {
            for (EstadoNodo n : estado.getValue()) {
                System.out.println(n.getNaoTerminalEsquerda() + " " + n.getProducaoDividida().toString());
            }
            System.out.println("\n");
        }
    }

    public void printEstados() {
        System.out.println("\n=== ESTADOS ===");
        Estado est = estadoInicial;
        while (est != null) {
            System.out.println("Goto: " + est.getGoTo());
            if (est.getValue() != null) {
                est.getValue().forEach((e) -> {
                    System.out.print(e.getNaoTerminalEsquerda() + e.getProducaoDividida().toString() + "\n");

                });
            }
            System.out.println("Estado: " + est.getStateName() + "\n");
            est = est.getNextState();
        }

    }

    public void printMensagem() {
        System.out.println("\n=== MENSAGEM ===");
        System.out.print(entrada.toString());
    }
    //</editor-fold>
}
