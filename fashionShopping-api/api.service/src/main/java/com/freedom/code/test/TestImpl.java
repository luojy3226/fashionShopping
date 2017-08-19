package com.freedom.code.test;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.freedom.code.mapper.ProvinceMapper;
import com.freedom.code.model.Province;

@Service("test")
public class TestImpl implements Test{
	@Resource
	private ProvinceMapper mapper;
	
	@Override
	public Province getProvinceById(int id) {
		return mapper.selectByPrimaryKey(id);
	}

}
