package cn.gyyx.openapi.webservice;

import cn.gyyx.elves.util.ExceptionUtil;
import cn.gyyx.elves.util.JsonFilter;
import cn.gyyx.elves.util.mq.MessageProducer;
import cn.gyyx.elves.util.mq.PropertyLoader;
import cn.gyyx.openapi.enums.Errorcode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v2/info")
public class InfoWebService{

	private static final Logger LOG=Logger.getLogger(InfoWebService.class);
	
	@Autowired
	private MessageProducer messageProducer;
	
	/**
	 * 获取APP信息
	 */
	@RequestMapping(value="/app",method= {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String getApps(String auth_id){
		LOG.info("request /api/v2/info/app start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("auth_id",auth_id);
			result = messageProducer.call("openapi.supervisor","appInfo", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
		return JSON.toJSONString(result, JsonFilter.filter);
	}

	
	/**
	 * 获取AGENT列表
	 */
	@RequestMapping(value="/agents",method= {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String getAgents(String auth_id){
		LOG.info("request /api/v2/info/agents start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("auth_id",auth_id);
			result = messageProducer.call("openapi.supervisor","agentList", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
     	return JSON.toJSONString(result,JsonFilter.filter);
	}

	/**
	 * 获取AGENT详情
	 */
	@RequestMapping(value="/agents/detail",method= {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String getAgentsDetail(String ip,String showcron){
		LOG.info("request /api/v2/info/agents/detail start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		if("true".equals(showcron)&&!"true".equalsIgnoreCase((PropertyLoader.CRON_ENABLED))){
			result.put("error", Errorcode.ERR402_2.getValue());
			return JSON.toJSONString(result,JsonFilter.filter);
		}
		if(StringUtils.isEmpty(ip)){
			result.put("error", Errorcode.ERR403_1.getValue());
			return JSON.toJSONString(result,JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("ip",ip );
			Map<String,Object> heartBeatBackResult = messageProducer.call("openapi.heartbeat","agentInfo", mqrequest, 5000);
			if(!"true".equals(heartBeatBackResult.get("flag"))){
				return JSON.toJSONString(result,JsonFilter.filter);
			}
			Map<String,Object> agentInfo = JSON.parseObject(heartBeatBackResult.get("result").toString(), new TypeReference<Map<String,Object>>(){});
			agentInfo.remove("apps");
			if("true".equals(showcron)){
				Map<String,Object>  mqcrequest = new HashMap<String, Object>();
				mqcrequest.put("ip",ip);
				Map<String,Object> cronBackResult=messageProducer.call("openapi.cron","cronList", mqcrequest, 5000);
				if(!"true".equals(cronBackResult.get("flag"))){
					return JSON.toJSONString(result,JsonFilter.filter);
				}
				List<String> cronList = JSON.parseObject(cronBackResult.get("result").toString(), new TypeReference<List<String>>(){});
				agentInfo.put("cron_list", cronList);
			}
			result.put("flag", "true");
			result.put("error", "");
			result.put("result", agentInfo);
		}catch(Exception e){
			result.put("flag", "false");
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
		return JSON.toJSONString(result,JsonFilter.filter);
	}
}
