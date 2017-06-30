package cn.gyyx.openapi.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

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
		//连接zookeeper，注册临时节点
		ZookeeperExcutor zke=new ZookeeperExcutor(PropertyLoader.ZOOKEEPER_HOST,
				PropertyLoader.ZOOKEEPER_OUT_TIME, PropertyLoader.ZOOKEEPER_OUT_TIME);
		String nodeName=zke.createNode(PropertyLoader.ZOOKEEPER_ROOT+"/Openapi/", "");
		if(null!=nodeName){
			zke.addListener(PropertyLoader.ZOOKEEPER_ROOT+"/Openapi/", "");
		}
		LOG.info("regster zookeeper node success...");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}
	
}
