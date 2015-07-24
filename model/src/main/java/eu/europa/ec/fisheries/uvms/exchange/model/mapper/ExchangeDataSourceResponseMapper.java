package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.wsdl.source.GetDataResponse;
import eu.europa.ec.fisheries.wsdl.types.ModuleObject;

public class ExchangeDataSourceResponseMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);

    /**
     * Validates a response
     *
     * @param response
     * @param correlationId
     * @throws ExchangeModelMapperException
     * @throws JMSException
     */
    private static void validateResponse(TextMessage response, String correlationId) throws ExchangeModelMapperException, JMSException {

        if (response == null) {
            LOG.error("[ Error when validating response in ResponseMapper: Response is Null ]");
            throw new ExchangeModelMapperException("[ Error when validating response in ResponseMapper: Response is Null ]");
        }

        if (response.getJMSCorrelationID() == null) {
            LOG.error("[ No corelationId in response.] Expected was: {} ", correlationId);
            throw new ExchangeModelMapperException("[ No corelationId in response (Null) ] . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            LOG.error("[ Wrong corelationId in response. Expected was {0} But actual was: {1} ]", correlationId, response.getJMSCorrelationID());
            throw new ExchangeModelMapperException("[ Wrong corelationId in response. ] Expected was: " + correlationId + "But actual was: " + response.getJMSCorrelationID());
        }

    }

    public static ModuleObject mapToModuleObjectFromResponse(TextMessage message) throws ExchangeModelMapperException {
        GetDataResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetDataResponse.class);
        return response.getVessel();
    }

}
