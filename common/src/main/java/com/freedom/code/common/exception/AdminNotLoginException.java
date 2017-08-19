package com.freedom.code.common.exception;

/**
 * 自定义未登陆异常
 * @author luo
 *
 */
public class AdminNotLoginException extends ServiceException{
	public AdminNotLoginException() {
		super();
	}

	public AdminNotLoginException(String s) {
		super(s);
	}
	
	public AdminNotLoginException(Throwable e){
        super(e);
    }

	public AdminNotLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
