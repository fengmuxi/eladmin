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

import cn.hutool.core.util.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.service.EmailService;
import me.zhengjie.modules.system.service.VerifyService;
import me.zhengjie.utils.Result;
import me.zhengjie.utils.enums.CodeBiEnum;
import me.zhengjie.utils.enums.CodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

/**
 * @author Zheng Jie
 * @date 2018-12-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code")
@Api(tags = "系统：验证码管理")
public class VerifyController {

    private final VerifyService verificationCodeService;
    private final EmailService emailService;

    @PostMapping(value = "/resetEmail")
    @ApiOperation("重置邮箱，发送验证码")
    public ResponseEntity<Object> resetEmail(@RequestParam String email){
        EmailVo emailVo = verificationCodeService.sendEmailCode(email, CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey(),CodeEnum.EMAIL_RESET_EMAIL_CODE.getDescription(), RandomUtil.randomNumbers (6));
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AnonymousPostMapping(value = "/registerEmail")
    @ApiOperation("注册，发送验证码")
    public Result<Object> registerEmail(@RequestParam String email){
        try {
            EmailVo emailVo = verificationCodeService.sendEmailCode(email, CodeEnum.EMAIL_REGISTER_EMAIL_CODE.getKey(), CodeEnum.EMAIL_REGISTER_EMAIL_CODE.getDescription(),RandomUtil.randomNumbers (6));
            emailService.send(emailVo,emailService.find());
            return Result.of("发送成功！可能在垃圾邮件中！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发送失败！请联系管理员！");
        }
    }

    @AnonymousPostMapping(value = "/email/resetPass")
    @ApiOperation("重置密码，发送验证码")
    public ResponseEntity<Object> resetPass(@RequestParam String email){
        EmailVo emailVo = verificationCodeService.sendEmailCode(email, CodeEnum.EMAIL_RESET_PWD_CODE.getKey(), CodeEnum.EMAIL_RESET_PWD_CODE.getDescription(),RandomUtil.randomNumbers (6));
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/validated")
    @ApiOperation("验证码验证")
    public ResponseEntity<Object> validated(@RequestParam String email, @RequestParam String code, @RequestParam Integer codeBi){
        CodeBiEnum biEnum = CodeBiEnum.find(codeBi);
        switch (Objects.requireNonNull(biEnum)){
            case ONE:
                verificationCodeService.validated(CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey() + email ,code);
                break;
            case TWO:
                verificationCodeService.validated(CodeEnum.EMAIL_RESET_PWD_CODE.getKey() + email ,code);
                break;
            case THREE:
                verificationCodeService.validated(CodeEnum.EMAIL_REGISTER_EMAIL_CODE.getKey() + email ,code);
                break;
            default:
                break;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
