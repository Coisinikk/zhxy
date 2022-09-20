package com.zjy.zhxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.zhxy.mapper.ClazzMapper;
import com.zjy.zhxy.pojo.Clazz;
import com.zjy.zhxy.pojo.Grade;
import com.zjy.zhxy.service.ClazzService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service("clazzServiceImpl")
@Transactional
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {
    @Override
    public IPage<Clazz> getClazzsByOpr(Page<Clazz> pageParam, Clazz clazz) {
        QueryWrapper<Clazz> queryWrapper = new QueryWrapper<>();
        String gradeName = clazz.getGradeName();
        if(!StringUtils.isEmpty(gradeName)){
            queryWrapper.like("grade_name",gradeName);
        }
        String name = clazz.getName();
        if(!StringUtils.isEmpty(name)){
            queryWrapper.like("name",name);
        }
        queryWrapper.orderByAsc("id");
        Page<Clazz> page = baseMapper.selectPage(pageParam,queryWrapper);
        return page;
    }

    @Override
    public List<Clazz> getClazzs() {
        List<Clazz> clazzs = baseMapper.selectList(null);
        return clazzs;
    }
}
