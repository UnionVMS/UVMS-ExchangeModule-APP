package eu.europa.ec.fisheries.uvms.exchange.model.contract.search;

import eu.europa.ec.fisheries.schema.exchange.v1.SearchField;

import javax.json.bind.annotation.JsonbTransient;
import java.util.ArrayList;
import java.util.List;

public class ExchangeSearchBranch implements ExchangeSearchInterface {

    boolean logicalAnd;

    List<ExchangeSearchInterface> fields = new ArrayList<>();

    @Override
    @JsonbTransient
    public boolean isLeaf() {
        return false;
    }

    public ExchangeSearchBranch() {
    }

    public void addNewSearchLeaf(SearchField searchField, String value){
        ExchangeSearchLeaf leaf = new ExchangeSearchLeaf(searchField, value);
        fields.add(leaf);
    }


    public ExchangeSearchBranch(boolean logicalAnd) {
        this.logicalAnd = logicalAnd;
    }

    public boolean isLogicalAnd() {
        return logicalAnd;
    }

    public void setLogicalAnd(boolean logicalAnd) {
        this.logicalAnd = logicalAnd;
    }

    public List<ExchangeSearchInterface> getFields() {
        return fields;
    }

    public void setFields(List<ExchangeSearchInterface> fields) {
        this.fields = fields;
    }
}
