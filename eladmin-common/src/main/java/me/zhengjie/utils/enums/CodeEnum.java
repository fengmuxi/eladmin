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
package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.smartcardio.Card;

/**
 * <p>
 * 验证码业务场景对应的 Redis 中的 key
 * </p>
 * @author Zheng Jie
 * @date 2020-05-02
 */
@Getter
@AllArgsConstructor
public enum CodeEnum {

    /* 通过手机号码重置邮箱 */
    PHONE_RESET_EMAIL_CODE("phone_reset_email_code_", "您正在通过手机号码重置邮箱，您的验证码为："),

    /* 通过旧邮箱重置邮箱 */
    EMAIL_RESET_EMAIL_CODE("email_reset_email_code_", "您正在通过旧邮箱重置邮箱，您的验证码为："),

    /* 通过旧邮箱重置邮箱 */
    EMAIL_REGISTER_EMAIL_CODE("email_register_email_code_", "您正在注册新用户，您的验证码为："),

    /* 通过手机号码重置密码 */
    PHONE_RESET_PWD_CODE("phone_reset_pwd_code_", "您正在通过手机号码重置密码，您的验证码为："),

    /* 通过邮箱重置密码 */
    EMAIL_RESET_PWD_CODE("email_reset_pwd_code_", "您正在通过邮箱重置密码，您的验证码为："),

    /* 通过邮箱重置密码发送新密码 */
    EMAIL_RESET_PWD("email_reset_pwd_", "您正在通过邮箱重置密码，您的新密码为："),

    /* 兑换会员卡密 */
    REDEEM_MEMBER_CARD_SECRET("redeem_member_card_secret_", "您正在兑换会员卡密："),

    /* 兑换积分卡密 */
    REDEMPTION_POINTS_CARD_SECRET("redemption_points_card_secret_", "您正在兑换积分卡密：");

    private final String key;
    private final String description;
}
