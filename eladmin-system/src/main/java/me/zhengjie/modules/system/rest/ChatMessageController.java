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
import me.zhengjie.modules.system.domain.ChatMessage;
import me.zhengjie.modules.system.service.ChatMessageService;
import me.zhengjie.modules.system.service.dto.ChatMessageQueryCriteria;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://eladmin.vip
* @author xdf
* @date 2023-06-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "chat消息记录管理")
@RequestMapping("/api/chatMessage")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('chatMessage:list')")
    public void exportChatMessage(HttpServletResponse response, ChatMessageQueryCriteria criteria) throws IOException {
        chatMessageService.download(chatMessageService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询chat消息记录")
    @ApiOperation("查询chat消息记录")
    @PreAuthorize("@el.check('chatMessage:list')")
    public ResponseEntity<Object> queryChatMessage(ChatMessageQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(chatMessageService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增chat消息记录")
    @ApiOperation("新增chat消息记录")
    @PreAuthorize("@el.check('chatMessage:add')")
    public ResponseEntity<Object> createChatMessage(@Validated @RequestBody ChatMessage resources, HttpServletRequest request){
        return new ResponseEntity<>(chatMessageService.create(resources,request),HttpStatus.CREATED);
    }

    @PostMapping("/addChatMessage")
    @Log("新增chat消息记录")
    @ApiOperation("新增chat消息记录")
    public Result<Object> addChatMessage(@Validated @RequestBody ChatMessage resources,HttpServletRequest request){
        return chatMessageService.addChatMessage(resources,request);
    }

    @PutMapping
    @Log("修改chat消息记录")
    @ApiOperation("修改chat消息记录")
    @PreAuthorize("@el.check('chatMessage:edit')")
    public ResponseEntity<Object> updateChatMessage(@Validated @RequestBody ChatMessage resources){
        chatMessageService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除chat消息记录")
    @ApiOperation("删除chat消息记录")
    @PreAuthorize("@el.check('chatMessage:del')")
    public ResponseEntity<Object> deleteChatMessage(@RequestBody Long[] ids) {
        chatMessageService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}