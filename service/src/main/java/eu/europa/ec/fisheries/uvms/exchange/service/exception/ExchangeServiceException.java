package eu.europa.ec.fisheries.uvms.exchange.service.exception;

public class ExchangeServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public ExchangeServiceException() {
    }

    public ExchangeServiceException(String message) {
        super(message);
    }

    public ExchangeServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
