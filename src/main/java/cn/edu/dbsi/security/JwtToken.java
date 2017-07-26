package cn.edu.dbsi.security;

import cn.edu.dbsi.model.User;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/7/26.
 */
public interface JwtToken {
    String getUsernameFromToken(String token);

    Date getCreatedDateFromToken(String token);

    Date getExpirationDateFromToken(String token);

    Claims getClaimsFromToken(String token);

    Date generateExpirationDate();

    Boolean isTokenExpired(String token);

    Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset);

    String generateToken(User userDetails);

    String generateToken(Map<String, Object> claims);

    Boolean canTokenBeRefreshed(String token, Date lastPasswordReset);

    String refreshToken(String token);

    Boolean validateToken(String token, String id);
}
