package eu.europa.ec.fisheries.uvms.exchange.rest.error;

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.RestResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.InputArgumentException;

public class ErrorHandler {

	public static ResponseDto getFault(Exception e) {
		if(e instanceof ExchangeException) {
			
			if(e instanceof ExchangeServiceException) {
				if(e instanceof InputArgumentException) {
					return new ResponseDto<String>(e.getMessage(), RestResponseCode.INPUT_ERROR);
				}
				return new ResponseDto<String>(e.getMessage(), RestResponseCode.SERVICE_ERROR);
			}
			
			return new ResponseDto<String>(e.getMessage(), RestResponseCode.EXCHANGE_ERROR);
		}
		return new ResponseDto<String>(e.getMessage(), RestResponseCode.UNDEFINED_ERROR);
	}
}
