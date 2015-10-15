package eu.europa.ec.fisheries.uvms.exchange.model.exception;

public class ExchangeValidationException extends ExchangeModelMapperException {
	private static final long serialVersionUID = 1L;
	
	public ExchangeValidationException(String message) {
		super(message);
	}
}
