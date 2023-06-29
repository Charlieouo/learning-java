import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
public class RedisHttpApplication {

    private static final String REDIS_URL = "http://localhost:6379";

    public static void main(String[] args) {
        SpringApplication.run(RedisHttpApplication.class, args);
    }

    @GetMapping("/string/{key}")
    public String getStringValue(@PathVariable String key) throws IOException {
        String url = REDIS_URL + "/get?key=" + key;
        return sendRequest(url, "GET", null);
    }

    @GetMapping("/string/{key}/{value}")
    public void setStringValue(@PathVariable String key, @PathVariable String value) throws IOException {
        String url = REDIS_URL + "/set?key=" + key + "&value=" + value;
        sendRequest(url, "POST", null);
    }

    @GetMapping("/string/{key}/delete")
    public void deleteStringKey(@PathVariable String key) throws IOException {
        String url = REDIS_URL + "/del?key=" + key;
        sendRequest(url, "POST", null);
    }

    @GetMapping("/list/{key}")
    public List<String> getListValues(@PathVariable String key) throws IOException {
        String url = REDIS_URL + "/lrange?key=" + key + "&start=0&end=-1";
        String response = sendRequest(url, "GET", null);
        return Arrays.asList(response.split(","));
    }

    @GetMapping("/list/{key}/filter")
    public List<String> filterListValues(@PathVariable String key) throws IOException {
        String url = REDIS_URL + "/lrange?key=" + key + "&start=0&end=-1";
        String response = sendRequest(url, "GET", null);
        List<String> listValues = Arrays.asList(response.split(","));
        return listValues.stream()
                .filter(item -> item.contains("value"))
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    @GetMapping("/set/{key}")
    public Set<String> getSetValues(@PathVariable String key) throws IOException {
        String url = REDIS_URL + "/smembers?key=" + key;
        String response = sendRequest(url, "GET", null);
        return new HashSet<>(Arrays.asList(response.split(",")));
    }

    @GetMapping("/set/{key}/filter")
    public Set<String> filterSetValues(@PathVariable String key) throws IOException {
        String url = REDIS_URL + "/smembers?key=" + key;
        String response = sendRequest(url, "GET", null);
        Set<String> setValues = new HashSet<>(Arrays.asList(response.split(",")));
        return setValues.stream()
                .filter(item -> item.startsWith("prefix"))
                .collect(Collectors.toSet());
    }

    private String sendRequest(String url, String method, String requestBody) throws IOException {
        // Same sendRequest method implementation as in the previous code
    }
}

/*
        在这个示例中，使用了@RestController注解标记了RedisHttpApplication类，并定义了一些HTTP接口。
        通过不同的@GetMapping注解，可以定义不同的接口路径和参数，以便执行Redis操作。这些接口方法内部调用了之前的sendRequest方法来发送HTTP请求。

        现在，可以运行这个Spring Boot应用程序，并使用以下URL来访问这些接口：
        获取String值：http://localhost:8080/string/{key}
        设置String值：http://localhost:8080/string/{key}/{value}
        删除String键：http://localhost:8080/string/{key}/delete
        获取List值：http://localhost:8080/list/{key}
        过滤List值：http://localhost:8080/list/{key}/filter
        获取Set值：http://localhost:8080/set/{key}
        过滤Set值：http://localhost:8080/set/{key}/filter
 */