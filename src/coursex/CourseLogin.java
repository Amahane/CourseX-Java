package coursex;

import java.util.Random;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.concurrent.*;
import org.apache.hc.client5.http.cookie.*;
import org.apache.hc.client5.http.impl.async.*;
import org.apache.hc.client5.http.async.methods.*;

public class CourseLogin {
    public static void loginAsync(String token, FutureCallback<CookieStore> callback) {
        var cookieStore = new BasicCookieStore();
        var httpClient  = HttpAsyncClients
            .custom()
            .setDefaultCookieStore(cookieStore)
            .build();
        httpClient.start();
        httpClient.execute(
            SimpleHttpRequests.create(
                Method.GET,
                CourseLogin.buildRequestUrl(token)),
            new FutureCallback<>() {
                @Override public void completed(SimpleHttpResponse response) {
                    try(httpClient) {
                        callback.completed(cookieStore);
                    } catch (Exception ignored) {}
                }
                @Override public void failed(Exception e) {
                    try(httpClient) {
                        callback.failed(e);
                    } catch (Exception ignored) {}
                }
                @Override public void cancelled() {
                    try(httpClient) {
                        callback.failed(new ApplicationException("登录已取消。"));
                    } catch (Exception ignored) {}
                }
            }
        );
    }

    private static String buildRequestUrl(String token) {
        return CourseLogin._courseLoginUrl  +
            "?_rand=" + new Random().nextFloat() +
            "&token=" + token;
    }

    private final static String _courseLoginUrl = "https://course.pku.edu.cn/webapps/bb-sso-bb_bb60/execute/authValidate/campusLogin";
}
