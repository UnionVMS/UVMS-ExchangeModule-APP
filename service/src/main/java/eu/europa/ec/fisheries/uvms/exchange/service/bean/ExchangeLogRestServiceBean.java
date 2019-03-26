/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import java.util.Set;
import java.util.UUID;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogWithRawMsgAndType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.bean.ExchangeLogModelBean;
import eu.europa.ec.fisheries.uvms.exchange.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class ExchangeLogRestServiceBean {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangeLogRestServiceBean.class);

    @EJB
    private ExchangeLogModelBean exchangeLogModel;

    public ExchangeLogType getExchangeLogByGuid(UUID guid) {
            return exchangeLogModel.getExchangeLogByGuid(guid);
    }

    public GetLogListByQueryResponse getExchangeLogList(ExchangeListQuery query) throws ExchangeLogException {
        GetLogListByQueryResponse response = new GetLogListByQueryResponse();
        try {
            ListResponseDto exchangeLogList = exchangeLogModel.getExchangeLogListByQuery(query);
            response.setCurrentPage(exchangeLogList.getCurrentPage());
            response.setTotalNumberOfPages(exchangeLogList.getTotalNumberOfPages());
            response.getExchangeLog().addAll(exchangeLogList.getExchangeLogList());
            return response;
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't get exchange log list.");
        }
    }

    public LogWithRawMsgAndType getExchangeLogRawMessage(UUID guid) {
        return exchangeLogModel.getExchangeLogRawXmlByGuid(guid);
    }

    public Set<ExchangeLogType> getExchangeLogsByRefUUID(UUID guid, TypeRefType type) {
            return exchangeLogModel.getExchangeLogByRefUUIDAndType(guid, type);
    }
}
