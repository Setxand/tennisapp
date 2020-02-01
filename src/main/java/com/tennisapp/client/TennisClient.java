package com.tennisapp.client;

import com.tennisapp.config.UrlTennis;
import com.tennisapp.dto.LoginForm;
import com.tennisapp.dto.TableModelDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import telegram.Message;

import java.util.Map;

@Component
public class TennisClient {

    private static final int COOKIE_INDEX = 1;
    private static final String COOKIE_PROP = "Set-Cookie";
    private static final String ALLOW_FOUR_PROP = "AllowFour";
    private static final String HIDE_SEATS_PROP = "HideFreeSeats";
    private static final String TABLE_ID_PROP = "TableId";
    private static final String COOKIE_HEADER = "Cookie";

    private final RestTemplate restTemplate;

    private final UrlTennis urlTennis;

    public TennisClient(UrlTennis urlTennis) {
        this.urlTennis = urlTennis;
        this.restTemplate = new RestTemplate();
    }

    public String login(String login, String password) {

        return restTemplate.postForEntity(urlTennis.getLoginUrl(),
                new LoginForm(login, password), Void.class).getHeaders().get(COOKIE_PROP).get(COOKIE_INDEX);
    }

    public void bookTable(Message message, String cookie) {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();

        body.add(ALLOW_FOUR_PROP, false);
        body.add(HIDE_SEATS_PROP, true);
        body.add(TABLE_ID_PROP, 1);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(COOKIE_HEADER, cookie);


        HttpEntity<?> httpEntity = new HttpEntity<Object>(body, httpHeaders);

        ResponseEntity<Boolean> exchange = restTemplate
                .exchange(urlTennis.getBookingUrl(), HttpMethod.POST, httpEntity, Boolean.class);
    }


    public TableModelDto getTableModel(String userAdminCookie) {
        HttpEntity<Object> requestObject = createEntityWithHeaders(null, userAdminCookie);
        return restTemplate
                .exchange(urlTennis.getShowTableUrl(),
                        HttpMethod.GET,
                        requestObject,
                        TableModelDto.class)
                .getBody();
    }

    public Map<String, Object> getProfileId(String cookie) {
        HttpEntity<Object> requestObject = createEntityWithHeaders(null, cookie);

        return restTemplate
                .exchange(urlTennis.getGetProfileUrl(),
                        HttpMethod.GET,
                        requestObject,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        })
                .getBody();
    }

    public void acceptInvite(String loginCookie) {
        HttpEntity<Object> requestBody = createEntityWithHeaders(null, loginCookie);
        restTemplate
                .exchange(urlTennis.getAcceptInvitationUrl(), HttpMethod.POST, requestBody, Void.class);
    }

    private HttpEntity<Object> createEntityWithHeaders(HttpEntity body, String cookie) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(COOKIE_HEADER, cookie);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<Object>(null, httpHeaders);
    }
}
