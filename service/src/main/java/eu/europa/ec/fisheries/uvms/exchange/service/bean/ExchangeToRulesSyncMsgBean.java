/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.schema.rules.rule.v1.ValidationMessageType;
import eu.europa.ec.fisheries.schema.rules.rule.v1.ValidationMessageTypeResponse;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.bean.ExchangeRulesProducerBean;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.*;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Stateless
@LocalBean
public class ExchangeToRulesSyncMsgBean {

    @EJB
    private ExchangeConsumer exchangeConsumer;

    @EJB
    private ExchangeRulesProducerBean rulesProducer;


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ExchangeLogWithValidationResults getValidationFromRules(String guid, TypeRefType type) {
        if (StringUtils.isEmpty(guid)) {
            return new ExchangeLogWithValidationResults();
        }
        ExchangeLogWithValidationResults resp = new ExchangeLogWithValidationResults();
        try {
            String getValidationsByGuidRequest = RulesModuleRequestMapper.createGetValidationsByGuidRequest(guid, type == null ? null : type.name());
            String correlationId;
            try {
                Map<String, String> messageProperties = new HashMap<>();
                messageProperties.put("messageSelector", "ValidationResultsByRawGuid");
                correlationId = rulesProducer.sendModuleMessageWithProps(getValidationsByGuidRequest, exchangeConsumer.getDestination(), messageProperties);
            } catch (MessageException e) {
                log.error("[ Error when sending rules message. ] {}", e.getMessage());
                throw new ExchangeMessageException("Error when sending rules message.", e);
            }

            TextMessage validationRespMsg = exchangeConsumer.getMessage(correlationId, TextMessage.class);
            ValidationMessageTypeResponse validTypeRespFromRules = JAXBMarshaller.unmarshallTextMessage(validationRespMsg, ValidationMessageTypeResponse.class);
            List<ValidationMessageType> validationsListResponse = validTypeRespFromRules.getValidationsListResponse();
            if(CollectionUtils.isNotEmpty(validationsListResponse)){
                for(ValidationMessageType validMsgFromRules : validationsListResponse){
                    resp.getValidationList().add(mapToLogValidationResult(validMsgFromRules));
                }
            }
        } catch (ExchangeMessageException | MessageException | RulesModelMarshallException | ExchangeModelMarshallException e) {
            log.error("Error while trying to get Validation Results for RawMessage GUID from Rules!", e);
        }
        return resp;
    }

    private LogValidationResult mapToLogValidationResult(ValidationMessageType validMsgFromRules) {
        LogValidationResult logResult = new LogValidationResult();
        logResult.setId(validMsgFromRules.getBrId());
        try {
            logResult.setLevel(RuleValidationLevel.fromValue(validMsgFromRules.getLevel()));
        } catch (IllegalArgumentException ex){
            log.error("[ERROR] The validation level "+validMsgFromRules.getLevel()+" doesn't exist in RuleValidationLevel class..");
        }
        logResult.setStatus(EnumUtils.getEnum(RuleValidationStatus.class, validMsgFromRules.getErrorType().toString()));
        logResult.setXpaths(StringUtils.join(validMsgFromRules.getXpaths(), ','));
        logResult.setNote(validMsgFromRules.getNote());
        logResult.setEntity(validMsgFromRules.getEntity());
        logResult.setExpression(validMsgFromRules.getExpression());
        logResult.setMessage(validMsgFromRules.getMessage());
        return logResult;
    }

}
