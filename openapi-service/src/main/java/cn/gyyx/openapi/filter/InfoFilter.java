package cn.gyyx.openapi.filter;

import cn.gyyx.elves.util.mq.PropertyLoader;
import cn.gyyx.openapi.enums.Errorcode;
import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class InfoFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(InfoFilter.class);

	/**
	 * 初始化
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext servletContext = filterConfig.getServletContext();
		WebApplicationContext app = WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}

	/**
	 * 过滤方法
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!"supervisor".equalsIgnoreCase(PropertyLoader.AUTH_MODE)) {
			map.put("flag", "false");
			map.put("error", Errorcode.ERR402_1.getValue());
			PrintWriter out = response.getWriter();
			out.write(JSON.toJSONString(map));
			out.close();
		}else{
			chain.doFilter(request, response);
		}
	}

	/**
	 * 回收
	 */
	@Override
	public void destroy() {
	}

}