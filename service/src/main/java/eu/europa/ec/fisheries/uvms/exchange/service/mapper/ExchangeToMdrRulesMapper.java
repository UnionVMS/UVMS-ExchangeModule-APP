package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXMDRSyncMessageExchangeRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetMdrPluginRequest;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

import javax.jms.TextMessage;

/**
 * Created by kovian on 07/09/2016.
 */
public class ExchangeToMdrRulesMapper {

    public static String mapExchangeToMdrPluginRequest(TextMessage requestMessage) throws ExchangeModelMarshallException {
        SetFLUXMDRSyncMessageExchangeRequest exchangeRequest = JAXBMarshaller.unmarshallTextMessage(requestMessage, SetFLUXMDRSyncMessageExchangeRequest.class);
        SetMdrPluginRequest pluginRequest = new SetMdrPluginRequest();
        pluginRequest.setMethod(ExchangePluginMethod.SET_MDR_REQUEST);
        pluginRequest.setRequest(exchangeRequest.getRequest());
        return JAXBMarshaller.marshallJaxBObjectToString(pluginRequest);
    }
}
