package cn.gyyx.openapi.webservice;

import cn.gyyx.elves.util.ExceptionUtil;
import cn.gyyx.elves.util.mq.MessageProducer;
import cn.gyyx.openapi.enums.Errorcode;
import cn.gyyx.openapi.filter.JsonFilter;
import cn.gyyx.openapi.util.ValidateUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/v2/rt")
public class RtWebService{

	private static final Logger LOG = Logger.getLogger(RtWebService.class);

	@Autowired
	private MessageProducer messageProducer;

	/**
	 *  执行及时任务
	 */
	@RequestMapping(value = "/exec", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String execRt(HttpServletRequest request, String ip, String app, String func,String param, String timeout, String proxy, String auth_id) {
		LOG.info("request /api/v2/rt/exec start");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("flag"  , "false");
		if(StringUtils.isBlank(app)) {
			result.put("error", Errorcode.ERR403_3.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(StringUtils.isBlank(func)){
			result.put("error", Errorcode.ERR403_7.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(!ValidateUtil.validateIpAddress(ip)) {
			result.put("error", Errorcode.ERR403_1.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(StringUtils.isNotBlank(param) && !ValidateUtil.validateJson(param)){
			result.put("error", Errorcode.ERR403_2.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(StringUtils.isNotBlank(timeout) && !ValidateUtil.validateNumber(timeout)){
			result.put("error", Errorcode.ERR403_8.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("ip",    ip);
			mqrequest.put("app",   app);
			mqrequest.put("func",  func);
			mqrequest.put("param", param);
			mqrequest.put("timeout", timeout=="" || timeout==null?"90":timeout);
			mqrequest.put("proxy", proxy);
			result = messageProducer.call("openapi.scheduler","syncJob", mqrequest, ( Integer.parseInt(mqrequest.get("timeout").toString())+5)*1000);
        }catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
		return JSON.toJSONString(result, JsonFilter.filter);
	}
	
}
