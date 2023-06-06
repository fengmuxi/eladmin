/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.modules.system.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://eladmin.vip
* @description /
* @author xdf
* @date 2023-05-31
**/
@Entity
@Data
@Table(name="user_kami")
public class UserKami implements Serializable {

    @Id
    @Column(name = "`id`")
    @ApiModelProperty(value = "主键id")
    private String id;

    @Column(name = "`ka_mi`")
    @ApiModelProperty(value = "卡密号")
    private String kaMi;

    @Column(name = "`expiration_time`",nullable = false)
    @NotNull
    @CreationTimestamp
    @ApiModelProperty(value = "过期时间")
    private Timestamp expirationTime;

    @Column(name = "`create_time`")
    @CreationTimestamp
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "`update_time`")
    @UpdateTimestamp
    @ApiModelProperty(value = "更新时间")
    private Timestamp updateTime;

    @Column(name = "`status`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "是否使用 Y/N")
    private String status;

    @Column(name = "`use_id`")
    @ApiModelProperty(value = "使用者id")
    private Long useId;

    @Column(name = "`type`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "卡密类型（积分，会员）")
    private String type;

    @Column(name = "`number`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "兑换数量")
    private Integer number;

    public void copy(UserKami source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
