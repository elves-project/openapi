package cn.gyyx.openapi.webservice;

import cn.gyyx.elves.util.ExceptionUtil;
import cn.gyyx.elves.util.JsonFilter;
import cn.gyyx.elves.util.mq.MessageProducer;
import cn.gyyx.openapi.enums.Errorcode;
import cn.gyyx.openapi.util.ValidateUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v2/cron")
public class CronWebService{

	private static final Logger LOG = Logger.getLogger(CronWebService.class);

	@Autowired
	private MessageProducer messageProducer;

	/**
	 * 添加计划任务
	 */
	@RequestMapping(value = "/add", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String addCron(String ip, String mode, String app, String func, String param, String rule, String timeout, String proxy){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("flag"  , "false");
		if(StringUtils.isBlank(app)){
			result.put("error", Errorcode.ERR403_3.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(StringUtils.isBlank(func)){
			result.put("error", Errorcode.ERR403_7.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(!ValidateUtil.validateIpAddress(ip)){
			result.put("error", Errorcode.ERR403_1.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(StringUtils.isNotBlank(param) && !ValidateUtil.validateJson(param)){
			result.put("error", Errorcode.ERR403_1.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(StringUtils.isNotBlank(timeout) && !ValidateUtil.validateNumber(timeout)){
			result.put("error", Errorcode.ERR403_8.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(!ValidateUtil.validataQuartzRule(rule)){
			result.put("error", Errorcode.ERR403_5.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		List<String> modelist = new ArrayList<String>();
		modelist.add("p");
		modelist.add("np");
		if (StringUtils.isNotBlank(mode) &&  !modelist.contains(mode.toLowerCase()) ){
			result.put("error", Errorcode.ERR403_4.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("ip", ip);
			mqrequest.put("mode", StringUtils.isBlank(mode)?"np":mode);
			mqrequest.put("app", app);
			mqrequest.put("func", func);
			mqrequest.put("param", param);
			mqrequest.put("timeout", StringUtils.isBlank(timeout)?90:Integer.parseInt(timeout));
			mqrequest.put("proxy", proxy);
			mqrequest.put("rule", rule);
            Map<String,Object> back = messageProducer.call("openapi.cron","createCron", mqrequest, 5000);

            if(null!=back&&null!=back.get("flag")&&"true".equals(back.get("flag").toString())){
                Map<String,Object>  data = new HashMap<String, Object>();
                data.put("id",back.get("id"));

                result.put("flag","true");
                result.put("error","");
                result.put("result",data);
            }else{
                result.put("error",back.get("error"));
            }
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
		return JSON.toJSONString(result, JsonFilter.filter);
	}

	/**
	 * 启动计划任务
	 */
	@RequestMapping(value = "/start", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String startCron(String cron_id){
		LOG.info("request /api/v2/cron/start start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		if(cron_id.length() != 16){
			result.put("error", Errorcode.ERR403_9.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("id",cron_id );
			result = messageProducer.call("openapi.cron","startCron", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
     	return JSON.toJSONString(result,JsonFilter.filter);
	}

	/**
	 * 停止计划任务
	 */
	@RequestMapping(value = "/stop", method =  {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String stopCron(String cron_id){
		LOG.info("request /api/v2/cron/stop start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		if(StringUtils.isBlank(cron_id)||cron_id.length() != 16){
			result.put("error", Errorcode.ERR403_9.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("id",cron_id );
			result = messageProducer.call("openapi.cron","stopCron", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
     	return JSON.toJSONString(result,JsonFilter.filter);
	}
	
	/**
	 * 删除计划任务
	 */
	@RequestMapping(value = "/delete", method =  {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String deleteCron(String cron_id){
		LOG.info("request /api/v2/cron/delete start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		if(StringUtils.isBlank(cron_id)||cron_id.length() != 16){
			result.put("error", Errorcode.ERR403_9.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("id",cron_id );
			result = messageProducer.call("openapi.cron","deleteCron", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
     	return JSON.toJSONString(result,JsonFilter.filter);
	}
	
	/**
	 * 计划任务详情
	 */
	@RequestMapping(value = "/detail", method =  {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String detailCron(String cron_id){
		LOG.info("request /api/v2/cron/delete start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		if(StringUtils.isBlank(cron_id)||cron_id.length() != 16){
			result.put("error", Errorcode.ERR403_9.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("id",cron_id );
			result = messageProducer.call("openapi.cron","cronDetail", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
     	return JSON.toJSONString(result,JsonFilter.filter);
	}
	
}
