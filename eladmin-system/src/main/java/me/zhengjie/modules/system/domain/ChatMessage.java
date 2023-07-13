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
* @date 2023-06-15
**/
@Entity
@Data
@Table(name="chat_message")
public class ChatMessage implements Serializable {

    @Id
    @Column(name = "`message_id`")
    @ApiModelProperty(value = "消息主键")
    private Long messageId;

    @Column(name = "`message_type`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "消息类型")
    private String messageType;

    @Column(name = "`message_text`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "消息内容")
    private String messageText;

    @Column(name = "`request_ip`",nullable = false)
    @ApiModelProperty(value = "ip")
    private String requestIp;

    @Column(name = "`ip_address`",nullable = false)
    @ApiModelProperty(value = "ip地址")
    private String ipAddress;

    @Column(name = "`username`",nullable = false)
    @ApiModelProperty(value = "用户名")
    private String username;

    @Column(name = "`create_time`")
    @CreationTimestamp
    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    @Column(name = "`message_source`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "消息来源")
    private String messageSource;

    public void copy(ChatMessage source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
