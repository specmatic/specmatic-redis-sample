package io.specmatic.redis.example;

import io.specmatic.redis.mock.RedisMock;
import io.specmatic.test.SpecmaticContractTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("contract-tests")
public class ContractTests implements SpecmaticContractTest {
    private static RedisMock redisMock;

    @BeforeAll
    public static void setUp() {
        redisMock = new RedisMock("localhost", 8081);
        redisMock.start();
        setUpRedisExpectations();
    }

    @AfterAll
    public static void tearDown() {
        if (redisMock != null) {
            redisMock.stop();
        }
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
