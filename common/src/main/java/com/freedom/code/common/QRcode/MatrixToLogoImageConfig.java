package com.freedom.code.common.QRcode;

import java.awt.Color;

/**
 * 方形二维码中间添加logo图片配置类
 * 设置默认颜色、边界像素、照片大小
 * @author luo
 *
 */
public class MatrixToLogoImageConfig {
	 // logo默认边框颜色
     public static final Color DEFAULT_BORDERCOLOR = Color.RED;
	 // logo默认边框宽度
	 public static final int DEFAULT_BORDER = 2;
	// logo大小默认为照片的1/5
	public static final int DEFAULT_LOGOPART = 5;
	private final int border = DEFAULT_BORDER;
	private  Color borderColor;
	private  int logoPart ;
	
	//构造方法
	public MatrixToLogoImageConfig() {
        this(DEFAULT_BORDERCOLOR, DEFAULT_LOGOPART);
   }
	
   public MatrixToLogoImageConfig(Color borderColor, int logoPart) {
	    this.borderColor = borderColor;
	    this.logoPart = logoPart;
    }

	public Color getBorderColor() {
		return borderColor;
	}
	
	
	public int getLogoPart() {
		return logoPart;
	}
	
	public int getBorder() {
		return border;
	}
   
   
}
