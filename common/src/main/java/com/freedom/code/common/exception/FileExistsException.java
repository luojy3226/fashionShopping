package com.freedom.code.common.exception;

/**
 * 文件已存在异常
 * @author luo
 *
 */
public class FileExistsException extends ServiceException{
	public FileExistsException() {
		super();
	}

	public FileExistsException(String s) {
		super(s);
	}
	
	public FileExistsException(Throwable e){
        super(e);
    }

	public FileExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
