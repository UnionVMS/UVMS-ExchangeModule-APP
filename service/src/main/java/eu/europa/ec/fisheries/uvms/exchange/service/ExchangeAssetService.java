package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

public interface ExchangeAssetService {

    Asset getAsset(String assetGuid) throws ExchangeServiceException;
}
