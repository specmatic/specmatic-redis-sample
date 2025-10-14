package io.specmatic.redis.example;

import io.specmatic.redis.VersionInfo;
import io.specmatic.redis.mock.RedisMock;
import io.specmatic.stub.ContractStub;
import io.specmatic.test.SpecmaticContractTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static io.specmatic.stub.API.createStub;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ContractTests implements SpecmaticContractTest {
    private static final String LOCALHOST = "localhost";
    private static final int SPECMATIC_STUB_PORT = 9000;
    private static final String APP_PORT = "8080";
    private static final int REDIS_PORT = 8081;
    private static ContractStub stub;
    private static RedisMock redisMock;

    @BeforeAll
    public static void setUp() {
        System.out.println("Using specmatic redis " + VersionInfo.INSTANCE.describe());
        System.setProperty("host", LOCALHOST);
        System.setProperty("port", APP_PORT);
        System.setProperty("endpointsAPI", "http://localhost:8080/actuator/mappings");
        System.setProperty("spring.profiles.active", "contract-tests");
        startRedisStub();
        stub = createStub(LOCALHOST, SPECMATIC_STUB_PORT);
    }

    @AfterAll
    public static void tearDown() throws IOException {
        if (stub != null) {
            stub.close();
        }
        if (redisMock != null) {
            redisMock.stop();
        }
    }

    private static void startRedisStub() {
        redisMock = new RedisMock(LOCALHOST, REDIS_PORT);
        redisMock.start();
        setUpRedisExpectations();
    }

    private static void setUpRedisExpectations() {
        redisMock
                .when("get")
                .with(new String[]{"Description-1"})
                .thenReturnString("Grocery store with free home delivery!");
        redisMock
                .when("rpush")
                .with(new String[]{"Products-1", "iPhone 12"})
                .thenReturnLong(2);
        redisMock
                .when("zrevrange")
                .with(new String[]{"Stores-1-Products-2", "0", "(string)"})
                .thenReturnArray(new String[]{"Powder", "Soap"});
    }

}
