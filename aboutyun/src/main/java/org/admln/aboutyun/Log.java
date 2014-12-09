package org.admln.aboutyun;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author admln
 * 定义异常类
 * 
 */
class MyException extends Exception {
	
	private static final long serialVersionUID = 1L;
	int flag;
	public MyException(String msg,int flag) {
		super(msg);
		this.flag = flag;
	}
	public int getFlag() {
		return flag;
	}
}


/**
 * @author admln
 *
 */
public class Log {
	
	private String ip;//访问者IP
	private String url;//访问的URL地址
	private String serWord;//用户搜索词
	private String forum;//模块ID
	private String space;//空间UID
	private boolean weixin = false;//判断是否是微信IP
	private boolean picWord = false;//图文阅读区判断
	private boolean guide = false;//导读判断
	private boolean blog = false;//是否是博客访问
	private boolean persionalBlog = false;//是否是个人博客访问
	private boolean ranklist = false;//是否是排行榜访问
	private boolean share = false; //是否是分享访问
	private boolean group = false;
	private boolean groupID = false;//群组访问
	private boolean broadcastMine = false;
	private boolean broadcastOther = false;//广播访问 
	
	private boolean isValid = true;
	
	/*
	 * 转换函数，返回一个blog日志对象
	 */
	public static Log parser(String str) throws MyException {
		Log log = new Log();
		
		//解析IP并验证合法性
		if(Log.isIPValid(str.split(" ")[0])) {
			log.setIp(str.split(" ")[0]);
		}else {
			throw new MyException("IP wrong!",1);
		}
		
		//获得URL
		log.setUrl(str.split(" ")[6]);//可以根据网站URL特征验证合法性
		
		//获得搜索词
		if(log.getUrl().startsWith("/search.php")&&log.getUrl().contains("srchtxt")) {//合法性还可以优化
			log.setSerWord(str.split(" ")[6].split("=")[2].split("&")[0]);
		}
		
		//获取模块ID
		if(log.getUrl().startsWith("/forum-")) {
			log.setForum(log.getUrl().split("-")[1]);
		}
		
		//获取空间UID
		if(log.getUrl().startsWith("/space-uid-")) {
			log.setSpace(log.getUrl().split("-")[2].split("\\.")[0]);
		}
		
		//微信判断
		if(log.getUrl().startsWith("/hux_wx-qr.html")) {
			log.setWeixin(true);
		}
		
		//图文阅读区判断
		if(log.getUrl().startsWith("/plugin.php?id")) {
			log.setPicWord(true);
		}
		
		//导读判断
		if(log.getUrl().startsWith("/forum.php?mod=guide")) {
			log.setGuide(true);
		}
		
		//博客判断
		if(log.getUrl().startsWith("/home.php?mod=space&do=blog")) {
			log.setBlog(true);
		}else if(log.getUrl().startsWith("/blog-")){
			log.setPersionalBlog(true);
		}
		
		//排行榜判断
		if(log.getUrl().startsWith("/misc.php?mod=ranklist")) {
			log.setRanklist(true);
		}
		
		//判断分享
		if(log.getUrl().startsWith("/home.php?mod=space&do=share&view=all")) {
			log.setShare(true);
		}
		
		//群组判断
		if(log.getUrl().startsWith("/group.php")) {
			log.setGroup(true);
		}else if(log.getUrl().startsWith("/group-")){
			log.setGroupID(true);
		}
		
		//群组判断
		if(log.getUrl().startsWith("/home.php?mod=follow&view=follow")) {
			log.setBroadcastMine(true);
		}else if(log.getUrl().startsWith("/home.php?mod=follow&view=other")){
			log.setBroadcastOther(true);
		}
		
		return log;
	}
	
	/*
	 * 验证IP合法性
	 */
	public static boolean isIPValid(String ip) {
		String testIP = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
		Pattern pattern = Pattern.compile(testIP);
		Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String str = "61.160.224.138 - - [11/Jul/2014:01:01:13 +0800] \"GET /search.php?mod=forum&formhash=2f13ed92&searchsubmit=true&source=hotsearch HTTP/1.0\" 200 7519 \"http://www.aboutyun.com/home.php?mod=space&do=notice&view=system\" \"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36\"";
		String testIp = str.split(" ")[0];
		String testURL = str.split(" ")[6];
		String testSerWord = str.split(" ")[6].split("=")[2].split("&")[0];
		System.out.println(testURL.contains("srchtxt"));
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public String getSerWord() {
		return serWord;
	}

	public void setSerWord(String serWord) {
		this.serWord = serWord;
	}

	public String getForum() {
		return forum;
	}

	public void setForum(String forum) {
		this.forum = forum;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public boolean isWeixin() {
		return weixin;
	}

	public void setWeixin(boolean weixin) {
		this.weixin = weixin;
	}

	public boolean isPicWord() {
		return picWord;
	}

	public void setPicWord(boolean picWord) {
		this.picWord = picWord;
	}

	public boolean isGuide() {
		return guide;
	}

	public void setGuide(boolean guide) {
		this.guide = guide;
	}

	public boolean isBlog() {
		return blog;
	}

	public void setBlog(boolean blog) {
		this.blog = blog;
	}

	public boolean isPersionalBlog() {
		return persionalBlog;
	}

	public void setPersionalBlog(boolean persionalBlog) {
		this.persionalBlog = persionalBlog;
	}

	public boolean isRanklist() {
		return ranklist;
	}

	public void setRanklist(boolean ranklist) {
		this.ranklist = ranklist;
	}

	public boolean isShare() {
		return share;
	}

	public boolean isBroadcastMine() {
		return broadcastMine;
	}

	public void setBroadcastMine(boolean broadcastMine) {
		this.broadcastMine = broadcastMine;
	}

	public boolean isBroadcastOther() {
		return broadcastOther;
	}

	public void setBroadcastOther(boolean broadcastOther) {
		this.broadcastOther = broadcastOther;
	}

	public boolean isGroup() {
		return group;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	public boolean isGroupID() {
		return groupID;
	}

	public void setGroupID(boolean groupID) {
		this.groupID = groupID;
	}

	public void setShare(boolean share) {
		this.share = share;
	}

}
