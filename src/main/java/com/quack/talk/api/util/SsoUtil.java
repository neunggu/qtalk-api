package com.quack.talk.api.util;

import com.quack.talk.api.util.vo.SsoUser;
import com.quack.talk.common.friend.entity.Friend;
import com.quack.talk.common.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@RequiredArgsConstructor
public class SsoUtil {

    protected Logger logger = LoggerFactory.getLogger(SsoUtil.class);
    private final ApiUtil apiUtil;

    @Value("${sso.url}")
    private String ssoUrl;

    public Friend getMyinfo(String accessToken){
        String url = ssoUrl + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        SsoUser ssoUser = apiUtil.get(url, SsoUser.class, headers);
        return ssoUserToFriend(ssoUser);
    }

    public Friend search(String keyword, SearchType type) {
        String url = null;
        switch (type){
            case EMAIL:
                url = ssoUrl+"/sign/getIdbyEmail?email="+keyword;
                break;
            case PHONE:
                url = ssoUrl+"/sign/getIdbyPhone?phone="+keyword;
                break;
            case QRCODE:
                url = ssoUrl+"/sign/getIdbyQrcode?qrcode="+keyword;
                break;
            default:
                break;
        }
        SsoUser ssoUser = apiUtil.get(url, SsoUser.class);
        return ssoUserToFriend(ssoUser);
    }


    private Friend ssoUserToFriend(SsoUser ssoUser){
        if (ObjectUtils.isEmpty(ssoUser)) return null;
        return new Friend(
                ssoUser.getId(),
                ssoUser.getPhone(),
                ssoUser.getOrigin_name(),
                ssoUser.getName(),
                ssoUser.getImgKey(),
                ssoUser.getEmail(),
                ssoUser.getPush_token(),
                0,
                0
        );
    }


}
