package cn.gyyx.openapi.filter;

import cn.gyyx.elves.util.ExceptionUtil;
import cn.gyyx.elves.util.MD5Utils;
import cn.gyyx.elves.util.mq.MessageProducer;
import cn.gyyx.elves.util.mq.PropertyLoader;
import cn.gyyx.openapi.enums.Errorcode;
import cn.gyyx.openapi.util.ValidateUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SecurityFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(SecurityFilter.class);
	private MessageProducer messageProducer;

	/**
	 * 初始化
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext servletContext = filterConfig.getServletContext();
		WebApplicationContext app = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		messageProducer = app.getBean(MessageProducer.class);
	}

	/**
	 * 过滤方法
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		Map<String, Object> map = new HashMap<String, Object>();
        try {
			String signType  = request.getParameter("sign_type");
			String sign      = request.getParameter("sign");
			String timestamp = request.getParameter("timestamp");
			String authId    = request.getParameter("auth_id");
			if (StringUtils.isBlank(timestamp) || StringUtils.isBlank(signType)|| StringUtils.isBlank(sign)|| StringUtils.isBlank(authId)) {
				//签名参数错误（timestamp/signType/sign/authId）
				map.put("error", Errorcode.ERR401_1.getValue());
			}
//			else if (authId.length() != 16) {
//				//AuthID长度错误
//				map.put("error", Errorcode.ERR401_2.getValue());
//			}
			else if (!"md5".equalsIgnoreCase(signType)) {
				//signType类型错误
				map.put("error", Errorcode.ERR401_3.getValue());
			} else if (sign.length() != 32) {
				//sign长度错误
				map.put("error", Errorcode.ERR401_4.getValue());
			} else if(!ValidateUtil.validateNumber(timestamp)){
				//timestemp长度错误
				map.put("error", Errorcode.ERR401_8.getValue());
			} else if (!(Long.parseLong(timestamp) > (System.currentTimeMillis() / 1000) - 3 * 60 && Long.parseLong(timestamp) < (System.currentTimeMillis() / 1000) + 3 * 60)) {
				// 只处理五分钟以内的请求， 当前时间-5分钟<=请求时间<=当前时间
				map.put("error", Errorcode.ERR401_7.getValue());
			}else{
				//基础验证成功
				Boolean GetKey = false;
				String authKey = null;
				if (PropertyLoader.AUTH_MODE.equalsIgnoreCase("supervisor")) {
					Map<String, Object> openapiSup = new HashMap<String, Object>();
					openapiSup.put("auth_id", authId);
					try {
						Map<String, Object> keyMap = messageProducer.call("openapi.supervisor", "getAuthKey", openapiSup,5000);
						if ("true".equals(keyMap.get("flag"))) {
							authKey = (String) keyMap.get("auth_key");
							GetKey = true;
						} else {
							map.put("error", keyMap.get("error"));
						}
					} catch (Exception e) {
						map.put("error",Errorcode.ERR500.getValue() + ",detai:"+ExceptionUtil.getStackTraceAsString(e));
					}
				} else {
					authKey = PropertyLoader.AUTH_KEY;
					GetKey = true;
				}
				if ( GetKey ) {
					if(validateSignature((HttpServletRequest) request, authKey)){
						if (PropertyLoader.AUTH_MODE.equalsIgnoreCase("supervisor") &&  (request.getParameter("app") != null || request.getParameter("ip") != null)) {
							Map<String, Object> openapicSup = new HashMap<String, Object>();
							openapicSup.put("auth_id", authId);
							if (StringUtils.isNotBlank(request.getParameter("app"))) {
								openapicSup.put("app", request.getParameter("app").trim());
							}
							if (StringUtils.isNotBlank(request.getParameter("ip"))) {
								openapicSup.put("ip", request.getParameter("ip").trim());
							}
							Map<String, Object> keyMapr = messageProducer.call("openapi.supervisor", "validateAuth",openapicSup, 5000);
							if (!"true".equals(keyMapr.get("result"))) {
								map.put("error", Errorcode.ERR401_6.getValue());
							}else{
								chain.doFilter(request, response);
								return;
							}
						}else{
							chain.doFilter(request, response);
							return;
						}
					}else{
						map.put("error", Errorcode.ERR401_5.getValue());
					}
				}
			}
		} catch (Exception e) {
			map.put("error", Errorcode.ERR500.getValue() + ",detai:"+ ExceptionUtil.getStackTraceAsString(e));
		}
		map.put("flag", "false");
		PrintWriter out = response.getWriter();
		out.write(JSON.toJSONString(map));
		out.close();
		return;
	}

	/**
	 * 签名验证
	 */
	@SuppressWarnings("unchecked")
	public boolean validateSignature(HttpServletRequest request, String authKey) {
		String url = request.getRequestURI();
		String sign = request.getParameter("sign");
		if (url.indexOf("//") == 0) {
			url = url.replace("//", "/");
		}
		StringBuffer sortUri = new StringBuffer(url);
		Map<String, String[]> params = request.getParameterMap();
		sortUri.append("?");
		Set<String> keys  = params.keySet();
		List<String> list = new ArrayList<String>();
		list.addAll(keys);
		Collections.sort(list);
		for (String k : list) {
			if (!"sign_type".equals(k) && !"sign".equals(k)) {
				sortUri.append(k + "=");
				sortUri.append((params.get(k) != null && params.get(k).length > 0) ? params.get(k)[0] : "");
				sortUri.append("&");
			}
		}
		sortUri.deleteCharAt(sortUri.length() - 1);
		sortUri.append(authKey);
        String signFinal = MD5Utils.MD5(sortUri.toString().trim());
        LOG.debug("final sign str :" + sortUri + " signFinal:" + signFinal+ " sign:" + sign);
		// 验签成功
		if (sign.equalsIgnoreCase(signFinal)) {
			LOG.debug("Sign Validate SCUUESS!");
			return true;
		}
		return false;
	}

	/**
	 * 回收
	 */
	@Override
	public void destroy() {
	}
}