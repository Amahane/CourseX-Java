package coursex;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.Method;

public class IaaaLogin {
    public static void loginAsync(
        String userName,
        String password,
        FutureCallback<String> callback
    ) {
        var httpClient = HttpAsyncClients.createDefault();
        httpClient.start();
        httpClient.execute(
            SimpleHttpRequests.create(
                Method.GET,
                IaaaLogin.buildRequestUrl(userName, password)
            ),
            new FutureCallback<>() {
                @Override public void completed(SimpleHttpResponse response) {
                    try (httpClient) {
                        callback.completed(
                            IaaaLogin.resolveResponse(response.getBodyText())
                        );
                    } catch (Exception e) { this.failed(e); }
                }
                @Override public void failed(Exception e) {
                    try(httpClient) {
                        callback.failed(e);
                    } catch (Exception ignored) {}
                }
                @Override public void cancelled() {
                    try(httpClient) {
                        callback.failed(new LoginException("登录已取消。"));
                    } catch (Exception ignored) {}
                }
            }
        );
    }

    private static String buildRequestUrl(String userName, String password) {
        return IaaaLogin._iaaaLoginUrl  +
            "&userName=" + userName +
            "&password=" + password +
            "&redirUrl=" + IaaaLogin._iaaaRedirectUrl;
    }

    private static String resolveResponse(String responseBody) throws Exception {
        var prefix = "{\"success\":true,\"token\":\"";
        if (! responseBody.startsWith(prefix))
            throw new LoginException("登录失败，请检查学号和教学网密码输入是否正确。");
        return responseBody.substring(
            prefix.length(),
            prefix.length() + 32);
    }

    private final static String _iaaaLoginUrl    = "https://iaaa.pku.edu.cn/iaaa/oauthlogin.do?appid=blackboard";
    private final static String _iaaaRedirectUrl = "https://course.pku.edu.cn/webapps/bb-sso-bb_bb60/execute/authValidate/campusLogin";
}
