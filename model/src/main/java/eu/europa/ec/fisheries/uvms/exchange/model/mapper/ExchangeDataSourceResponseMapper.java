/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import java.util.List;

import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.source.v1.*;
import eu.europa.ec.fisheries.schema.exchange.v1.PollStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;

public class ExchangeDataSourceResponseMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceResponseMapper.class);


    public static List<ServiceResponseType> mapToServiceTypeListFromResponse(TextMessage message) {
            GetServiceListResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListResponse.class);
            return response.getService();
    }

    public static List<ServiceResponseType> mapToServiceTypeListFromModuleResponse(TextMessage message) {
            eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse response = JAXBMarshaller.unmarshallTextMessage(message, eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse.class);
            return response.getService();
    }

    public static ServiceResponseType mapToRegisterServiceResponse(TextMessage message) {
            RegisterServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, RegisterServiceResponse.class);
            return response.getService();
    }

    public static ServiceResponseType mapToUnregisterServiceResponse(TextMessage message) {
            UnregisterServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, UnregisterServiceResponse.class);
            return response.getService();

    }

    public static ServiceResponseType mapToServiceTypeFromGetServiceResponse(TextMessage message) {
            GetServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceResponse.class);
            return response.getService();

    }

    public static ServiceResponseType mapToServiceTypeFromSetSettingsResponse(TextMessage message) {
            SetServiceSettingsResponse response = JAXBMarshaller.unmarshallTextMessage(message, SetServiceSettingsResponse.class);
            return response.getService();
    }

    public static ExchangeLogType mapToExchangeLogTypeFromCreateExchageLogResponse(TextMessage message) {
            CreateLogResponse response = JAXBMarshaller.unmarshallTextMessage(message, CreateLogResponse.class);
            return response.getExchangeLog();
    }

    public static GetLogListByQueryResponse mapToGetLogListByQueryResponse(TextMessage message) {
            GetLogListByQueryResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetLogListByQueryResponse.class);
            return response;
    }

    public static ServiceResponseType mapSetServiceResponse(TextMessage message) {
            SetServiceStatusResponse response = JAXBMarshaller.unmarshallTextMessage(message, SetServiceStatusResponse.class);
            return response.getService();
    }

    public static ExchangeLogType mapCreateExchangeLogResponse(TextMessage message) {
            CreateLogResponse response = JAXBMarshaller.unmarshallTextMessage(message, CreateLogResponse.class);
            return response.getExchangeLog();
    }

    public static ExchangeLogType mapUpdateLogStatusResponse(TextMessage message) {
            UpdateLogStatusResponse response = JAXBMarshaller.unmarshallTextMessage(message, UpdateLogStatusResponse.class);
            return response.getExchangeLog();
    }

    public static List<UnsentMessageType> mapGetSendingQueueResponse(TextMessage message) {
            GetUnsentMessageListResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetUnsentMessageListResponse.class);
            return response.getUnsentMessage();
    }

    public static List<ExchangeLogStatusType> mapGetLogStatusHistoryByQueryResponse(TextMessage message) {
            GetLogStatusHistoryByQueryResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetLogStatusHistoryByQueryResponse.class);
            return response.getStatusLog();
    }

    public static String createGetServiceSettingsResponse(List<SettingType> settings) {
        GetServiceSettingsResponse response = new GetServiceSettingsResponse();
        SettingListType listType = new SettingListType();
        listType.getSetting().addAll(settings);
        response.setSettings(listType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetServiceCapabilitiesResponse(List<CapabilityType> capabilities) {
        GetServiceCapabilitiesResponse response = new GetServiceCapabilitiesResponse();
        CapabilityListType listType = new CapabilityListType();
        listType.getCapability().addAll(capabilities);
        response.setCapabilities(listType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetServiceResponse(ServiceResponseType service) {
        GetServiceResponse response = new GetServiceResponse();
        response.setService(service);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetServiceListResponse(List<ServiceResponseType> services) {
        GetServiceListResponse response = new GetServiceListResponse();
        response.getService().addAll(services);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createCreateExchangeLogResponse(ExchangeLogType log) {
        CreateLogResponse response = new CreateLogResponse();
        response.setExchangeLog(log);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetExchangeListByQueryResponse(List<ExchangeLogType> logs, int currentPage, int totalNumberOfPages) {
        GetLogListByQueryResponse response = new GetLogListByQueryResponse();
        response.getExchangeLog().addAll(logs);
        response.setCurrentPage(currentPage);
        response.setTotalNumberOfPages(totalNumberOfPages);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createRegisterServiceResponse(ServiceResponseType service) {
        RegisterServiceResponse response = new RegisterServiceResponse();
        response.setService(service);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createUnregisterServiceResponse(ServiceResponseType service) {
        UnregisterServiceResponse response = new UnregisterServiceResponse();
        response.setService(service);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createSetServiceSettingsResponse(ServiceResponseType updatedService) {
        SetServiceSettingsResponse response = new SetServiceSettingsResponse();
        response.setService(updatedService);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createSetServiceStatusResponse(ServiceResponseType statusService) {
        SetServiceStatusResponse response = new SetServiceStatusResponse();
        response.setService(statusService);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createUpdateLogStatusResponse(ExchangeLogType exchangeLog) {
        UpdateLogStatusResponse response = new UpdateLogStatusResponse();
        response.setExchangeLog(exchangeLog);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetLogStatusHistoryResponse(ExchangeLogStatusType statusType) {
        GetLogStatusHistoryResponse response = new GetLogStatusHistoryResponse();
        response.setStatus(statusType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createCreateUnsentMessageResponse(String messageId) {
        CreateUnsentMessageResponse response = new CreateUnsentMessageResponse();
        response.setUnsentMessageId(messageId);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createRemoveUnsentMessageResponse(String messageId) {
        RemoveUnsentMessageResponse response = new RemoveUnsentMessageResponse();
        response.setUnsentMessageId(messageId);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetUnsentMessageListResponse(List<UnsentMessageType> unsentMessageList) {
        GetUnsentMessageListResponse response = new GetUnsentMessageListResponse();
        response.getUnsentMessage().addAll(unsentMessageList);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createResentMessageResponse(List<UnsentMessageType> messageList) {
        ResendMessageResponse response = new ResendMessageResponse();
        response.getResentMessage().addAll(messageList);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetLogStatusHistoryByQueryResponse(List<ExchangeLogStatusType> statusHistoryList) {
        GetLogStatusHistoryByQueryResponse response = new GetLogStatusHistoryByQueryResponse();
        response.getStatusLog().addAll(statusHistoryList);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createSingleExchangeLogResponse(ExchangeLogType exchangeLog) {
        SingleExchangeLogResponse response = new SingleExchangeLogResponse();
        response.setExchangeLog(exchangeLog);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static ExchangeLogType mapToExchangeLogTypeFromSingleExchageLogResponse(TextMessage message) {
            SingleExchangeLogResponse singleExchangeLogResponse = JAXBMarshaller.unmarshallTextMessage(message, SingleExchangeLogResponse.class);
            return singleExchangeLogResponse.getExchangeLog();
    }

    public static String mapCreateUnsentMessageResponse(TextMessage message) {
            CreateUnsentMessageResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(message, CreateUnsentMessageResponse.class);
            return unmarshalledResponse.getUnsentMessageId();
    }

    public static String mapRemoveUnsentMessageResponse(TextMessage message) {
            RemoveUnsentMessageResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(message, RemoveUnsentMessageResponse.class);
            return unmarshalledResponse.getUnsentMessageId();
    }

    public static List<UnsentMessageType> mapResendMessageResponse(TextMessage message) {
            ResendMessageResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(message, ResendMessageResponse.class);
            return unmarshalledResponse.getResentMessage();
    }

    public static ExchangeLogStatusType mapGetLogStatusHistoryResponse(TextMessage message) {
            GetLogStatusHistoryResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(message, GetLogStatusHistoryResponse.class);
            return unmarshalledResponse.getStatus();
    }

    public static PollStatus mapSetPollStatusResponse(TextMessage message) {
            SetPollStatusResponse response = JAXBMarshaller.unmarshallTextMessage(message, SetPollStatusResponse.class);
            return response.getExchangeLog();
    }

    public static String createSetPollStatusResponse(ExchangeLogType log) {
        SetPollStatusResponse response = new SetPollStatusResponse();
        PollStatus pollStatus = new PollStatus();
        pollStatus.setStatus(log.getStatus());
        pollStatus.setExchangeLogGuid(log.getGuid());
        pollStatus.setPollGuid(log.getTypeRef().getRefGuid());
        response.setExchangeLog(pollStatus);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
}