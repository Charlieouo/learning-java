import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisHttpExample {
    private static final String REDIS_URL = "http://localhost:6379";

    public static void main(String[] args) {
        try {
            // String类型操作示例
            System.out.println("String类型操作示例：");
            String key = "myKey";
            String value = getStringValue(key);
            System.out.println("Value: " + value);

            setStringValue(key, "Hello, Redis!");
            System.out.println("String value set.");

            value = getStringValue(key);
            System.out.println("New value: " + value);

            deleteKey(key);
            System.out.println("String key deleted.\n");

            // List类型操作示例
            System.out.println("List类型操作示例：");
            String listKey = "myList";
            List<String> listValues = getListValues(listKey);
            System.out.println("List values: " + listValues);

            List<String> filteredList = listValues.stream()
                    .filter(item -> item.contains("value"))
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());
            System.out.println("Filtered and transformed list: " + filteredList + "\n");

            // Set类型操作示例
            System.out.println("Set类型操作示例：");
            String setKey = "mySet";
            Set<String> setValues = getSetValues(setKey);
            System.out.println("Set values: " + setValues);

            Set<String> filteredSet = setValues.stream()
                    .filter(item -> item.startsWith("prefix"))
                    .collect(Collectors.toSet());
            System.out.println("Filtered set: " + filteredSet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送HTTP请求并返回响应的字符串形式
     *
     * @param url         请求的URL
     * @param method      HTTP方法（GET或POST）
     * @param requestBody 请求体内容（仅对POST请求有效）
     * @return 响应的字符串形式
     * @throws IOException 发生IO异常时抛出
     */
    private static String sendRequest(String url, String method, String requestBody) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response;

        if (method.equalsIgnoreCase("GET")) {
            HttpGet httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
        } else if (method.equalsIgnoreCase("POST")) {
            HttpPost httpPost = new HttpPost(url);
            if (requestBody != null) {
                httpPost.setEntity(new StringEntity(requestBody));
            }
            response = httpClient.execute(httpPost);
        } else {
            throw new IllegalArgumentException("Invalid HTTP method.");
        }

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity);
        } else {
            return null;
        }
    }

    /**
     * 获取String类型的值
     *
     * @param key 键名
     * @return 对应键的值
     * @throws IOException 发生IO异常时抛出
     */
    private static String getStringValue(String key) throws IOException {
        String url = REDIS_URL + "/get?key=" + key;
        return sendRequest(url, "GET", null);
    }

    /**
     * 设置String类型的值
     *
     * @param key   键名
     * @param value 键值
     * @throws IOException 发生IO异常时抛出
     */
    private static void setStringValue(String key, String value) throws IOException {
        String url = REDIS_URL + "/set?key=" + key + "&value=" + value;
        sendRequest(url, "POST", null);
    }

    /**
     * 删除键
     *
     * @param key 键名
     * @throws IOException 发生IO异常时抛出
     */
    private static void deleteKey(String key) throws IOException {
        String url = REDIS_URL + "/del?key=" + key;
        sendRequest(url, "POST", null);
    }

    /**
     * 获取List类型的值列表
     *
     * @param key 列表键名
     * @return 列表值的集合
     * @throws IOException 发生IO异常时抛出
     */
    private static List<String> getListValues(String key) throws IOException {
        String url = REDIS_URL + "/lrange?key=" + key + "&start=0&end=-1";
        String response = sendRequest(url, "GET", null);
        return Arrays.asList(response.split(","));
    }

    /**
     * 获取Set类型的值集合
     *
     * @param key Set键名
     * @return Set值的集合
     * @throws IOException 发生IO异常时抛出
     */
    private static Set<String> getSetValues(String key) throws IOException {
        String url = REDIS_URL + "/smembers?key=" + key;
        String response = sendRequest(url, "GET", null);
        return new HashSet<>(Arrays.asList(response.split(",")));
    }
}
