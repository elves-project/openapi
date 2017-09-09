package cn.gyyx.openapi.listener;

import cn.gyyx.elves.util.ExceptionUtil;
import cn.gyyx.elves.util.SpringUtil;
import cn.gyyx.elves.util.mq.PropertyLoader;
import cn.gyyx.elves.util.zk.ZookeeperExcutor;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @ClassName: zookeeperEnrollListener
 * @Description: web 启动运行，注册zookeeper 临时节点
 * @author East.F
 * @date 2017年5月27日 下午5:25:01
 */
public class zookeeperEnrollListener implements ServletContextListener {

	private static final Logger LOG = Logger.getLogger(zookeeperEnrollListener.class);
	@Override
	public void contextInitialized(ServletContextEvent event) {
		//设置conf.properties 文件路径
//		String openapiPath=System.getProperty("OPENAPI_PATH");
//		if(StringUtils.isBlank(openapiPath)){
//			throw new RuntimeException("get system properties fail,key is opeanapi-path");
//		}

        ServletContext context = event.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        SpringUtil.app=ctx;

		if("true".equalsIgnoreCase(PropertyLoader.ZOOKEEPER_ENABLED)){
			try {
				ZookeeperExcutor zke=new ZookeeperExcutor(PropertyLoader.ZOOKEEPER_HOST,
						PropertyLoader.ZOOKEEPER_OUT_TIME, PropertyLoader.ZOOKEEPER_OUT_TIME);

				//创建模块根节点
				if(null==zke.getClient().checkExists().forPath(PropertyLoader.ZOOKEEPER_ROOT)){
					zke.getClient().create().creatingParentsIfNeeded().forPath(PropertyLoader.ZOOKEEPER_ROOT);
				}
				if(null==zke.getClient().checkExists().forPath(PropertyLoader.ZOOKEEPER_ROOT+"/openapi")){
					zke.getClient().create().creatingParentsIfNeeded().forPath(PropertyLoader.ZOOKEEPER_ROOT+"/openapi");
				}

				String nodeName=zke.createNode(PropertyLoader.ZOOKEEPER_ROOT+"/openapi/", "");
				if(null!=nodeName){
					zke.addListener(PropertyLoader.ZOOKEEPER_ROOT+"/openapi/", "");
					LOG.info("register zookeeper openapi node success");
				}
			}catch (Exception e){
				LOG.error("register zookeeper openapi node fail , msg:"+ ExceptionUtil.getStackTraceAsString(e));
			}

		}
		
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}
	
}
