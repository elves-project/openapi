package cn.gyyx.openapi.enums;

public enum Errorcode {

	ERR401("[401]Unauthorized"),
	ERR401_1("[401.1]Unauthorized Missing Sign Param"),
	ERR401_2("[401.2]Unauthorized AuthId Is Illegal"),
	ERR401_3("[401.3]Unauthorized SignType Is Illegal"),
	ERR401_4("[401.4]Unauthorized Sign Length Is Illegal"),
	ERR401_5("[401.5]Unauthorized Sign Error"),
	ERR401_6("[401.6]Unauthorized AuthId Permission Denied"),
	ERR401_7("[401.7]Unauthorized Sign Timeout "),
	ERR401_8("[401.8]Unauthorized Timestamp Is Illegal"),
	
	ERR402("[402]Require Module Not Enabled"),
	ERR402_1("[402.1]Require Module (supervisor) Not Enabled"),
	ERR402_2("[402.2]Require Module (cron) Not Enabled"),
	ERR402_3("[402.3]Require Module (queue) Not Enabled"),
	
	ERR403("[403]Request Param Illegal"),
	ERR403_1("[403.1]Request Params (ip) Illegal"),
	ERR403_2("[403.2]Request Params (param) Illegal"),
	ERR403_3("[403.3]Request Params (app) Illegal"),
	ERR403_4("[403.4]Request Params (mode) Illegal"),
	ERR403_5("[403.5]Request Params (cron-rule) Illegal"),
	ERR403_6("[403.6]Request Params (queue_id) Illegal"),
	ERR403_7("[403.7]Request Params (func) Illegal"),
	ERR403_8("[403.8]Request Params (timeout) Illegal"),
	ERR403_9("[403.9]Request Params (cron_id) Illegal"),

	ERR500("[500]Internal ServerError ");
	
	private final String value;
	
	private Errorcode(String value){
		this.value=value;
	}
	
	public String getValue(){
		return value;
	}
	
}
