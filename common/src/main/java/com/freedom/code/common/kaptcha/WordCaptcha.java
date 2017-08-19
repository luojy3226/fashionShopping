package com.freedom.code.common.kaptcha;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import static com.freedom.code.common.kaptcha.Randoms.num;

/**
 * <p>
 * png格式汉字验证码
 * 把XP下的字体C:\WINDOWS\FONTS\simsun.ttf（也就是宋体,大小为10M），把他重命名为 simsun.ttf  
 * 拷贝simsun.ttf 字体到 /usr/local/services/jdk1.7.0/jre/lib/fonts/simsun.ttc
 * </p>
 *
 * @author: wuhongjun
 * @version:1.0
 */

public class WordCaptcha extends Kaptcha {
	public WordCaptcha() {
	}

	public WordCaptcha(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public WordCaptcha(int width, int height, int len) {
		this(width, height);
		this.len = len;
	}

	public WordCaptcha(int width, int height, int len, Font font) {
		this(width, height, len);
		this.font = font;
	}

	/**
	 * 生成验证码
	 * 
	 * @throws java.io.IOException
	 *             IO异常
	 */
	@Override
	public void out(OutputStream out) {
		graphicsImage(alphas(), out);
	}

	/**
	 * 画随机码图
	 * 
	 * @param strs
	 *            文本
	 * @param out
	 *            输出流
	 */
	private boolean graphicsImage(char[] strs, OutputStream out) {
		boolean ok = false;
		try {
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) bi.getGraphics();
			AlphaComposite ac3;
			Color color;
			int len = strs.length;
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			// 随机画干扰的蛋蛋
			for (int i = 0; i < 15; i++) {
				color = color(150, 250);
				g.setColor(color);
				g.drawOval(num(width), num(height), 5 + num(10), 5 + num(10));// 画蛋蛋，有蛋的生活才精彩
				color = null;
			}
			g.setFont(new Font("宋体", Font.BOLD, 20));
			int h = height - ((height - font.getSize()) >> 1), w = width / len, size = w - font.getSize() + 1;
			/* 画字符串 */
			int x = 5;
			for (int i = 0; i < len; i++) {
				ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);// 指定透明度
				g.setComposite(ac3);
				color = new Color(20 + num(110), 20 + num(110), 20 + num(110));// 对每个字符都用随机颜色
				g.setColor(color);
				// 设置字体旋转角度
	            int degree = new Random().nextInt() % 30;
	            // 正向角度
	            g.rotate(degree * Math.PI / 180, x, 20);
				g.drawString(strs[i] + "", (width - (len - i) * w) + size, h - 4);
				// 反向角度
	            g.rotate(-degree * Math.PI / 180, x, 20);
	            x += 30;
				color = null;
				ac3 = null;
			}
			ImageIO.write(bi, "png", out);
			out.flush();
			ok = true;
		} catch (IOException e) {
			ok = false;
		} finally {
			Streams.close(out);
		}
		return ok;
	}

}
