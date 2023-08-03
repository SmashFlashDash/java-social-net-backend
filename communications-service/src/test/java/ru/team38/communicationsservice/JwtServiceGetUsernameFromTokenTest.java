package ru.team38.communicationsservice;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.team38.communicationsservice.services.JwtService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"yandexObjectStorage.endpoint=https://storage.yandexcloud.net/",
		"yandexObjectStorage.accessKey=YCAJEg9dsfQhSCJAMoJ_i4CK2",
		"yandexObjectStorage.secretKey=YCMMT0nO2JO1tAMqmxFK28KZijMN0C1BRzokJQdB",
		"yandexObjectStorage.bucketName=team38bucket"})
class JwtServiceGetUsernameFromTokenTest {

	{
		System.setProperty("spring.datasource.url", "jdbc:postgresql://${db.postgres.host}/${db.postgres.name}");
		System.setProperty("spring.datasource.username", "${db.postgres.username}");
		System.setProperty("spring.datasource.password", "${db.postgres.password}");
		System.setProperty("spring.servlet.multipart.max-file-size", "5MB");
	}

		@Mock
		private HttpServletRequest request;

		@Mock
		private Cookie cookie;

		@Autowired
		private JwtService jwtService;

		@Value("${jwt.secret}")
		private String jwtSecret;

		private final String username = "testUser";
		private String token;

		@BeforeEach
		public void setUp() {
			token = Jwts.builder()
					.setSubject(username)
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + 1000*60))
					.signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)), SignatureAlgorithm.HS256)
					.compact();
		}

		@Test
		void getUsernameFromTokenInHeader() {
			when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

			String result = jwtService.getUsernameFromToken(request);

			assertEquals(username, result);
		}

		@Test
		void getUsernameFromTokenInCookie() {
			when(request.getHeader("Authorization")).thenReturn(null);
			when(request.getCookies()).thenReturn(new Cookie[]{cookie});

			when(cookie.getName()).thenReturn("token");
			when(cookie.getValue()).thenReturn(token);

			String result = jwtService.getUsernameFromToken(request);

			assertEquals(username, result);
		}
}