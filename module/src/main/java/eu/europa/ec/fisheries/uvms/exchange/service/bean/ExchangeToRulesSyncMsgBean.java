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
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.service.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.service.message.producer.bean.ExchangeRulesProducer;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
@LocalBean
public class ExchangeToRulesSyncMsgBean {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeToRulesSyncMsgBean.class);

    @EJB
    private ExchangeConsumer exchangeConsumerBean;

    @Inject
    private ExchangeRulesProducer rulesProducer;


    public ExchangeLogWithValidationResults getValidationFromRules(String guid, TypeRefType type, String dataFlow) {
        throw new NotImplementedException("Rules has been removed since it is not in use and is not being maintained");
    }

}
