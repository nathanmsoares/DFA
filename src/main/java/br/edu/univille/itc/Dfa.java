package br.edu.univille.itc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
Falta Adicionar a matriz nos status, por isos da erro
 */
public class Dfa {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private Map<String, State> status = new HashMap<>();
    private State inicialStatus;
    private State atualStatus;
    private JsonNode dfaData;
    private JsonNode sequences;

    private Map<JsonNode, String> results = new HashMap<>();

    public Dfa() {
        logger.info("initializing DFA");
        getJsonInfos();
        generateStatus();
        setInicialStatus();
        setFinalStatus();
        setMatrixToItsStatus();
        execute();
    }

    private void getNext(String nextState){
        logger.info(String.format("Getting next transcation -> %s", nextState));
        this.atualStatus = this.atualStatus.getStatusConnected().get(nextState);
    }

    private void getJsonInfos(){
        logger.info("Getting Info from DFA Json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            dfaData = objectMapper.readTree(new File("src/main/resources/dfa.json"));
            sequences = objectMapper.readTree(new File("src/main/resources/sequences.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateStatus(){
        logger.info("Generating Status");
        dfaData.get("status").forEach(name -> status.put(name.asText(),
                new State(name.asText())
                )
        );
    }

    private void setInicialStatus(){
        logger.info("Setting InicialStatus");
        dfaData.get("inicialStatus").forEach(inicial -> {
            this.inicialStatus = status.get(inicial.asText());
            this.inicialStatus.setInicial(true);
            this.atualStatus = this.inicialStatus;
        });
    }


    private void setFinalStatus(){
        logger.info("Setting Final Status");
        dfaData.get("FinalStatus").forEach(finalStatus -> {
            status.get(finalStatus.asText()).setAccept(true);
        });
    }

    private JsonNode getAlphabet(){
        logger.info("getting the alphabet");
        return dfaData.get("alphabet");
    }

    private JsonNode getMatrixByStatus(String name){
        logger.info("getting Matrix");
        return dfaData.get("matrix").get(name);
    }

    private void setMatrixToItsStatus(){
        getStatus().values().forEach(value -> {
            JsonNode matrix = getMatrixByStatus(value.getStateName());
            getAlphabet().forEach(alphabet -> {
                value.getStatusConnected().put(
                        alphabet.asText(),
                        getStatus().get(matrix.get(alphabet.asText()).asText())
                );
            });
        });
    }

    public void execute(){
        logger.info("Running Sequence");
        for (JsonNode sequence: sequences.get("sequences")) {
            sequence.forEach(transaction -> this.getNext(transaction.asText()));
            if (this.atualStatus.isAccept()){
                results.put(sequence, "Approved");
            } else {
                results.put(sequence, "Failed");
            }
            this.atualStatus = this.inicialStatus;
        }
        logger.info(String.format("Results -> %s", results));
        saveResults();
    }

    public void saveResults(){
        logger.info("Saving results into results.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File("src/main/resources/results.json"), this.results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, State> getStatus() {
        return status;
    }
}
