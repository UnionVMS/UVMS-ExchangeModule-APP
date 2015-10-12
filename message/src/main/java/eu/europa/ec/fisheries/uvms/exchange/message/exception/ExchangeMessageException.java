package eu.europa.ec.fisheries.uvms.exchange.message.exception;

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;

public class ExchangeMessageException extends ExchangeException {
    private static final long serialVersionUID = 1L;

    public ExchangeMessageException(String message) {
        super(message);
    }
}
