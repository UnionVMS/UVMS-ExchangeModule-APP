/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.exchange.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by kovian on 12/12/2017.
 */
public class XMLUtils {

    public static String preetyPrint(String obj) throws JsonProcessingException {
        if(StringUtils.isEmpty(obj)){
            return null;
        }
        return new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValueAsString(obj);
    }

    public static ObjectNode preetyPrintPojo(String object) throws JsonProcessingException {
        ObjectNode objNode = new ObjectMapper().createObjectNode();
        objNode.put("data", object);
        objNode.put("code", 200);
        return objNode;
    }



}
