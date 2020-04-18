package com.zjh.contentcenter.dao.content;

import com.zjh.contentcenter.domain.entity.content.Share;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhaojh
 */
public interface ShareMapper extends Mapper<Share> {
    /**
     * 通过title查询
     *
     * @param title
     * @return
     */
    List<Share> selectByParam(@Param("title") String title);

}