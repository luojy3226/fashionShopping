package com.freedom.code.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.freedom.code.model.Province;
import com.freedom.code.test.Test;

/**
 * 测试
 * @author luo
 *
 */
@Controller
@RequestMapping("/test")
public class TestController {
	@Resource
	private Test test;
	
	@RequestMapping(value = "/provinces", method = RequestMethod.GET)
	@ResponseBody
	public Province getOneProvince(int id){
		return test.getProvinceById(id);
	}
}
