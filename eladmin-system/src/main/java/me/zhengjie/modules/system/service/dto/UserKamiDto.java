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

/**
* @website https://eladmin.vip
* @description /
* @author xdf
* @date 2023-05-31
**/
@Data
public class UserKamiDto implements Serializable {

    /** 主键id */
    private String id;

    /** 卡密号 */
    private String kaMi;

    /** 过期时间 */
    private Timestamp expirationTime;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 是否使用 Y/N */
    private String status;

    /** 使用者id */
    private String useId;

    /** 卡密类型（积分，会员） */
    private String type;

    /** 兑换数量 */
    private Integer number;

    /** 生成数量 */
    private Integer generateNumber;
}