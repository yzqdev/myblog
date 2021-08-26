package com.site.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.site.blog.constants.SysConfigConstants;
import com.site.blog.mapper.BlogInfoMapper;
import com.site.blog.mapper.CategoryMapper;
import com.site.blog.model.entity.BlogCategory;
import com.site.blog.model.entity.BlogInfo;
import com.site.blog.model.entity.Category;
import com.site.blog.service.BlogCategoryService;
import com.site.blog.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 博客分类 服务实现类
 * </p>
 *
 * @author: 南街
 * @since 2019-08-30
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    CategoryMapper categoryMapper;
    @Resource
    private BlogInfoMapper blogInfoMapper;
@Resource
    BlogCategoryService blogCategoryService;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean clearCategory(Integer id) {
        Category category = categoryMapper.selectById(id);
        BlogCategory blogCategory = new BlogCategory();
                blogCategory.setCategoryId(Integer.valueOf(SysConfigConstants.DEFAULT_CATEGORY.getConfigField()));

        LambdaUpdateWrapper<BlogCategory> updateWrapper = Wrappers.<BlogCategory>lambdaUpdate()
                .eq(BlogCategory::getCategoryId, category.getCategoryId());
        blogCategoryService.update(blogCategory, updateWrapper);
        return retBool(baseMapper.deleteById(category.getCategoryId()));
    }
}
