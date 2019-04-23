/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.exchange.service.domain;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.bean.ExchangeLogModelBean;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.dao.ExchangeLogDao;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.exchangelog.ExchangeLog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;

/**
 * Created by kovian on 11/12/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExchangeLogModelTest {

    @InjectMocks
    private ExchangeLogModelBean exchangeLogModel;

    @Mock
    private ExchangeLogDao logDao;

    private List<ExchangeLog> logs;

    private List<ExchangeLog> refLogs;


    @Before
    public void prepare(){
        logs = MockData.getLogEntities();
        refLogs = MockData.getLogEntities();
        refLogs.get(0).setTypeRefGuid(logs.get(0).getGuid());
        refLogs.get(0).setTypeRefGuid(logs.get(1).getGuid());
    }

    @Test
    public void testGetExchangeLogByRefUUIDAndType() throws ExchangeModelException {
        Mockito.when(logDao.getExchangeLogByTypesRefAndGuid(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(logs);
        Set<ExchangeLogType> exchangeLogByRefUUIDAndType = exchangeLogModel.getExchangeLogByRefUUIDAndType("refUUID", TypeRefType.FA_QUERY);
        assertFalse(exchangeLogByRefUUIDAndType.isEmpty());
    }

}
