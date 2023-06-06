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
package me.zhengjie.modules.system.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.UserKami;
import me.zhengjie.modules.system.service.UserKamiService;
import me.zhengjie.modules.system.service.dto.UserKamiDto;
import me.zhengjie.modules.system.service.dto.UserKamiQueryCriteria;
import me.zhengjie.utils.Result;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://eladmin.vip
* @author xdf
* @date 2023-05-31
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "卡密管理")
@RequestMapping("/api/userKami")
public class UserKamiController {

    private final UserKamiService userKamiService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('userKami:list')")
    public void exportUserKami(HttpServletResponse response, UserKamiQueryCriteria criteria) throws IOException {
        userKamiService.download(userKamiService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询卡密")
    @ApiOperation("查询卡密")
    @PreAuthorize("@el.check('userKami:list')")
    public ResponseEntity<Object> queryUserKami(UserKamiQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(userKamiService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增卡密")
    @ApiOperation("新增卡密")
    @PreAuthorize("@el.check('userKami:add')")
    public ResponseEntity<Object> createUserKami(@Validated @RequestBody UserKamiDto resources){
        return userKamiService.create(resources);
    }

    @PutMapping
    @Log("修改卡密")
    @ApiOperation("修改卡密")
    @PreAuthorize("@el.check('userKami:edit')")
    public ResponseEntity<Object> updateUserKami(@Validated @RequestBody UserKami resources){
        userKamiService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除卡密")
    @ApiOperation("删除卡密")
    @PreAuthorize("@el.check('userKami:del')")
    public ResponseEntity<Object> deleteUserKami(@RequestBody String[] ids) {
        userKamiService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delExpirationKaMi")
    @Log("删除过期卡密")
    @ApiOperation("删除过期卡密")
    @PreAuthorize("@el.check('userKami:del')")
    public ResponseEntity<Object> delExpirationKaMi() {
        userKamiService.delExpirationKaMi();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/exchangeKaMi")
    @Log("兑换卡密")
    @ApiOperation("兑换卡密")
    public Result<String> exchangeKaMi(String kaMi){
        return userKamiService.exchangeKaMi(kaMi);
    }
}