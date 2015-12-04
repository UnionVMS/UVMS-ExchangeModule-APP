package eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class PluginType {

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private List<SendingLog> sendingLogList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SendingLog> getSendingLogList() {
        return sendingLogList;
    }

    public void setSendingLogList(List<SendingLog> sendingLogList) {
        this.sendingLogList = sendingLogList;
    }

}
