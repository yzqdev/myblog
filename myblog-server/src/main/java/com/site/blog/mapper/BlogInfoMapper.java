package com.site.blog.mapper;

import com.site.blog.model.dto.PageDto;
import com.site.blog.model.entity.BlogInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.site.blog.model.vo.BlogDetailVO;

import java.util.List;

/**
 * <p>
 * 博客信息表 Mapper 接口
 * </p>
 *
 * @author zhulin
 * @since 2019-08-27
 */
public interface BlogInfoMapper extends BaseMapper<BlogInfo> {

    List<BlogDetailVO> getBlogDetail( );

    List<BlogInfo> searchBlog(String keyword);
}
