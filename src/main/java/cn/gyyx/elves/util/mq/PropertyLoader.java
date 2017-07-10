package cn.gyyx.elves.util.mq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import cn.gyyx.elves.util.ExceptionUtil;

/**
 * @ClassName: PropertyLoader
 * @Description: *.property 配置文件加载类
 * @author East.F
 * @date 2016年5月5日 上午11:51:27
 */
public class PropertyLoader {

	private static final Logger LOG=Logger.getLogger(PropertyLoader.class);
	
	private static Properties properties = new Properties();
	private static InputStream is = null;
	
	public static String ZOOKEEPER_ENABLED;
	public static String ZOOKEEPER_HOST;
	public static int ZOOKEEPER_OUT_TIME;
	public static String ZOOKEEPER_ROOT;
	
	public static String MQ_IP;
	public static int MQ_PORT;
	public static String MQ_USER;
	public static String MQ_PASSWORD;
	public static String MQ_EXCHANGE;
	
	public static String AUTH_MODE;
	public static String AUTH_ID;
	public static String AUTH_KEY;
	
	public static String CRON_ENABLED;
	public static String QUEUE_ENABLED;
	
	static {
		try {
			is = PropertyLoader.class.getResourceAsStream("/conf.properties");
			properties.load(is);
			
			ZOOKEEPER_ENABLED=properties.getProperty("zookeeper.enabled");
			ZOOKEEPER_HOST = properties.getProperty("zookeeper.host");
    		ZOOKEEPER_OUT_TIME =Integer.parseInt(properties.getProperty("zookeeper.outTime"));
    		ZOOKEEPER_ROOT = properties.getProperty("zookeeper.root");
    		
    		MQ_IP = properties.getProperty("mq.ip");
    		MQ_PORT = Integer.parseInt(properties.getProperty("mq.port"));
    		MQ_USER = properties.getProperty("mq.user");
    		MQ_PASSWORD = properties.getProperty("mq.password");
    		MQ_EXCHANGE = properties.getProperty("mq.exchange");
    		
    		AUTH_MODE = properties.getProperty("auth.mode");
    		AUTH_ID = properties.getProperty("auth.id");
    		AUTH_KEY = properties.getProperty("auth.key");
    		
    		CRON_ENABLED = properties.getProperty("cron.enabled");
    		QUEUE_ENABLED =  properties.getProperty("queue.enabled");
    		
		} catch (IOException e) {
			LOG.error(ExceptionUtil.getStackTraceAsString(e));
		}
	}
}
