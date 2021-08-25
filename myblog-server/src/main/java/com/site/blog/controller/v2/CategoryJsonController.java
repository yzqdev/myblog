package com.site.blog.controller.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.site.blog.constants.DeleteStatusEnum;
import com.site.blog.constants.HttpStatusEnum;
import com.site.blog.model.dto.AjaxPutPage;
import com.site.blog.model.dto.AjaxResultPage;
import com.site.blog.model.dto.Result;
import com.site.blog.model.entity.BlogCategory;
import com.site.blog.model.entity.BlogInfo;
import com.site.blog.service.BlogCategoryService;
import com.site.blog.service.BlogInfoService;
import com.site.blog.util.DateUtils;
import com.site.blog.util.ResultGenerator;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @qq交流群 796794009
 * @qq 1320291471
 * @Description: 分类Controller
 * @date: 2019/8/6 17:24
 */
@Controller
@RequestMapping("/v2/admin")
@Api(tags = "分类json")
public class CategoryJsonController {

    @Resource
    private BlogCategoryService blogCategoryService;

    @Resource
    private BlogInfoService blogInfoService;

    /**
     * 分类的集合数据[用于下拉框]
     *
     * @param
     * @return com.site.blog.pojo.dto.Result<com.site.blog.entity.BlogCategory>
     * @date 2019/8/30 14:38
     */
    @ResponseBody
    @GetMapping("/category/list")
    public Result<List<BlogCategory>> categoryList() {
        QueryWrapper<BlogCategory> queryWrapper = new QueryWrapper<BlogCategory>();
        queryWrapper.lambda().eq(BlogCategory::getIsDeleted, DeleteStatusEnum.NO_DELETED.getStatus()).orderByDesc(BlogCategory::getCreateTime) ;
        List<BlogCategory> list = blogCategoryService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, list);
    }



    /**
     * 分类的分页
     *
     * @param ajaxPutPage
     * @param condition
     * @return com.site.blog.pojo.dto.AjaxResultPage<com.site.blog.entity.BlogCategory>
     * @date 2019/8/30 14:38
     */
    @ResponseBody
    @GetMapping("/category/paging")
    public AjaxResultPage<BlogCategory> getCategoryList(AjaxPutPage<BlogCategory> ajaxPutPage, BlogCategory condition) {
        QueryWrapper<BlogCategory> queryWrapper = new QueryWrapper<>(condition);
        queryWrapper.lambda()
                .orderByAsc(BlogCategory::getCategoryRank)
                .ne(BlogCategory::getCategoryId, 1);
        Page<BlogCategory> page = ajaxPutPage.putPageToPage();
        blogCategoryService.page(page, queryWrapper);
        AjaxResultPage<BlogCategory> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    /**
     * 修改分类信息
     * @author Linn-cn
     * @date 2020/9/1
     * @return com.site.blog.pojo.dto.Result<java.lang.String>
     */
    @ResponseBody
    @PostMapping("/category/update")
    public Result<String> updateCategory(BlogCategory blogCategory) {
        BlogCategory sqlCategory = blogCategoryService.getById(blogCategory.getCategoryId());
        boolean flag = sqlCategory.getCategoryName().equals(blogCategory.getCategoryName());
        if (!flag) {
            BlogInfo blogInfo = new BlogInfo()
                    .setBlogCategoryId(blogCategory.getCategoryId())
                    .setBlogCategoryName(blogCategory.getCategoryName());
            UpdateWrapper<BlogInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(BlogInfo::getBlogCategoryId, blogCategory.getCategoryId());
            blogInfoService.update(blogInfo, updateWrapper);
            blogCategoryService.updateById(blogCategory);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
    }

    /**
     * 修改分类状态
     *
     * @param blogCategory
     * @return com.site.blog.pojo.dto.Result
     * @date 2019/8/30 14:55
     */
    @ResponseBody
    @PostMapping("/category/isDel")
    public Result<String> updateCategoryStatus(BlogCategory blogCategory) {
        boolean flag = blogCategoryService.updateById(blogCategory);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 清除分类信息
     *
     * @param id category id
     * @return com.site.blog.pojo.dto.Result
     * @date 2019/9/1 15:48
     */
    @ResponseBody
    @PostMapping("/category/clear/{id}")
    public Result  clearCategory(@PathVariable("id") Integer id) {
        if (blogCategoryService.clearCategory(id)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK,true,id);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }



    /**
     * 新增分类信息
     *
     * @param blogCategory
     * @return com.site.blog.pojo.dto.Result
     * @date 2019/9/1 15:48
     */
    @ResponseBody
    @PostMapping("/category/add")
    public Result addCategory(BlogCategory blogCategory) {
        blogCategory.setCreateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogCategoryService.save(blogCategory);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK,true,blogCategory);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }


}
