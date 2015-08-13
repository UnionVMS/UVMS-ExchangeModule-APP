/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author jojoha
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ListQueryResponse {

    @XmlElement(required = true, type = Integer.class)
    private int currentPage;
    @XmlElement(required = true, type = Integer.class)
    private int totalNumberOfPages;
    @XmlElement(required = true, type = ExchangeLog.class)
    private List<ExchangeLog> logs;

    public ListQueryResponse() {
        logs = new ArrayList<>();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    public void setTotalNumberOfPages(int totalNumberOfPages) {
        this.totalNumberOfPages = totalNumberOfPages;
    }

    public List<ExchangeLog> getLogs() {
        return logs;
    }

}
