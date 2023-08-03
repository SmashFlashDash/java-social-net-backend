package team38.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.team38.gatewayservice.GatewayServiceApplication;

@SpringBootTest(classes = GatewayServiceApplication.class)
class GatewayServiceApplicationTests {

	{
		System.setProperty("spring.services.user.url", "http://localhost:8081");
		System.setProperty("spring.services.communications.url", "http://localhost:8082");
		System.setProperty("spring.cors.origin", "http://localhost:8088");
		System.setProperty("spring.servlet.multipart.max-file-size", "5MB");
	}

	@Test
	void contextLoads() {
	}

}
