package com.freedom.code.common.exception;

/**
 * 自定义dao层异常
 * @author luo
 *
 */
public class DaoException extends RuntimeException{
	public DaoException() {
		super();
	}

	public DaoException(String s) {
		super(s);
	}
	
	public DaoException(Throwable e){
        super(e);
    }

	public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
