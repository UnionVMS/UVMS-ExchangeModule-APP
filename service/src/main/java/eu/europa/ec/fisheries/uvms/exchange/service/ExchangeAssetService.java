package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.wsdl.vessel.types.Vessel;

public interface ExchangeAssetService {

    Vessel getAsset(String assetGuid) throws ExchangeServiceException;
}
