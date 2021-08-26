package com.site.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.site.blog.constants.BlogStatusEnum;
import com.site.blog.constants.DeleteStatusEnum;
import com.site.blog.mapper.*;
import com.site.blog.model.dto.PageDto;
import com.site.blog.model.entity.*;
import com.site.blog.model.vo.BlogDetailVO;
import com.site.blog.model.vo.SimpleBlogListVO;
import com.site.blog.service.*;
import com.site.blog.util.BeanMapUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 博客信息表 服务实现类
 * </p>
 *
 * @author: 南街
 * @since 2019-08-27
 */
@Service
public class BlogInfoServiceImpl extends ServiceImpl<BlogInfoMapper, BlogInfo> implements BlogInfoService {

    @Resource
    private BlogInfoMapper blogInfoMapper;

    @Resource
    private BlogTagService blogTagMapper;
    @Resource
    private BlogCategoryService blogCategoryMapper;
    @Resource
    private CategoryService categoryMapper;
    @Resource
    private TagService tagMapper;
    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<SimpleBlogListVO> getNewBlog() {
        List<SimpleBlogListVO> simpleBlogListVOS = new ArrayList<>();
        Page<BlogInfo> page = new Page<>(1, 5);
        blogInfoMapper.selectPage(page, new QueryWrapper<BlogInfo>()
                .lambda()
                .eq(BlogInfo::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                .eq(BlogInfo::getIsDeleted, DeleteStatusEnum.NO_DELETED.getStatus())
                .orderByDesc(BlogInfo::getCreateTime));
        for (BlogInfo blogInfo : page.getRecords()) {
            SimpleBlogListVO simpleBlogListVO = new SimpleBlogListVO();
            BeanUtils.copyProperties(blogInfo, simpleBlogListVO);
            simpleBlogListVOS.add(simpleBlogListVO);
        }
        return simpleBlogListVOS;
    }

    @Override
    public List<SimpleBlogListVO> getHotBlog() {
        List<SimpleBlogListVO> simpleBlogListVOS = new ArrayList<>();
        Page<BlogInfo> page = new Page<>(1, 5);
        blogInfoMapper.selectPage(page, new QueryWrapper<BlogInfo>()
                .lambda()
                .eq(BlogInfo::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                .eq(BlogInfo::getIsDeleted, DeleteStatusEnum.NO_DELETED.getStatus())
                .orderByDesc(BlogInfo::getBlogViews));
        for (BlogInfo blogInfo : page.getRecords()) {
            SimpleBlogListVO simpleBlogListVO = new SimpleBlogListVO();
            BeanUtils.copyProperties(blogInfo, simpleBlogListVO);
            simpleBlogListVOS.add(simpleBlogListVO);
        }
        return simpleBlogListVOS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean clearBlogInfo(Long blogId) {
        if (SqlHelper.retBool(blogInfoMapper.deleteById(blogId))) {
            QueryWrapper<BlogTag> tagRelationWrapper = new QueryWrapper<>();
            tagRelationWrapper.lambda().eq(BlogTag::getBlogId, blogId);
            blogTagMapper.remove(tagRelationWrapper);
            QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
            commentWrapper.lambda().eq(Comment::getBlogId, blogId);
            commentMapper.delete(commentWrapper);
            return true;
        }
        return false;
    }

    @Override
    public List<BlogDetailVO> getBlogs(PageDto pageDto) {
        QueryWrapper<BlogInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(BlogInfo::getUpdateTime);
        Page<BlogInfo> page = new Page<>(pageDto.getPageNum(), pageDto.getPageSize());
        Page<BlogInfo> blogInfoPage = blogInfoMapper.selectPage(page, queryWrapper);

        List<BlogDetailVO> blogDetailVOS = blogInfoPage.getRecords().stream().map(BeanMapUtil::copyBlog).collect(Collectors.toList());

        blogDetailVOS.forEach(post -> {

            QueryWrapper<BlogTag> tagQueryWrapper = new QueryWrapper<BlogTag>().eq("blog_id", post.getBlogId());
            List<Tag> tags = blogTagMapper.list(tagQueryWrapper).stream().map(item -> tagMapper.getById(item.getTagId())).collect(Collectors.toList());
            post.setBlogTags(tags);
            Integer cateId = blogCategoryMapper.getById(new QueryWrapper<BlogCategory>().eq("blog_id", post.getBlogId())).getCategoryId();
            if (cateId != null) {
                post.setBlogCategory(categoryMapper.getById(cateId));
            }
        });
        return blogInfoMapper.getBlogDetail();
    }
}
