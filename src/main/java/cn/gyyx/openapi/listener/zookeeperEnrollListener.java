package cn.gyyx.openapi.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import cn.gyyx.elves.util.ExceptionUtil;
import cn.gyyx.elves.util.mq.PropertyLoader;
import cn.gyyx.elves.util.zk.ZookeeperExcutor;

/**
 * @ClassName: zookeeperEnrollListener
 * @Description: web 启动运行，注册zookeeper 临时节点
 * @author East.F
 * @date 2017年5月27日 下午5:25:01
 */
public class zookeeperEnrollListener implements ServletContextListener {

	private static final Logger LOG = Logger.getLogger(zookeeperEnrollListener.class);
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
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
