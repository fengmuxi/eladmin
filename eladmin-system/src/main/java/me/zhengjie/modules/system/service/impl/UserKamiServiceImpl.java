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
package me.zhengjie.modules.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.domain.UserKami;
import me.zhengjie.modules.system.repository.UserRepository;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.modules.system.service.mapstruct.UserMapper;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.system.repository.UserKamiRepository;
import me.zhengjie.modules.system.service.UserKamiService;
import me.zhengjie.modules.system.service.dto.UserKamiDto;
import me.zhengjie.modules.system.service.dto.UserKamiQueryCriteria;
import me.zhengjie.modules.system.service.mapstruct.UserKamiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://eladmin.vip
* @description 服务实现
* @author xdf
* @date 2023-05-31
**/
@Service
@RequiredArgsConstructor
public class UserKamiServiceImpl implements UserKamiService {

    private final UserKamiRepository userKamiRepository;
    private final UserKamiMapper userKamiMapper;
    @Value("${spring.kaMiPassword}")
    private String kaMiPassword;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<String,Object> queryAll(UserKamiQueryCriteria criteria, Pageable pageable){
        Page<UserKami> page = userKamiRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(userKamiMapper::toDto));
    }

    @Override
    public List<UserKamiDto> queryAll(UserKamiQueryCriteria criteria){
        return userKamiMapper.toDto(userKamiRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public UserKamiDto findById(String id) {
        UserKami userKami = userKamiRepository.findById(id).orElseGet(UserKami::new);
        ValidationUtil.isNull(userKami.getId(),"UserKami","id",id);
        return userKamiMapper.toDto(userKami);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> create(UserKamiDto resources) {
        try {
            for (int i = 0; i < resources.getGenerateNumber(); i++) {
                UserKami userKami = userKamiMapper.toEntity(resources);
                userKami.setId(IdUtil.simpleUUID());
                userKami.setStatus("N");
                userKami.setKaMi(RedeemUtil.create((byte)1,1,16,kaMiPassword).get(0));
                userKamiRepository.save(userKami);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserKami resources) {
        UserKami userKami = userKamiRepository.findById(resources.getId()).orElseGet(UserKami::new);
        ValidationUtil.isNull( userKami.getId(),"UserKami","id",resources.getId());
        userKami.copy(resources);
        userKamiRepository.save(userKami);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(String[] ids) {
        for (String id : ids) {
            userKamiRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<UserKamiDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserKamiDto userKami : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("卡密类型（积分，会员）", userKami.getType());
            map.put("卡密号", userKami.getKaMi());
            map.put("过期时间", userKami.getExpirationTime());
            map.put("创建时间", userKami.getCreateTime());
            map.put("更新时间", userKami.getUpdateTime());
            map.put("是否使用 Y/N", userKami.getStatus());
            map.put("使用者id", userKami.getUseId());
            map.put("兑换数量", userKami.getNumber());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delExpirationKaMi() {
        userKamiRepository.delExpirationKaMi(DateUtil.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> exchangeKaMi(String kaMi) {
        try {
            UserKami byKami = userKamiRepository.getByKami(kaMi);
            if (ObjectUtil.isEmpty(byKami)){
                return Result.error("卡密信息错误！");
            }
            if (DateUtil.compare(byKami.getExpirationTime(),DateUtil.date())<0) {
                return Result.error("卡密已过期！");
            }
            if ("Y".equals(byKami.getStatus())) {
                return Result.error("卡密已使用！");
            }
            if (!RedeemUtil.VerifyCode(kaMi)) {
                return Result.error("非法卡密！");
            }
            User byId = userRepository.getById(SecurityUtils.getCurrentUserId());
            if ("会员".equals(byKami.getType())) {
                if (DateUtil.compare(byId.getVipTime(),DateUtil.date())>0) {
                    byId.setVipTime(me.zhengjie.utils.DateUtil.getDateAddDay(byId.getVipTime(),byKami.getNumber()));
                }else {
                    byId.setVipTime(me.zhengjie.utils.DateUtil.getNowDateAddDay(byKami.getNumber()));
                }
            }else {
                byId.setWallet(byId.getWallet()+byKami.getNumber());
            }
            byKami.setStatus("Y");
            byKami.setUseId(byId.getId());
            userKamiRepository.save(byKami);
            userRepository.save(byId);
            return Result.of("兑换成功！");
        } catch (Exception e) {
            return Result.error("兑换失败！");
        }
    }
}