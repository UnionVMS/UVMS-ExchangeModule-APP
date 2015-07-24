package eu.europa.ec.fisheries.uvms.exchange.model.exception;

public class ExchangeModelMapperException extends Exception {
    private static final long serialVersionUID = 1L;

    public ExchangeModelMapperException() {
    }

    public ExchangeModelMapperException(String message) {
        super(message);
    }

    public ExchangeModelMapperException(String message, Throwable cause) {
        super(message, cause);
    }

}
