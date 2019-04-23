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
package eu.europa.ec.fisheries.uvms.exchange.service.domain;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.ServiceSetting;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.exception.ExchangeDaoMappingException;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.mapper.ServiceMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MapperTest {

    @InjectMocks
    private ServiceMapper mapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // private void mockDaoToEntity() throws ExchangeDaoException {
    // when(enumDao.getMyEntityEnum(any(MyEntityEnumSource.class))).thenReturn(MyEntityEnum.VALUE_1);
    // }

    @Test
    public void testEntityToModel() throws ExchangeDaoException, ExchangeDaoMappingException {
        Integer id = 1;
        Service entity = MockData.getEntity(id);
        List<ServiceCapability> capabilityList = new ArrayList<>();
		entity.setServiceCapabilityList(capabilityList);
        List<ServiceSetting> settingList = new ArrayList<>();
		entity.setServiceSettingList(settingList);
        // mockDaoToEntity();
        ServiceType result = mapper.toServiceModel(entity);

        assertSame(entity.getName(), result.getName());
        assertSame(entity.getServiceClassName(), result.getServiceClassName());
    }

    @Test
    public void testModelToEntity() throws ExchangeDaoException, ExchangeDaoMappingException {
        Integer id = 1;
        ServiceType model = MockData.getModel(id);
        CapabilityListType capabilityListType = MockData.getCapabilityList();
        SettingListType settingListType = MockData.getSettingList();
        // mockDaoToEntity();

        Service result = mapper.toServiceEntity(model, capabilityListType, settingListType, "TEST");

        assertSame(model.getName(), result.getName());
        assertSame(model.getServiceClassName(), result.getServiceClassName());
    }

    @Test
    public void testEntityAndModelToEntity() throws ExchangeDaoException, ExchangeDaoMappingException {
        Integer id = 1;
        Service entity = MockData.getEntity(id);
        ServiceType service = MockData.getModel(1);
        CapabilityListType capabilityListType = MockData.getCapabilityList();
        SettingListType settingListType = MockData.getSettingList();
        // mockDaoToEntity();

        Service result = mapper.toServiceEntity(entity, service, capabilityListType, settingListType, "TEST");

        assertSame(entity.getName(), result.getName());
        assertSame(entity.getServiceClassName(), result.getServiceClassName());
    }

    @Test
    public void testEntityAndModelToModel() throws ExchangeDaoException, ExchangeDaoMappingException {
        Service entity = MockData.getEntity(1);
        List<ServiceCapability> capabilityList = new ArrayList<>();
		entity.setServiceCapabilityList(capabilityList);
        List<ServiceSetting> settingList = new ArrayList<>();
		entity.setServiceSettingList(settingList);
        // mockDaoToEntity();
        ServiceType result = mapper.toServiceModel(entity);

        assertSame(entity.getName(), result.getName());
        assertSame(entity.getServiceClassName(), result.getServiceClassName());
    }
    
    @Test
    public void testUpsert() {
    	String newValue = "NEW_VALUE";
    	
    	Service entity = MockData.getEntity(1);
    	entity.setServiceCapabilityList(MockData.getEntityCapabilities(entity));
    	entity.setServiceSettingList(MockData.getEntitySettings(entity));

    	SettingListType updateSettings = new SettingListType();
    	SettingType updateSetting = new SettingType();
    	updateSetting.setKey(MockData.SETTING_KEY);
		updateSetting.setValue(newValue);
    	updateSettings.getSetting().add(updateSetting);
    	List<ServiceSetting> list = mapper.mapSettingsList(entity, updateSettings, "TEST");
    	
    	assertFalse(list.isEmpty());
    	for(ServiceSetting setting : list) {
    		assertSame(setting.getValue(), newValue);
    	}
    	
    	SettingListType newSettings = new SettingListType();
    	SettingType newSetting = new SettingType();
    	newSetting.setKey("NEW.KEY");
    	newSetting.setValue("NEW.VALUE");
		newSettings.getSetting().add(newSetting);
		list = mapper.mapSettingsList(entity, newSettings, "TEST");

		assertTrue(list.size() == 1);
		
    }

}