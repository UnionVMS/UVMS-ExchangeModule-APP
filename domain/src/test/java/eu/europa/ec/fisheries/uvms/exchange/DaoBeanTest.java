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

import eu.europa.ec.fisheries.uvms.exchange.dao.bean.ServiceRegistryDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DaoBeanTest {

    @Mock
    EntityManager em;

    @InjectMocks
    private ServiceRegistryDaoBean dao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateService() {
        Service carrier = new Service();

        dao.createEntity(carrier);
        verify(em).persist(carrier);
    }

    @Test
    public void testGetServiceById() {
        UUID id = UUID.randomUUID();
        Service entity = new Service();
        entity.setId(id);
        when(em.find(Service.class, id)).thenReturn(entity);

        Service result = dao.getEntityById(id.toString());

        verify(em).find(Service.class, id);
        assertSame(id, result.getId());
    }

    @Test
    public void testUpdateService() {
        UUID id = UUID.randomUUID();

        Service myEntity = new Service();
        myEntity.setId(id);

        Service result = new Service();
        result.setId(id);
        when(em.merge(myEntity)).thenReturn(result);

        Service resultEntity = dao.updateService(myEntity);

        verify(em).merge(myEntity);
        assertSame(id, resultEntity.getId());
    }

    @Test
    public void testDeleteService() {
        // em.remove(arg0);
    }

    @Test
    public void testGetServiceList() {
        TypedQuery<Service> query = mock(TypedQuery.class);
        when(em.createNamedQuery(Service.SERVICE_FIND_ALL, Service.class)).thenReturn(query);

        List<Service> dummyResult = new ArrayList<Service>();
        when(query.getResultList()).thenReturn(dummyResult);

        List<Service> result = dao.getServices();

        verify(em).createNamedQuery(Service.SERVICE_FIND_ALL, Service.class);
        verify(query).getResultList();
        assertSame(dummyResult, result);
    }

}