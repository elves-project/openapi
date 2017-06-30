package cn.gyyx.openapi.webservice;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.gyyx.elves.util.ExceptionUtil;
import cn.gyyx.elves.util.mq.MessageProducer;
import cn.gyyx.openapi.enums.Errorcode;
import cn.gyyx.openapi.filter.JsonFilter;
import cn.gyyx.openapi.util.ValidateUtil;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping("/api/v2/queue")
public class QueueWebService{

	private static final Logger LOG = Logger.getLogger(QueueWebService.class);

	@Autowired
	private MessageProducer messageProducer;

	/**
	 *  创建队列
	 */
	@RequestMapping(value = "/create", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String create(String app){
		LOG.info("request /api/v2/queue/create start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		if(StringUtils.isBlank(app)){
			result.put("error", Errorcode.ERR403_3.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("app",app);
			result = messageProducer.call("openapi.queue","createQueue", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
     	return JSON.toJSONString(result,JsonFilter.filter);
	}
	
	/**
	 *  添加队列任务
	 */
	@RequestMapping(value = "/addtask", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String addTask(String queue_id, String ip, String mode, String app, String func,String param, String timeout, String proxy, String auth_id,String depend_task_id){
		LOG.info("request /api/v2/queue/addtask start");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("flag"  , "false");
		
		if(StringUtils.isBlank(queue_id)){
			result.put("error", Errorcode.ERR403_6.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(StringUtils.isBlank(func)){
			result.put("error", Errorcode.ERR403_7.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(!ValidateUtil.validateIpAddress(ip)) {
			result.put("error", Errorcode.ERR403_2.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(StringUtils.isNotBlank(param) && !ValidateUtil.validateJson(param)){
			result.put("error", Errorcode.ERR403_1.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		if(StringUtils.isNotBlank(timeout)&&!ValidateUtil.validateNumber(timeout)){
			result.put("error", Errorcode.ERR403_8.getValue());
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
			mqrequest.put("id", queue_id);
			mqrequest.put("ip", ip);
			mqrequest.put("func", func);
			mqrequest.put("mode",StringUtils.isBlank(mode)?"np":mode);
			mqrequest.put("param", param);
			mqrequest.put("timeout", StringUtils.isBlank(timeout)?90:Integer.parseInt(timeout));
			mqrequest.put("proxy", proxy);
			mqrequest.put("depend_task_id",depend_task_id);
			result = messageProducer.call("openapi.queue","addTask", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
		
		return JSON.toJSONString(result, JsonFilter.filter);
	}
	

	/**
	 *  提交队列
	 */
	@RequestMapping(value = "/commit", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String commit(String queue_id){
		LOG.info("request /api/v2/queue/commit start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		if(StringUtils.isBlank(queue_id)){
			result.put("error", Errorcode.ERR403_6.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("id",queue_id );
			result = messageProducer.call("openapi.queue","commitQueue", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
     	return JSON.toJSONString(result,JsonFilter.filter);
	}
	
	/**
	 *  停止队列
	 */
	@RequestMapping(value = "/stop", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String qstop(String queue_id){
		LOG.info("request /api/v2/queue/stop start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		if(StringUtils.isBlank(queue_id)){
			result.put("error", Errorcode.ERR403_6.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("id",queue_id );
			result = messageProducer.call("openapi.queue","stopQueue", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
     	return JSON.toJSONString(result,JsonFilter.filter);
	}
	
	/**
	 *  获取队列结果
	 */
	@RequestMapping(value = "/result", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String result(String queue_id){
		LOG.info("request /api/v2/queue/result start");
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("flag", "false");
		if(StringUtils.isBlank(queue_id)){
			result.put("error", Errorcode.ERR403_6.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  mqrequest = new HashMap<String, Object>();
			mqrequest.put("id",queue_id );
			result = messageProducer.call("openapi.queue","queueResult", mqrequest, 5000);
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
     	return JSON.toJSONString(result,JsonFilter.filter);
	}

	
	/**
	 * 添加快速单队列任务
	 */
	@RequestMapping(value = "/qksqueue", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String qksqueue(String ip, String mode, String app, String func,String param, String timeout, String proxy,String depend_task_id){
		LOG.info("request /api/v2/queue/addtask start");
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
			result.put("error", Errorcode.ERR403_2.getValue());
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
		List<String> modelist = new ArrayList<String>();
		modelist.add("p");
		modelist.add("np");
		if (StringUtils.isNotBlank(mode) &&  !modelist.contains(mode.toLowerCase()) ){
			result.put("error", Errorcode.ERR403_4.getValue());
			return JSON.toJSONString(result, JsonFilter.filter);
		}
		try{
			Map<String,Object>  cmqrequest = new HashMap<String, Object>();
			cmqrequest.put("app",app);
			result = messageProducer.call("openapi.queue","createQueue", cmqrequest, 5000);
			if("true".equals(result.get("flag"))){
				String queue_id = (String) result.get("id");
				try{
					Map<String,Object>  mqrequest = new HashMap<String, Object>();
					mqrequest.put("id", queue_id);
					mqrequest.put("depend_task_id", depend_task_id);
					mqrequest.put("ip", ip);
					mqrequest.put("mode", StringUtils.isBlank(mode)?"np":mode);
					mqrequest.put("app", app);
					mqrequest.put("func", func);
					mqrequest.put("param", param);
					mqrequest.put("timeout",StringUtils.isBlank(timeout)?90:Integer.parseInt(timeout));
					mqrequest.put("proxy", proxy);
					result = messageProducer.call("openapi.queue","addTask", mqrequest, 5000);
					if("true".equals(result.get("flag"))){
						try{
							Map<String,Object>  submqrequest = new HashMap<String, Object>();
							submqrequest.put("id",queue_id );
							submqrequest.put("task_id",result.get("id").toString() );
							result = messageProducer.call("openapi.queue","commitQueue", submqrequest, 5000);
							if("true".equals(result.get("flag"))){
								result.put("result", submqrequest);
							}else{
								result.put("error", result.get("error"));
							}
						}catch(Exception e){
							result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
						}
					}else{
						result.put("error", result.get("error"));
					}
				}catch(Exception e){
					result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
				}
			}else{
				result.put("error", result.get("error"));
			}
		}catch(Exception e){
			result.put("error", Errorcode.ERR500.getValue()+",openapi try/catch:"+ExceptionUtil.getStackTraceAsString(e));
		}
		return JSON.toJSONString(result, JsonFilter.filter);
	}
}
