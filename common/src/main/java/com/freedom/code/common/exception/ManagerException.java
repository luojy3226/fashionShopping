package com.freedom.code.common.exception;

/**
 * 自定义manager层异常
 * @author luo
 *
 */
public class ManagerException extends ServiceException{
	public ManagerException() {
		super();
	}

	public ManagerException(String s) {
		super(s);
	}
	
	public ManagerException(Throwable e){
        super(e);
    }

	public ManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
