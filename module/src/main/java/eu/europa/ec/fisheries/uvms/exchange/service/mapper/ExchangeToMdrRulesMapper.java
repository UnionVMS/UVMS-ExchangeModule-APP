package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXMDRSyncMessageExchangeRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetMdrPluginRequest;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

/**
 * Created by kovian on 07/09/2016.
 */
public class ExchangeToMdrRulesMapper {

    public static String mapExchangeToMdrPluginRequest(TextMessage requestMessage) {
        SetFLUXMDRSyncMessageExchangeRequest exchangeRequest = JAXBMarshaller
                .unmarshallTextMessage(requestMessage, SetFLUXMDRSyncMessageExchangeRequest.class);
        SetMdrPluginRequest pluginRequest = new SetMdrPluginRequest();
        pluginRequest.setMethod(ExchangePluginMethod.SET_MDR_REQUEST);
        pluginRequest.setRequest(exchangeRequest.getRequest());
        pluginRequest.setFr(exchangeRequest.getFr());
        return JAXBMarshaller.marshallJaxBObjectToString(pluginRequest);
    }
}
