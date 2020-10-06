package eu.europa.ec.fisheries.uvms.exchange.rest.dto;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusHistoryType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogRefType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestExchangeLogStatusType implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String guid;
    protected LogRefType typeRef;
    protected List<ExchangeLogStatusHistoryType> history;
    protected String identifier;
    protected String businessModuleExceptionMessage;
    protected String refMessage;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String value) {
        this.guid = value;
    }

    public LogRefType getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(LogRefType value) {
        this.typeRef = value;
    }

    public List<ExchangeLogStatusHistoryType> getHistory() {
        if (history == null) {
            history = new ArrayList<ExchangeLogStatusHistoryType>();
        }
        return this.history;
    }

    public void setHistory(List<ExchangeLogStatusHistoryType> history) {
        this.history = history;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String value) {
        this.identifier = value;
    }

    public String getBusinessModuleExceptionMessage() {
        return businessModuleExceptionMessage;
    }

    public void setBusinessModuleExceptionMessage(String value) {
        this.businessModuleExceptionMessage = value;
    }

    public String getRefMessage() {
        return refMessage;
    }

    public void setRefMessage(String refMessage) {
        this.refMessage = refMessage;
    }
}
