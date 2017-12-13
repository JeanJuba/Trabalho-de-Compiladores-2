package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Slr {

    private String inicial;
    private String inicialMod;
    private String[] productions;
    private Map<String, Map<Integer, List<String>>> mapaProducoes;

    private Map<String, Set<String>> mapaProducoesFollow;
    private Map<String, Set<String>> mapaFirst;
    private Map<String, Set<String>> mapaFollow;

    private String vazio = "&";

    private List<String> entrada;

    List<Estado> canonicos;

    private int contadorEntrada = 0;
    private int indexCounterExterno = 0;

    private Estado estadoInicial;
    private Estado estadoFinal;
    private EstadoNodo nodo;
    private int indexFimLista = 0;

    private Map<String, Map<String, String>> tabela;
    private Set<String> simbolosEncontrados;

    public Slr(String[] productions, String[] mensagem) {
        this.mapaProducoesFollow = new HashMap<>();
        this.mapaFirst = new HashMap<>();
        this.mapaFollow = new HashMap<>();
        this.productions = productions;
        this.entrada = new ArrayList<>(Arrays.asList(mensagem));
        this.tabela = new HashMap<>();
        createMap();
        createMapAux();
        printMapaAux();
        goTo();
        createTabela();
        first();
        follow();
        
        printMapa();
        printMapaAux();
        printCanonicos();
        printEstados();
        printFirst();
        printFollow();
        fillMapStates();
        printTabela();
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

    private void createMapAux() {
        inicial = productions[0].substring(0, productions[0].indexOf("->"));
        for (String s : productions) {
            s = s.replaceAll("\\s", "");
            mapaProducoesFollow.put(s.substring(0, s.indexOf("->")), new HashSet<>(Arrays.asList(s.substring(s.indexOf("->") + 2, s.length()).split("\\|"))));
        }
        printMapa();
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
            String jaProcessado = "";
            if (estado.getValue() != null) {
                int index = 0;
                for (EstadoNodo n : estado.getValue()) {
                    index++;
                    List<EstadoNodo> nodeList = new ArrayList<>();
                    System.out.println("\n===Próximo Nodo: " + n.getNaoTerminalEsquerda() + " " + n.getProducaoDividida().toString());
                    Estado novoEstado = new Estado(); //Novo estado
                    int dotListIndex = findDotInList(n.getProducaoDividida());

                    if (!isDotEndOfString(n.getProducaoDividida(), dotListIndex) && !jaProcessado(n.getProducaoDividida(), jaProcessado)) { //Verifica se o ponto já percorreu a produção
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

                        List<EstadoNodo> nodeEstadoNodos = estado.getValue();
                        System.out.println("index: " + index + " EstadoNodos Size: " + nodeEstadoNodos.size());
                        for (int i = index; i < nodeEstadoNodos.size(); i++) {
                            EstadoNodo est = nodeEstadoNodos.get(i);
                            int dotPos = findDotInList(est.getProducaoDividida());
                            String dotString = est.getProducaoDividida().get(dotPos);
                            int dotIndexStr = dotString.indexOf(".");
                            if (dotIndexStr + 1 < dotString.length()) {
                                String afterDot = String.valueOf(dotString.charAt(dotIndexStr + 1));
                                System.out.println("Afterdot: " + afterDot + " SymbolAfterDor: " + symbolAfterDot);
                                System.out.println("Ja processados: " + jaProcessado);
                                if (afterDot.equals(symbolAfterDot) && !jaProcessado.contains(afterDot)) {
                                    jaProcessado = jaProcessado + afterDot;
                                    node = new EstadoNodo();
                                    node.setNaoTerminalEsquerda(est.getNaoTerminalEsquerda());
                                    node.setProducaoDividida(moveDot(new ArrayList<>(est.getProducaoDividida()), dotPos));
                                    System.out.println("Colocou: " + n.getNaoTerminalEsquerda() + n.getProducaoDividida());
                                    nodeList.add(node);
                                }
                            }
                        }

                        System.out.println("NodeList original: ");
                        for (EstadoNodo nodeOriginal : nodeList) {
                            System.out.println(nodeOriginal.getNaoTerminalEsquerda() + nodeOriginal.getProducaoDividida().toString());
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

    private boolean jaProcessado(List<String> prodSplit, String jaProcessados) {
        int dotPos = findDotInList(prodSplit);
        String dotString = prodSplit.get(dotPos);
        int dotIndexStr = dotString.indexOf(".");

        if (dotIndexStr + 1 < dotString.length()) {
            String afterDot = String.valueOf(dotString.charAt(dotIndexStr + 1));
            System.out.println("Ja processados: " + jaProcessados + " AfterDot: " + afterDot);
            if (jaProcessados.contains(afterDot)) {
                return true;
            }
        }
        return false;
    }

    private List<String> moveDot(List<String> prodDividida, int dotPos) {
        String str = prodDividida.get(dotPos);
        str = str.replace(".", "");
        prodDividida.set(dotPos, str);
        if (dotPos + 1 < prodDividida.size()) {
            prodDividida.set(dotPos + 1, "." + prodDividida.get(dotPos + 1));
        } else {
            prodDividida.set(dotPos, prodDividida.get(dotPos) + ".");
        }
        return prodDividida;
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

    private List<EstadoNodo> findNtAfterDotImproved(List<EstadoNodo> nodeList, int indexLastAdded) {
        System.out.println("--- Procurando NT depois ponto. ---");
        //printNodeList(nodeList);
        System.out.println("Index procurado: " + indexLastAdded);

        if (indexLastAdded < nodeList.size()) {
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
                System.out.println("Novos nodos: " + countAdded);
                System.out.println("Index do fim lista: " + indexFimLista);
                if (countAdded > 0) {
                    //indexLastAdded ++;
                    printNodeList(nodeList);
                    nodeList = findNtAfterDotImproved(nodeList, indexLastAdded + 1);
                }

            } else {
                System.out.println("Não encontrou");
                nodeList = findNtAfterDotImproved(nodeList, indexLastAdded + 1);
            }
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

    private void printNodeList(List<EstadoNodo> nodeList) {
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
        l.add("." + inicial);
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
    //<editor-fold defaultstate="collapsed" desc="FIRST">
    private void first() {
        for (Map.Entry<String, Set<String>> e : mapaProducoesFollow.entrySet()) {
            Set<String> line = new HashSet<>();

            e.getValue().forEach((s) -> {
                String symbol = s.substring(0, 1);
                System.out.println("Symbol: " + symbol);
                if (symbol.equals("E") || symbol.matches("[a-z0-9]")) {
                    line.add(symbol);
                } else if (symbol.matches("[A-Z]")) {
                    Set<String> temp = findReplacement(symbol);

                    if (temp.contains("E") && s.length() > 1) { //Se existir algo como A->XYZ, X->a|E, Y->b|c, Z->d,e
                        for (String letter : s.split("")) {
                            temp = findReplacement(letter);
                            if (temp.contains("E") == false) {
                                break;
                            }
                        }
                    }
                    line.addAll(temp);

                }
            });
            System.out.println("Line added to FIRST: " + line.toString());
            mapaFirst.put(e.getKey(), line);
        }
        System.out.println("\n");
        printFirst();
    }

    public Set<String> findReplacement(String naoTerminal) {
        Set<String> newFisrt = new HashSet<>();
        System.out.println("Não Terminal: " + naoTerminal);
        if (mapaFirst.get(naoTerminal) == null) {

            mapaProducoesFollow.get(naoTerminal).forEach((e) -> {
                System.out.println("e: " + e);
                String symbol = e.substring(0, 1);
                if (symbol.matches("[a-z0-9]") || symbol.equals("E")) {
                    newFisrt.add(symbol);
                } else {
                    newFisrt.addAll(findReplacement(symbol));
                }

            });
            mapaFirst.put(naoTerminal, newFisrt);
        }

        return mapaFirst.get(naoTerminal);
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="FOLLOW">
    private void follow() {
        mapaProducoesFollow.entrySet().forEach((e) -> { //Loop nas chaves dos mapas
            Set<String> line = new HashSet<>();
            if (e.getKey().equals(inicial)) {
                line.add("$");
            }
            line.addAll(findFollow(e.getKey()));

            mapaFollow.put(e.getKey(), line);
        });
        //removeEmpty();
        printFollow();
    }

    private Set<String> findFollow(String NaoTerminal) {
        Set<String> newfollow = new HashSet<>();
        System.out.println("\n---Var: " + NaoTerminal);
        if (mapaFollow.get(NaoTerminal) == null) {
            System.out.println("Nãp está no mapa");
            mapaProducoesFollow.entrySet().forEach((k) -> { //Loop para achar a ocorrência da chave em outras produções

                if (containsNaoTerminal(k.getValue(), NaoTerminal)) {
                    System.out.println("Key: " + k.getKey() + " contains " + NaoTerminal);
                    k.getValue().forEach((j) -> {
                        System.out.println("K: " + j);

                        if (j.matches(".*(" + NaoTerminal + ")$")) { //se exister algo como [1 ou mais simbolos][Nao terminal analisado]
                            System.out.println("Matches .*(" + NaoTerminal + ")$");
                            System.out.println("Key: " + k.getKey() + " Nao terminal: " + NaoTerminal);
                            if (!NaoTerminal.equals(k.getKey())) {
                                if (mapaFollow.get(k.getKey()) == null) { //Se nao existir o follow do Simbolo a esquerda da produção então procura ele
                                    System.out.println("Find follow: " + k.getKey());
                                    newfollow.addAll(findFollow(k.getKey()));
                                } else {
                                    System.out.println("Pega follow: " + k.getKey());
                                    newfollow.addAll(mapaFollow.get(k.getKey()));
                                }
                            }
                        }

                        if (j.matches(".*(" + NaoTerminal + "[a-z0-9]+[a-zA-Z0-9]*)$")) { //se exister algo como [0 ou mais simbolos][Nao terminal analisado][Terminal]
                            System.out.println("Matches .*(" + NaoTerminal + "[a-z0-9]+[a-zA-Z0-9]*)$");
                            System.out.println("Adiciona terminal: " + j.substring(j.indexOf(NaoTerminal) + 1, j.indexOf(NaoTerminal) + 2));
                            newfollow.add(j.substring(j.indexOf(NaoTerminal) + 1, j.indexOf(NaoTerminal) + 2));
                        }

                        if (j.matches(".*(" + NaoTerminal + "[A-Z]+[a-zA-Z0-9]*)$")) {
                            System.out.println("Matches .*(" + NaoTerminal + "[A-Z]+[a-zA-Z0-9]*)$");
                            String nt = j.substring(j.indexOf(NaoTerminal) + 1, j.indexOf(NaoTerminal) + 2);
                            System.out.println("Adiciona FIRST de: " + nt);
                            newfollow.addAll(mapaFirst.get(nt));

                            if (mapaProducoesFollow.get(nt).contains("E")) { //Se derivar palavra vazia "E" também adiciona o follow
                                System.out.println("--Procura FOLLOW: " + nt);
                                newfollow.addAll(findFollow(nt));
                            }
                        }

                    });
                }
            });
            System.out.println("Line added to FOLLOW: " + newfollow.toString());
            mapaFollow.put(NaoTerminal, newfollow);
        }

        return mapaFollow.get(NaoTerminal);
    }

    private boolean containsNaoTerminal(Set<String> set, String NaoTerminal) {
        if (set.stream().anyMatch((s) -> (s.contains(NaoTerminal)))) {
            return true;
        }
        return false;
    }

    private void removeEmpty() {
        mapaFollow.values().forEach((e) -> {
            e.remove("E");
        });

    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="TABELA">
    public void createTabela() {
        Estado est = estadoInicial;
        Set<String> simbolos = new HashSet<>();
        for (String s : productions) {
            s = s.substring(s.indexOf("->") + 2, s.length());
            s = s.replaceAll("\\|", " ");
            System.out.println("Novo s: " + s);
            String[] split = s.split("\\s+");

            System.out.println(Arrays.toString(split));
            for (String str : split) {
                simbolos.add(str);
            }
        }
        simbolosEncontrados = new HashSet<>(simbolos);
        System.out.println("Simbolos: " + simbolos.toString());
        while (est != null) {
            tabela.put(est.getStateName(), createColumns(simbolos));
            est = est.getNextState();
        }
    }

    private Map<String, String> createColumns(Set<String> columnNames) {
        Map<String, String> mapa = new HashMap<>();

        for (String str : columnNames) {
            mapa.put(str, "");
        }
        return mapa;
    }

    private void fillMapStates() {
        System.out.println("--- Fill Map ---");
        for (String s : simbolosEncontrados) {
            if (mapaProducoes.get(s) == null) {
                System.out.println("s: " + s);
                Estado estado = estadoInicial;
                while (estado != null) {
                    String strName = estado.getGoTo();

                    System.out.println("Goto: " + strName);
                    if (strName.contains(",")) {
                        String stateName = strName.split(",")[0];
                        String stateSymbol = strName.split(",")[1];
                        stateSymbol = stateSymbol.trim();
                        System.out.println("s: " + s + " StateSymbol: " + stateSymbol);
                        if (s.equals(stateSymbol)) {
                            tabela.get(stateName).replace(s, estado.getStateName());
                        }
                    }
                    estado = estado.getNextState();
                }
            }else{
                Set<String> follow = mapaFollow.get(s);
                System.out.println("NT: " + s);
                System.out.println("NT: " + s + " follow: " + follow.toString());
                
            }
        }
    }

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
    
     public void printMapaAux() {
        System.out.println("\n=== MAPA AUX===");
        for (Map.Entry<String, Set<String>> e : mapaProducoesFollow.entrySet()) {
            System.out.println(e.getKey() + e.getValue().toString());
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

    public void printFirst() {
        System.out.println("\n=== FIRST ===");
        mapaFirst.entrySet().forEach((e) -> {
            System.out.println(e.getKey() + e.getValue().toString());
        });
    }

    public void printFollow() {
        System.out.println("\n=== FOLLOW ===");
        mapaFollow.entrySet().forEach((e) -> {
            System.out.println(e.getKey() + e.getValue().toString());
        });
    }

    public void printTabela() {
        for (Map.Entry<String, Map<String, String>> m : tabela.entrySet()) {
            m.getValue().entrySet().forEach((e) -> {
                System.out.println("Linha: " + m.getKey() + " Coluna: " + e.getKey() + " Valor: " + e.getValue());
            });
        }
    }
    //</editor-fold>
}
