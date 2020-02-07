/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeLogModelBean;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by kovian on 11/12/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExchangeLogModelTest {

    @InjectMocks
    private ExchangeLogModelBean exchangeLogModel;

    @Mock
    private ExchangeLogDaoBean logDao;

    private List<ExchangeLog> logs;

    private List<ExchangeLog> refLogs;


    @Before
    public void prepare() {
        logs = MockData.getLogEntities();
        refLogs = MockData.getLogEntities();
        refLogs.get(0).setTypeRefGuid(logs.get(0).getId());
        refLogs.get(0).setTypeRefGuid(logs.get(1).getId());
    }

    @Test
    public void testGetExchangeLogByRefUUIDAndType() {
        Mockito.when(logDao.getExchangeLogByTypesRefAndGuid(Mockito.any(), Mockito.anyList()))
                .thenReturn(logs);
        Set<ExchangeLogType> exchangeLogByRefUUIDAndType = exchangeLogModel.getExchangeLogByRefUUIDAndType(UUID.randomUUID(), TypeRefType.FA_QUERY);
        assertFalse(exchangeLogByRefUUIDAndType.isEmpty());
    }

    @Test
    public void testDataEnrichment() {
        Mockito.when(logDao.getExchangeLogListSearchCount(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(100L);
        Mockito.when(logDao.getExchangeLogListPaginated(Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.anyString(), Mockito.anyList()))
                .thenReturn(logs);
        Mockito.when(logDao.getExchangeLogByRangeOfRefGuids(Mockito.anyList()))
                .thenReturn(refLogs);

        ExchangeListQuery query = new ExchangeListQuery();
        ExchangeListPagination pagin = new ExchangeListPagination();
        ExchangeListCriteria exchCrit = new ExchangeListCriteria();
        query.setPagination(pagin);
        pagin.setListSize(10);
        query.setExchangeSearchCriteria(exchCrit);

        ListResponseDto exchangeLogListByQuery = exchangeLogModel.getExchangeLogListByQuery(query);

        List<ExchangeLogType> exchangeLogList = exchangeLogListByQuery.getExchangeLogList();
        ExchangeLogType exchangeLogType1 = exchangeLogList.get(0);
        ExchangeLogType exchangeLogType2 = exchangeLogList.get(1);

        assertTrue(exchangeLogType1.getRelatedLogData().isEmpty());
        assertFalse(exchangeLogType2.getRelatedLogData().isEmpty());

        System.out.println("Done");
    }
}
