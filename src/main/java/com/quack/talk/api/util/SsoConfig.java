package com.quack.talk.api.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SsoConfig {

    protected Logger logger = LoggerFactory.getLogger(SsoConfig.class);

    private final RestTemplate template;

    @Value("${sso.url}")
    private String ssoUrl;

    /**
     * 유저 정보
     *
     * @param request
     * @return
     */
    public Map<String, Object> getUser(HttpServletRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", request.getHeader("Authorization"));
            HttpEntity<String> apiRequest = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> result = template.exchange(
                    ssoUrl + "/users", HttpMethod.GET, apiRequest,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            return result.getBody();
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            return null;
        }
    }

    /**
     * 유저 정보 By Id
     *
     * @param id
     * @return
     */
    public Map<String, Object> getUserById(String id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> apiRequest = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> result = template.exchange(
                    ssoUrl + "/users/find?id=" + id, HttpMethod.POST, apiRequest,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            return result.getBody();
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            return null;
        }
    }

    /**
     * 유저 정보 By Email
     *
     * @param email
     * @return
     */
    public Map<String, Object> getUserByEmail(String email) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> apiRequest = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> result = template.exchange(
                    ssoUrl + "/sign/getIdbyEmail?email=" + email, HttpMethod.GET, apiRequest,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            return result.getBody();
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            return null;
        }
    }

    /**
     * 유저 정보 By Phone
     *
     * @param phone
     * @return
     */
    public List<Map<String, Object>> getUserByPhone(String phone) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> apiRequest = new HttpEntity<>(headers);
            ResponseEntity<List<Map<String, Object>>> result = template.exchange(
                    ssoUrl + "/sign/getIdbyPhone?phone=" + phone, HttpMethod.GET, apiRequest,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            return result.getBody();
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            return new ArrayList<>();
        }
    }
}
