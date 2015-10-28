package eu.europa.ec.fisheries.uvms.exchange.service.exception;

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;

public class ExchangeLogException extends ExchangeException {
	private static final long serialVersionUID = 1L;

	public ExchangeLogException(String message) {
		super(message);
	}

}
