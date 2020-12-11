package com.changhong.sei.deploy.service;

import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.core.dto.serach.SearchFilter;
import com.changhong.sei.core.dto.serach.SearchOrder;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.deploy.dao.TagDao;
import com.changhong.sei.deploy.dto.CreateTagRequest;
import com.changhong.sei.deploy.dto.TagDto;
import com.changhong.sei.deploy.entity.AppModule;
import com.changhong.sei.deploy.entity.Tag;
import com.changhong.sei.integrated.service.GitlabService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 应用标签(Tag)业务逻辑实现类
 *
 * @author sei
 * @since 2020-11-23 08:34:09
 */
@Service("tagService")
public class TagService extends BaseEntityService<Tag> {
    private static final Logger LOG = LoggerFactory.getLogger(TagService.class);

    @Autowired
    private TagDao dao;

    @Autowired
    private AppModuleService moduleService;

    @Autowired
    private GitlabService gitlabService;

    @Override
    protected BaseEntityDao<Tag> getDao() {
        return dao;
    }

    /**
     * 获取最新的标签
     *
     * @param moduleCode 模块代码
     * @return 创建结果
     */
    public ResultData<TagDto> getLastTag(String moduleCode) {
        AppModule module = moduleService.findByProperty(AppModule.CODE_FIELD, moduleCode);
        if (Objects.isNull(module)) {
            return ResultData.fail("应用模块[" + moduleCode + "]不存在.");
        }

        Search search = Search.createSearch();
        search.addFilter(new SearchFilter(Tag.FIELD_MODULE_CODE, moduleCode));
        search.addSortOrder(new SearchOrder(Tag.FIELD_MAJOR, SearchOrder.Direction.DESC));
        search.addSortOrder(new SearchOrder(Tag.FIELD_MINOR, SearchOrder.Direction.DESC));
        search.addSortOrder(new SearchOrder(Tag.FIELD_REVISED, SearchOrder.Direction.DESC));
        Tag tag = dao.findFirstByFilters(search);
        return ResultData.success(convert(tag));
    }

    /**
     * 获取最新的标签
     *
     * @param id id
     * @return 创建结果
     */
    public ResultData<TagDto> getTag(String id) {
        Tag tag = dao.findOne(id);
        if (Objects.isNull(tag)) {
            return ResultData.fail("标签[" + id + "]不存在");
        }

        return ResultData.success(convert(tag));
    }

    private TagDto convert(Tag tag) {
        TagDto dto = new TagDto();
        if (Objects.nonNull(tag)) {
            dto.setModuleCode(tag.getModuleCode());
            dto.setName(tag.getCode());
            dto.setMajor(tag.getMajor());
            dto.setMinor(tag.getMinor());
            dto.setRevised(tag.getRevised());
            dto.setRelease(tag.getRelease());
            dto.setCommitId(tag.getCommitId());
            dto.setMessage(tag.getMessage());
            dto.setCreateTime(tag.getCreateTime());
            dto.setCreateAccount(tag.getCreateAccount());
        }
        return dto;
    }

    /**
     * 获取项目标签
     *
     * @param moduleCode 模块代码
     * @return 创建结果
     */
    public ResultData<List<TagDto>> getTags(String moduleCode) {
        Search search = Search.createSearch();
        search.addFilter(new SearchFilter(Tag.FIELD_MODULE_CODE, moduleCode));
        search.addSortOrder(new SearchOrder(Tag.FIELD_MAJOR, SearchOrder.Direction.DESC));
        search.addSortOrder(new SearchOrder(Tag.FIELD_MINOR, SearchOrder.Direction.DESC));
        search.addSortOrder(new SearchOrder(Tag.FIELD_REVISED, SearchOrder.Direction.DESC));
        List<Tag> tags = dao.findByFilters(search);
        if (CollectionUtils.isNotEmpty(tags)) {
            return ResultData.success(tags.stream().map(this::convert).collect(Collectors.toList()));
        } else {
            return ResultData.success(Lists.newArrayList());
        }

//        List<TagDto> tagList = new ArrayList<>(16);
//        ResultData<List<org.gitlab4j.api.models.Tag>> resultData = gitlabService.getProjectTags(module.getGitId());
//        if (resultData.successful()) {
//            TagDto dto;
//            List<org.gitlab4j.api.models.Tag> tags = resultData.getData();
//            for (org.gitlab4j.api.models.Tag tag : tags) {
//                dto = new TagDto();
//                dto.setName(tag.getName());
//                dto.setMessage(tag.getMessage());
//                dto.setRelease(Objects.nonNull(tag.getRelease()));
//                tagList.add(dto);
//            }
//        }
    }

    /**
     * 创建标签
     *
     * @param request 创建标签请求
     * @return 创建结果
     */
    public ResultData<TagDto> createTag(CreateTagRequest request) {
        ResultData<org.gitlab4j.api.models.Tag> resultData = gitlabService.createProjectTag(request.getGitId(), request.getTagName(), request.getBranch(), request.getMessage());
        if (resultData.successful()) {
            org.gitlab4j.api.models.Tag tag = resultData.getData();
            TagDto dto = new TagDto();
            dto.setName(tag.getName());
            dto.setMessage(tag.getMessage());
            dto.setRelease(Objects.nonNull(tag.getRelease()));
            return ResultData.success(dto);
        } else {
            return ResultData.fail(resultData.getMessage());
        }
    }

    /**
     * 删除项目标签
     *
     * @param id id
     * @return 创建结果
     */
    public ResultData<Void> deleteTag(String id) {
        Tag tag = dao.findOne(id);
        if (Objects.isNull(tag)) {
            return ResultData.fail("标签[" + id + "]不存在");
        }
        AppModule module = moduleService.findByProperty(AppModule.CODE_FIELD, tag.getModuleCode());
        if (Objects.isNull(module)) {
            return ResultData.fail("应用模块[" + tag.getModuleCode() + "]不存在.");
        }
        return gitlabService.deleteProjectTag(module.getGitId(), tag.getCode());
    }
}