package com.zjh.contentcenter.domain.entity.content;

import java.util.Date;
import javax.persistence.*;

@Table(name = "notice")
public class Notice {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否显示 0:否 1:是
     */
    @Column(name = "show_flag")
    private Boolean showFlag;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 获取id
     *
     * @return id - id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取内容
     *
     * @return content - 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取是否显示 0:否 1:是
     *
     * @return show_flag - 是否显示 0:否 1:是
     */
    public Boolean getShowFlag() {
        return showFlag;
    }

    /**
     * 设置是否显示 0:否 1:是
     *
     * @param showFlag 是否显示 0:否 1:是
     */
    public void setShowFlag(Boolean showFlag) {
        this.showFlag = showFlag;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}