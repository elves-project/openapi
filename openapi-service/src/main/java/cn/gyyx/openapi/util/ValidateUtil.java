package cn.gyyx.openapi.util;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.quartz.CronExpression;

import com.alibaba.fastjson.JSON;

/**
 * @ClassName: ValidateUtil
 * @Description: 验证工具类
 * @author East.F
 * @date 2017年6月16日 上午9:43:49
 */
public class ValidateUtil {

	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	/**
	 * @Title: validateIpAddress
	 * @Description: 验证一个字符串是否为ip地址
	 * @param ipAddress
	 * @return boolean 返回类型
	 */
	public static boolean validateIpAddress(String ipAddress) {
		if(null==ipAddress){
			return false;
		}
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}
	
	/**
	 * @Title: validateJson
	 * @Description: 验证一个字符串是否为json格式（null值返回false）
	 * @param json
	 * @return boolean    返回类型
	 */
	public static boolean validateJson(String json) {
		if(json==null){
			return false;
		}
		try {
			JSON.parse(json);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * @Title: validateNumber
	 * @Description: 验证一个字符串是否为数字
	 * @param number
	 * @return boolean    返回类型
	 */
	public static boolean validateNumber(String number) {
		if(null==number){
			return false;
		}
		for (int i = 0; i < number.length(); i++) {
			if (!Character.isDigit(number.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @Title: validataQuartzRule
	 * @Description: 验证quartz表达式格式是否正确
	 * @param quartzRule
	 * @return boolean    返回类型
	 */
	public static boolean validataQuartzRule(String quartzRule){
		if(null==quartzRule){
			return false;
		}
		try {
			new CronExpression(quartzRule);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
}
