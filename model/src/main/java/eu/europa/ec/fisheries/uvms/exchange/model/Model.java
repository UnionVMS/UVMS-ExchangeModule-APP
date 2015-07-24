package eu.europa.ec.fisheries.uvms.exchange.model;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;

@Local
public interface Model {

    public void sendData(Object dto) throws ExchangeModelException;

    public Object getData() throws ExchangeModelException;

}
