package com.freedom.code.common.utils.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.util.JSONPObject;
import org.springframework.web.servlet.ModelAndView;

import com.freedom.code.common.ValidateUtils;
import com.freedom.code.common.exception.ServiceException;
import com.freedom.code.common.utils.json.JsonUtils;

/**
 * 统一的数据返回类
 * @author luo
 *
 */
public class Res {
	//利用cas实现单点登录
	public final static String USER_REPEAT_KEY= "__USER_REPEAT__";
	//存放用户的信息
    public final static Map<String, AtomicLong> casContext = new HashMap<>();
    private String casKey = null;
    private long casValue = 0;
    
    /**
     * 返回数据类型枚举
     */
    private enum Code {
        Ok(200), Redirect(302), Verify(403), Error(500);

        private int value = 0;

        Code(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
        
    }
    
    /**
     * 信息类
     * @author luo
     *
     */
    private static class Message{
    	private String id;
    	private String message;

    	public Message(String id, String message){
    		this.id = id;
    		this.message = message;
    	}

		public String getId() {
			return id;
		}

		public String getMessage() {
			return message;
		}
    }
    
    /**
     * 类型默认为OK
     */
    private int	code = Code.Ok.value();

    /**
     * 包含格式的返回数据
     */
    private List<Message> messages = new ArrayList<Message>();

    /**
     * 返回的数据包
     */
    private Map<String, Object> data = new HashMap<String, Object>();

    /**
     * 默认构造器
     */
    public Res() {
    	data.put("time", System.currentTimeMillis());
    }
    
    /**
     * 默认构造器
     */
    public Res(Object userId) {
        this.casKey = USER_REPEAT_KEY + userId;
        //从
        AtomicLong casItem = casContext.get(this.casKey);
        if (casItem != null) {
            this.casValue = casItem.incrementAndGet();//给当前值加1
        }else{
            casItem = new AtomicLong();
            casContext.put(this.casKey, casItem);
            this.casValue = casItem.incrementAndGet();
        }
    }
    
    /**
     * 1、设置成功消息
     * @param message
     */
    public Res addSuccess(String message) {
        return addMessage("doAction", message, false);
    }
    /**
     * 2、设置跳转   针对Redirect
     * @param url
     */
    public void setRedirect(String url) {
        this.code = Code.Redirect.value();
        addMessage("returnUrl", url, false);
    }

    /**
     * 3、设置验证码  针对Verify
     */
    public void setVerify() {
        this.code = Code.Verify.value();
    }

    /**
     * 4、设置错误信息
     * @param errorMessage
     */
    public Res addError(String errorMessage) {
        return addError("doAction", errorMessage);
    }
    
    /**
     * 4、设置错误信息，定位元素
     * @param errorKey
     * @param errorMessage
     */
    public Res addError(String errorKey, String errorMessage) {
        if (ValidateUtils.isNull(errorKey)
                || ValidateUtils.isNull(errorMessage)) {
            return this;
        }
        addMessage(errorKey, errorMessage, false);
        if(this.code != Code.Error.value()){
        	this.code = Code.Error.value(); // 设置为错误
        }
        return this;
    }
    
    /**
     * 设置消息体
     * @param id
     * @param message
     * @param cdata
     */
    private Res addMessage(String id, String message, boolean cdata) {
        if (ValidateUtils.isNull(message)) {
            return this;
        }
        if (cdata) {
            message = "<![CDATA[ " + message + " ]]>";
        }
        messages.add(new Message(id, message));
        return this;
    }
    
    /**
     * 添加返回数据
     * @param key
     * @param value
     * @return
     */
    public Res addData(String key , Object value) {
		this.data.put(key, value);
		return this;
	}
    
    public Res setResponse(Object value) {
		addData("response", value);
		return this;
	}
    
    public Res addResponse(String key , Object value) {
		Map responseMap = new HashMap();
		if(this.getData().containsKey("response")){
			responseMap = (Map)this.getData().get("response");
		}
		responseMap.put(key, value);
		addData("response", responseMap);
		return this;
	}
    
    @SuppressWarnings("static-access")
    public void commit() throws ServiceException {
      //throw new ServiceException(messages.getMessage("sys.busy"));
        if (this.casKey != null) {
            AtomicLong casItem = casContext.get(this.casKey);
            if (casItem != null) {
                if(this.casValue!=casItem.get()){
                    throw new RuntimeException("系统繁忙，请稍候再试");
                }
            }
        }
	}
    
