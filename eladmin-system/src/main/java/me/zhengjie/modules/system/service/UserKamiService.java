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
package me.zhengjie.modules.system.service;

import me.zhengjie.modules.system.domain.UserKami;
import me.zhengjie.modules.system.service.dto.UserKamiDto;
import me.zhengjie.modules.system.service.dto.UserKamiQueryCriteria;
import me.zhengjie.utils.Result;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://eladmin.vip
* @description 服务接口
* @author xdf
* @date 2023-05-31
**/
public interface UserKamiService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(UserKamiQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<UserKamiDto>
    */
    List<UserKamiDto> queryAll(UserKamiQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return UserKamiDto
     */
    UserKamiDto findById(String id);

    /**
    * 创建
    * @param resources /
    * @return UserKamiDto
    */
    ResponseEntity<Object> create(UserKamiDto resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(UserKami resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(String[] ids);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<UserKamiDto> all, HttpServletResponse response) throws IOException;

    /**
     * 删除过期卡密和已使用
     */
    void delExpirationKaMi();

    /**
     * 兑换卡密
     *
     * @author: xdf
     * @date: 2023/6/3 0:42
     * @Param kaMi
     * @return me.zhengjie.utils.Result<java.lang.String>
     **/
    Result<String> exchangeKaMi(String kaMi);
}