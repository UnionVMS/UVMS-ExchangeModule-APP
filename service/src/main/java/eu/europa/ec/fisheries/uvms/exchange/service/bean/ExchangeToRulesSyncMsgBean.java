/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogWithValidationResults;
import eu.europa.ec.fisheries.schema.exchange.v1.LogValidationResult;
import eu.europa.ec.fisheries.schema.exchange.v1.RuleValidationLevel;
import eu.europa.ec.fisheries.schema.exchange.v1.RuleValidationStatus;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.schema.rules.rule.v1.ValidationMessageType;
import eu.europa.ec.fisheries.schema.rules.rule.v1.ValidationMessageTypeResponse;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by kovian on 14/12/2017.
 */
@Singleton
@Slf4j
public class ExchangeToRulesSyncMsgBean {

    @EJB
    private ExchangeConsumer exchangeConsumerBean;

    @EJB
    private ExchangeMessageProducer exchangeProducerBean;


    public ExchangeLogWithValidationResults getValidationFromRules(String guid, TypeRefType type) {
        if (StringUtils.isEmpty(guid)) {
            return new ExchangeLogWithValidationResults();
        }
        ExchangeLogWithValidationResults resp = new ExchangeLogWithValidationResults();
        try {
            String getValidationsByGuidRequest = RulesModuleRequestMapper.createGetValidationsByGuidRequest(guid, type == null ? null : type.name());
            String correlationId = exchangeProducerBean.sendRulesMessage(getValidationsByGuidRequest);
            TextMessage validationRespMsg = exchangeConsumerBean.getMessage(correlationId, TextMessage.class);
            ValidationMessageTypeResponse validTypeRespFromRules = JAXBMarshaller.unmarshallTextMessage(validationRespMsg, ValidationMessageTypeResponse.class);
            List<ValidationMessageType> validationsListResponse = validTypeRespFromRules.getValidationsListResponse();
            if(CollectionUtils.isNotEmpty(validationsListResponse)){
                for(ValidationMessageType validMsgFromRules : validationsListResponse){
                    resp.getValidationList().add(mapToLogValidationResult(validMsgFromRules));
                }
            }
        } catch (ConfigMessageException | ExchangeMessageException | RulesModelMarshallException | ExchangeModelMarshallException e) {
            log.error("Error while trying to get Validation Results for RawMessage GUID from Rules!", e);
        }
        return resp;
    }

    private LogValidationResult mapToLogValidationResult(ValidationMessageType validMsgFromRules) throws NullPointerException {
        LogValidationResult logResult = new LogValidationResult();
        logResult.setId(validMsgFromRules.getBrId());
        try {
            logResult.setLevel(RuleValidationLevel.fromValue(validMsgFromRules.getLevel()));
        } catch (IllegalArgumentException ex){
            log.error("[ERROR] The validation level "+validMsgFromRules.getLevel()+" doesn't exist in RuleValidationLevel class..");
        }
        logResult.setStatus(EnumUtils.getEnum(RuleValidationStatus.class, validMsgFromRules.getErrorType().toString()));
        logResult.setXpaths(StringUtils.join(validMsgFromRules.getXpaths(), ','));
        return logResult;
    }

}
