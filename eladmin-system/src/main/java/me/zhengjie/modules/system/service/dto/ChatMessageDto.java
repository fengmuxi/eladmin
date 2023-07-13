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
package me.zhengjie.modules.system.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;

/**
* @website https://eladmin.vip
* @description /
* @author xdf
* @date 2023-06-15
**/
@Data
public class ChatMessageDto implements Serializable {

    /** 消息主键 */
    /** 防止精度丢失 */
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long messageId;

    /** 消息类型 */
    private String messageType;

    /** 消息内容 */
    private String messageText;

    /** ip */
    private String requestIp;

    /** ip地址 */
    private String ipAddress;

    /** 用户名 */
    private String username;
    /** 消息来源 */
    private String messageSource;

    private Timestamp createTime;
}