package eu.europa.ec.fisheries.uvms.exchange.service.event;

public class ServiceEvent {

	public ServiceEvent(String msg) {
		this.msg = msg;
	}

	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
