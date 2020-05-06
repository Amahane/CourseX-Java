package coursex;

import java.util.*;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.concurrent.*;
import org.apache.hc.client5.http.cookie.*;
import org.apache.hc.client5.http.impl.async.*;
import org.apache.hc.client5.http.async.methods.*;
import org.jsoup.Jsoup;

public class CourseListParser {
    public static void fetchAndParse(
        CookieStore cookieStore,
        FutureCallback<Map<String, String>> callback
    ) {
        var httpClient  = HttpAsyncClients
            .custom()
            .setDefaultCookieStore(cookieStore)
            .build();
        httpClient.start();
        httpClient.execute(
            SimpleHttpRequests.create(
                Method.GET,
                CourseListParser._courseHomeUrl
            ),
            new FutureCallback<>() {
                @Override public void completed(SimpleHttpResponse response) {
                    try(httpClient) {
                        callback.completed(
                            CourseListParser.parseCourseList(
                                response.getBodyText()));
                    } catch (Exception ignored) {}
                }
                @Override public void failed(Exception e) {
                    try(httpClient) {
                        callback.failed(e);
                    } catch (Exception ignored) {}
                }
                @Override public void cancelled() {
                    try(httpClient) {
                        callback.failed(new ApplicationException("操作已取消。"));
                    } catch (Exception ignored) {}
                }
            }
        );
    }

    private static Map<String, String> parseCourseList(String responseBody) {
        var elements = Jsoup.parse(responseBody).select("a");
        var result   = new HashMap<String, String>();
        for (var element : elements) {
            var courseUrl = element.attr("href").trim();
            if (courseUrl.startsWith(CourseListParser._courseLinkPrefix)
            ) {
                result.put(
                    courseUrl.substring(
                        CourseListParser._courseLinkPrefix.length(),
                        CourseListParser._courseLinkPrefix.length()
                            + CourseListParser._courseIDLength
                    ),
                    element.text()
                );
            }
        }
        return result;
    }

    private static final int    _courseIDLength   = 8;
    private static final String _courseHomeUrl    = "https://course.pku.edu.cn/webapps/portal/execute/tabs/tabAction?tab_tab_group_id=_3_1";
    private static final String _courseLinkPrefix = "/webapps/blackboard/execute/launcher?type=Course&id=";
}
