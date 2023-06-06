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
package me.zhengjie.modules.system.repository;

import me.zhengjie.modules.system.domain.UserKami;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
* @website https://eladmin.vip
* @author xdf
* @date 2023-05-31
**/
public interface UserKamiRepository extends JpaRepository<UserKami, String>, JpaSpecificationExecutor<UserKami> {

    @Modifying
    @Query(nativeQuery = true,value = "delete from user_kami where `status`= 'Y' or expiration_time<?1 ")
    void delExpirationKaMi(String now);

    @Query(nativeQuery = true,value = "select * from user_kami where ka_mi = ?1")
    UserKami getByKami(String kaMi);
}