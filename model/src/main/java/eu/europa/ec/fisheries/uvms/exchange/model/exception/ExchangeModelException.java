package eu.europa.ec.fisheries.uvms.exchange.model.exception;

public class ExchangeModelException extends Exception {
    private static final long serialVersionUID = 1L;

    public ExchangeModelException() {
    }

    public ExchangeModelException(String message) {
        super(message);
    }

    public ExchangeModelException(String message, Throwable cause) {
        super(message, cause);
    }

}