    private static String toJavaScript(String str) {
        if (ValidateUtils.isNull(str))
            return "";
        StringBuffer out = new StringBuffer();
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            switch (ch) {
            case '\b':
                out.append('\\');
                out.append('b');
                break;
            case '\n':
                // out.append('\\');
                // out.append('n');
                break;
            case '\t':
                // out.append('\\');
                // out.append('t');
                break;
            case '\f':
                // out.append('\\');
                // out.append('f');
                break;
            case '\r':
                // out.append('\\');
                // out.append('r');
                break;
            case '\'':
                // out.append('\\');
                out.append('\\');
                out.append('\'');
                break;
            case '"':
                // out.append('\\');
                out.append('"');
                break;
            case '\\':
                out.append('\\');
                out.append('\\');
                break;
            default:
                out.append(ch);
                break;
            }
        }
        // str = str.replaceAll("'", "\\'");
        // str = str.replaceAll("\r\n", "");
        // str = str.replaceAll("\n", "");
        // str = str.replaceAll("\r", "");
        // str = str.replaceAll("\t", ""); //转义字符
        return out.toString();
    }
    
    /**
     * 导出为json格式
     * @return
     */
    public String toJson() {
        StringBuffer json = new StringBuffer();
        json.append("{");

        json.append("code:'" + code + "',");
        json.append("messages:[");
        for (Message message : messages) {
            if (message != null){
                json.append("{id:'" + message.getId() + "'," +
                			"message:'" + toJavaScript(message.getMessage()) + "'},");
            }
        }
        if (messages.size() > 0) {
            // 去掉最后一个,
            json.setLength(json.length() - 1);
        }
        json.append("]");

        json.append("}");
        return json.toString();
    }
    
 // 导出为xml格式
    public String toXml() {
        StringBuffer xml = new StringBuffer();
        xml.append("<response code=\"" + code + "\">");
        if (code == Code.Ok.value()) {
            xml.append("<messages>");
            for (Message message : messages) {
                if (message != null) {
                    xml.append("<message id=\"" + message.getId() + "\">");
                    xml.append(message.getMessage());
                    xml.append("</message>");
                }
            }
            xml.append("</messages>");
        }
        xml.append("</response>");
        return xml.toString();
    }
    
    /**
     * 返回js格式的形式，主要用于跨域调用
     * @return
     */
    public String toJs(String callback) {
        if (ValidateUtils.isNull(callback)) {
            callback = "jsCallBack";
        }
        StringBuffer js = new StringBuffer();
        String json = toJson();
        js.append("if(" + callback + "){\n" + callback + "(" + json
                + "[0]);\n}");
        return js.toString();
    }
    
    public ModelAndView toView(FormatType formatType, String viewName, String callback, HttpServletResponse response) {
    	return toView(formatType, viewName, callback, response, null);
    }
    
    /**
     * 返回视图对象
     * @param formatType 返回视图类型
     * @param callback   返回值
     * @param viewName   视图名称
     * @param response   servletResponse,用于返回值
     * @param global     全局配置集合信息
     * @return
     */
    public ModelAndView toView(FormatType formatType, String viewName, String callback, HttpServletResponse response, Map<String, Object> global) {
    	if (FormatType.json.name().equals(formatType.name())) {//返回json类型
    		if(callback==null || "".equals(callback)){
    			return JsonView.Render(this, response);
    		}else{
    			//将返回数据转为json对象在返回
    			return JsonView.Render(new JSONPObject(callback, this), response);
    		}
		}else if (FormatType.html.name().equals(formatType.name())) {//返回html类型
			if(viewName==null || "".equals(viewName)){
				ModelAndView mav =  new ModelAndView("null");
				mav.addObject("res", this);
				return mav;
			}else{
				if (this.getCode() == Code.Error.value()) {
					viewName = "500";
				}
				ModelAndView mav =  new ModelAndView(viewName);
				mav.addObject("res", this);
				if(global!=null){
					mav.addObject("global", global);
//					mav.addObject("globalStr", JsonUtils.convertEntity2Json(global));
					//将其转为json返回
					mav.addObject("globalStr", JsonUtils.convertObjectToJson(global));
				}
				return mav;
			}
		}else{
			return null;
		}
    }
    
    public int getCode() {
        return code;
    }

	public List<Message> getMessages() {
		return messages;
	}
	
	public Map<String, Object> getData() {
		return data;
	}
}
