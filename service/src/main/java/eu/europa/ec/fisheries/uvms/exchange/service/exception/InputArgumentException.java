package eu.europa.ec.fisheries.uvms.exchange.service.exception;

public class InputArgumentException extends ExchangeServiceException {
    private static final long serialVersionUID = 1L;

    public InputArgumentException() {
        super();
    }

    public InputArgumentException(String message) {
        super(message);
    }

}
