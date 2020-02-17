package eu.europa.ec.fisheries.uvms.exchange.rest;

import eu.europa.ec.fisheries.schema.exchange.v1.LogValidationResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestExchangeLogWithValidationResults implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String msg;
    protected List<LogValidationResult> validationList;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String value) {
        this.msg = value;
    }

    public List<LogValidationResult> getValidationList() {
        if (validationList == null) {
            validationList = new ArrayList<LogValidationResult>();
        }
        return this.validationList;
    }

    public void setValidationList(List<LogValidationResult> validationList) {
        this.validationList = validationList;
    }
}
