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
package eu.europa.ec.fisheries.uvms.exchange;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.uvms.exchange.bean.ServiceRegistryModelBean;
import eu.europa.ec.fisheries.uvms.exchange.dao.ServiceRegistryDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceSetting;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoMappingException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DomainModelBeanTest {

    @Mock
    ServiceRegistryDao dao;

    @InjectMocks
    private ServiceRegistryModelBean model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Ignore
    @Test
    public void testCreateModel() throws ExchangeModelException, ExchangeDaoException, ExchangeDaoMappingException {
        UUID id = UUID.randomUUID();

        ServiceType serviceType = MockData.getModel((int) id.getLeastSignificantBits());
        CapabilityListType capabilityListType = MockData.getCapabilityList();
        SettingListType settingListType = MockData.getSettingList();
        
        Service service = new Service();
        service.setId(id);
        service.setActive(false);
        List<ServiceCapability> serviceCapabilityList = new ArrayList<>();
		service.setServiceCapabilityList(serviceCapabilityList);
        List<ServiceSetting> serviceSettingList = new ArrayList<>();
		service.setServiceSettingList(serviceSettingList);
        
        when(dao.getServiceByServiceClassName(any(String.class))).thenReturn(null);
        //when(dao.updateService(any(Service.class))).thenReturn(service);

        ServiceResponseType result = model.registerService(serviceType, capabilityListType, settingListType, "TEST");
        
        //assertEquals(id.toString(), result.getId());
    }

}