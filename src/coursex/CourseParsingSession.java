package coursex;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.function.*;

import org.apache.hc.core5.io.*;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.concurrent.*;
import org.apache.hc.client5.http.cookie.*;
import org.apache.hc.client5.http.impl.async.*;
import org.apache.hc.client5.http.async.methods.*;

import org.jsoup.Jsoup;

public class CourseParsingSession {
    public CourseParsingSession(
        CookieStore cookieStore,
        FutureCallback<List<Homework>> callback
    ) {
        this._queue       = new ArrayDeque<>();
        this._visited     = new ArrayList<>();
        this._homeworks   = new ArrayList<>();
        this._callback    = callback;
        this._cookieStore = cookieStore;
    }

    public void start(String courseID) {
        this.traverse(
            String.format(
                CourseParsingSession._courseHomeUrlFormat,
                courseID
            )
        );
    }

    private FutureCallback<SimpleHttpResponse> buildCallback(ModalCloseable httpClient) {
        return this.buildCallback(
            response -> {
                var elements = Jsoup.parse(response.getBodyText()).select("a");
                for (var element : elements) {
                    var href = element.attr("href");
                    if (href.startsWith(CourseParsingSession._homeworkUrlPrefix))
                        CourseParsingSession.this.parseHomeworkUrl(
                            CourseParsingSession._courseUrlBase + href);
                    else if (
                        href.startsWith(CourseParsingSession._courseContentUrlPrefix) &&
                            ! CourseParsingSession.this._visited.contains(href))
                        CourseParsingSession.this._queue.add(href);
                }
                CourseParsingSession.this.traverse(
                    CourseParsingSession.this._queue.pop());
                return true;
            },
            httpClient);
    }

    private FutureCallback<SimpleHttpResponse> buildCallback(
        Function<SimpleHttpResponse, Boolean> completeCallback,
        ModalCloseable httpClient
    ) {
        return new FutureCallback<>() {
            @Override public void completed(SimpleHttpResponse response) {
                try (httpClient) {
                    completeCallback.apply(response);
                    if (CourseParsingSession.this._queue.isEmpty())
                        CourseParsingSession.this._callback.completed(
                            CourseParsingSession.this._homeworks);
                } catch (Exception ignored) {}
            }

            @Override public void failed(Exception e) {
                try (httpClient) {
                    CourseParsingSession.this._callback.failed(e);
                } catch (IOException ignore) {}
            }

            @Override public void cancelled() {
                try (httpClient) {
                    CourseParsingSession.this._callback.cancelled();
                } catch (IOException ignore) {}
            }
        };
    }

    private void traverse(String href) {
        System.out.println("Traversing " + href);
        this._visited.add(href);

        var httpClient = HttpAsyncClients
            .custom()
            .setDefaultCookieStore(this._cookieStore)
            .build();
        httpClient.start();
        httpClient.execute(
            SimpleHttpRequests.create(
                Method.GET,
                CourseParsingSession._courseUrlBase + href
            ),
            this.buildCallback(httpClient)
        );
    }

    private void parseHomeworkUrl(String url) {
        var httpClient = HttpAsyncClients
            .custom()
            .setDefaultCookieStore(this._cookieStore)
            .build();
        httpClient.start();
        httpClient.execute(
            SimpleHttpRequests.create(Method.GET, url),
            this.buildCallback(response -> {
                var document = Jsoup.parse(response.getBodyText());
                this._homeworks.add(
                    document.title().startsWith("上载作业") ?
                        new Homework(
                            document.title().substring("上载作业： ".length()),
                            document.select("#crumb_1").text(),
                            url,
                            this.parseDateTime(
                                document.select(".metaField").text()
                            ),
                            false,
                            "详细信息请打开网页查看。",
                            "Not Scored",
                            document.select(".metaField").next().text()
                        ) :
                        new Homework(
                            document.title().substring("复查提交历史记录: ".length()),
                            document.select("#crumb_1").text(),
                            url,
                            this.parseDateTime(
                                document
                                    .select("#assignmentInfo")
                                    .select("p")
                                    .next()
                                    .text()
                            ),
                            true,
                            "详细信息请打开网页查看。",
                            document
                                .select("#aggregateGrade")
                                .attr("value"),
                            document
                                .select("#aggregateGrade_pointsPossible")
                                .text()
                        )
                );
                return true;
            },
            httpClient)
        );
    }

    private Date parseDateTime(String s) {
        try {
            return DateFormat.getDateTimeInstance().parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private CookieStore _cookieStore;
    private ArrayDeque<String>   _queue;
    private ArrayList <String>   _visited;
    private ArrayList <Homework> _homeworks;
    private final FutureCallback<List<Homework>> _callback;

    private static final String _courseUrlBase          = "https://course.pku.edu.cn";
    private static final String _courseHomeUrlFormat    = "/webapps/blackboard/execute/announcement?method=search&context=course_entry&course_id=%s&handle=announcements_entry&mode=view";
    private static final String _courseContentUrlPrefix = "/webapps/blackboard/content/listContent.jsp";
    private static final String _homeworkUrlPrefix      = "/webapps/assignment/uploadAssignment";
}
