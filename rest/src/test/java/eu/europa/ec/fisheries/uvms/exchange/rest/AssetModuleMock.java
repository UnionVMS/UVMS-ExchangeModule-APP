package eu.europa.ec.fisheries.uvms.exchange.rest;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.config.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.config.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetHistoryId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.UUID;

@MessageDriven(mappedName = "jms/queue/UVMSAssetEvent", activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = "javax.jms.MessageListener"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "UVMSAssetEvent"),
       /* @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = MessageConstants.JMS_FUNCTION_PROPERTY + " NOT IN ( 'ASSET_INFORMATION' ) AND JMSCorrelationID IS NULL")*/})
public class AssetModuleMock implements MessageListener {

    @Inject
    ExchangeMessageProducer messageProducer;

    @Override
    public void onMessage(Message message) {
        try {
            Asset a = getBasicAsset();
            GetAssetModuleResponse response = new GetAssetModuleResponse();
            response.setAsset(a);
            String stringResponse = JAXBMarshaller.marshallJaxBObjectToString(response);
            messageProducer.sendModuleResponseMessage((TextMessage) message, stringResponse);


        } catch (ModelMarshallException | MessageException e) {
        }
    }

    private Asset getBasicAsset() {
        Asset asset = new Asset();
        asset.setIrcs("IRCS");
        AssetId assetId = new AssetId();
        assetId.setType(AssetIdType.GUID);
        assetId.setGuid(UUID.randomUUID().toString());
        asset.setAssetId(assetId);
        AssetHistoryId assetHistoryId = new AssetHistoryId();
        assetHistoryId.setEventId(UUID.randomUUID().toString());
        asset.setEventHistory(assetHistoryId);
        asset.setName("Test Asset");
        asset.setCountryCode("SWE");
        return asset;
    }
}