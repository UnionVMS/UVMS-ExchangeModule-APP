/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.service.util;

/**
 *
 * @author osdjup
 */
public class StringUtil {
    
    
    public static String compressServiceClassName(String serviceClassName) {
        if (serviceClassName != null && serviceClassName.length() > 36) {
            String[] packages = serviceClassName.split("\\.");
            if (packages.length > 2) {
                StringBuilder compressed = new StringBuilder();
                for (int i = 0; i < packages.length - 2; i++) {
                    compressed.append(packages[i].charAt(0));
                    compressed.append(".");
                }

                compressed.append(packages[packages.length - 2]);
                compressed.append(".");
                compressed.append(packages[packages.length - 1]);
                return compressed.toString();
            } else {
                return serviceClassName.substring(serviceClassName.length() - 36, serviceClassName.length());
            }
        } else {
            return serviceClassName;
        }
    }
}
