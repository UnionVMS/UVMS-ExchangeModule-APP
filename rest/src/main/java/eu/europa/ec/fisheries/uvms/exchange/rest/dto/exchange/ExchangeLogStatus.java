package eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange;

import java.util.List;

public class ExchangeLogStatus {

	private String typeRefGuid;
	private List<StatusLog> statusList;
	
	public String getTypeRefGuid() {
		return typeRefGuid;
	}

	public void setTypeRefGuid(String typeRefGuid) {
		this.typeRefGuid = typeRefGuid;
	}

	public List<StatusLog> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<StatusLog> statusList) {
		this.statusList = statusList;
	}
}
