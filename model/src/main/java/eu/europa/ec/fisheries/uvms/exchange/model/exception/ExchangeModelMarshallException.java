package eu.europa.ec.fisheries.uvms.exchange.model.exception;

public class ExchangeModelMarshallException extends ExchangeModelMapperException {
    private static final long serialVersionUID = 1L;

    public ExchangeModelMarshallException(String message) {
        super(message);
    }

    public ExchangeModelMarshallException(String message, Throwable cause) {
        super(message, cause);
    }

}
