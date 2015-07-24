package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.MessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.wsdl.types.ModuleObject;

@Stateless
public class ExchangeServiceBean implements ExchangeService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeServiceBean.class);

    @EJB
    ParameterService parameterService;

    @EJB
    MessageConsumer consumer;

    @EJB
    MessageProducer producer;

    /**
     * {@inheritDoc}
     *
     * @param data
     * @throws ExchangeServiceException
     */
    @Override
    public ModuleObject create(ModuleObject data) throws ExchangeServiceException {
        LOG.info("Create invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapObjectToString(data);
            String messageId = producer.sendDataSourceMessage(request, DataSourceQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToModuleObjectFromResponse(response);
        } catch (ExchangeModelMapperException | ExchangeMessageException ex) {
            throw new ExchangeServiceException(ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws ExchangeServiceException
     */
    @Override
    public List<ModuleObject> getList() throws ExchangeServiceException {
        LOG.info("Get list invoked in service layer");
        throw new ExchangeServiceException("Get list logic not implemented in service layer");
    }

    /**
     * {@inheritDoc}
     *
     * @param id
     * @return
     * @throws ExchangeServiceException
     */
    @Override
    public ModuleObject getById(Long id) throws ExchangeServiceException {
        LOG.info("Get by id invoked in service layer");
        throw new ExchangeServiceException("Get by id not implemented in service layer");
    }

    /**
     * {@inheritDoc}
     *
     * @param data
     * @throws ExchangeServiceException
     */
    @Override
    public ModuleObject update(ModuleObject data) throws ExchangeServiceException {
        LOG.info("Update invoked in service layer");
        throw new ExchangeServiceException("Update not implemented in service layer");
    }

}
