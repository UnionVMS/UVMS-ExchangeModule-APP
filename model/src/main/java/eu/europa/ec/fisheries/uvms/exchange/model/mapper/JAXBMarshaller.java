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

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class JAXBMarshaller {

    private static Map<String, JAXBContext> contexts = new HashMap<>();

    /**
     * Marshalls a JAXB Object to a XML String representation
     *
     * @param <T>
     * @param data
     * @return
     * @throws
     * eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException
     */
    public static <T> String marshallJaxBObjectToString(T data) throws ExchangeModelMarshallException {
        try {
            JAXBContext jaxbContext = contexts.get(data.getClass().getName());
            if (jaxbContext == null) {
                jaxbContext = JAXBContext.newInstance(data.getClass());
                contexts.put(data.getClass().getName(), jaxbContext);
            }
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            marshaller.marshal(data, sw);
            String marshalled = sw.toString();
            return marshalled;
        } catch (JAXBException ex) {
            throw new ExchangeModelMarshallException("[ Error when marshalling Object to String ]");
        }
    }

    /**
     * Unmarshalls A textMessage to the desired Object. The object must be the
     * root object of the unmarchalled message!
     *
     * @param <R>
     * @param textMessage
     * @param clazz pperException
     * @return
     * @throws
     * eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException
     */
    public static <R> R unmarshallTextMessage(TextMessage textMessage, Class clazz) throws ExchangeModelMarshallException {
        try {
            JAXBContext jc = contexts.get(clazz.getName());
            if (jc == null) {
                jc = JAXBContext.newInstance(clazz);
                contexts.put(clazz.getName(), jc);
            }
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StringReader sr = new StringReader(textMessage.getText());
            StreamSource source = new StreamSource(sr);
            R object = (R) unmarshaller.unmarshal(source);
            return object;
        } catch (JMSException | JAXBException ex) {
            throw new ExchangeModelMarshallException("[Error when unmarshalling response in ResponseMapper ]");
        }
    }


    /**
     * Unmarshalls a string to the desired class.
     *
     * @param text
     * @param clazz class to marshall to. The class must be the
     * root object of the unmarchalled message!
     * @return
     * @throws ExchangeModelMarshallException
     */
    public static <R> R unmarshallString(String text, Class clazz) throws ExchangeModelMarshallException {
        try {
            JAXBContext jc = contexts.get(clazz.getName());
            if (jc == null) {
                jc = JAXBContext.newInstance(clazz);
                contexts.put(clazz.getName(), jc);
            }
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StringReader sr = new StringReader(text);
            StreamSource source = new StreamSource(sr);
            R object = (R) unmarshaller.unmarshal(source);
            return object;
        } catch (JAXBException ex) {
            throw new ExchangeModelMarshallException("Error when unmarshalling text", ex);
        }
    }
}