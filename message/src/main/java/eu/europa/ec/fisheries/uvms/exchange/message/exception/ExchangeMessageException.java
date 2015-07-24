package eu.europa.ec.fisheries.uvms.exchange.message.exception;

public class ExchangeMessageException extends Exception {
    private static final long serialVersionUID = 1L;

    public ExchangeMessageException() {
    }

    public ExchangeMessageException(String message) {
        super(message);
    }

    public ExchangeMessageException(String message, Throwable cause) {
        super(message, cause);
    }

}
