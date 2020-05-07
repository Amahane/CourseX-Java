package coursex;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

import org.apache.hc.core5.io.*;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.concurrent.*;
import org.apache.hc.client5.http.cookie.*;
import org.apache.hc.client5.http.impl.async.*;
import org.apache.hc.client5.http.async.methods.*;

import org.jsoup.Jsoup;

public class CourseFetchSession {
    public CourseFetchSession(
        CookieStore cookieStore,
        Iterable<String> courseIDs,
        FutureCallback<Homework> callback
    ) {
        this._queue       = new ArrayDeque<>();
        this._visited     = new ArrayList<>();
        this._callback    = callback;
        this._cookieStore = cookieStore;

        for (var courseID : courseIDs)
            this._queue.push(
                String.format(
                    CourseFetchSession._courseHomeUrlFormat,
                    courseID
                )
            );
    }

    public void start() {
        this.traverseNext();
    }

    private FutureCallback<SimpleHttpResponse> buildCallback(ModalCloseable httpClient) {
        return this.buildCallback(
            response -> {
                var elements = Jsoup.parse(response.getBodyText()).select("a");
                for (var element : elements) {
                    var href = element.attr("href");
                    if (href.startsWith(CourseFetchSession._homeworkUrlPrefix))
                        CourseFetchSession.this.parseHomeworkUrl(
                            CourseFetchSession._courseUrlBase + href);
                    else if (
                        href.startsWith(CourseFetchSession._courseContentUrlPrefix) &&
                        ! CourseFetchSession.this._visited.contains(href) &&
                        ! CourseFetchSession.this._queue  .contains(href))
                        CourseFetchSession.this._queue.push(href);
                }
                CourseFetchSession.this.traverseNext();
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
                } catch (Exception ignored) {}
            }

            @Override public void failed(Exception e) {
                try (httpClient) {
                    CourseFetchSession.this._callback.failed(e);
                } catch (IOException ignore) {}
            }

            @Override public void cancelled() {
                try (httpClient) {
                    CourseFetchSession.this._callback.cancelled();
                } catch (IOException ignore) {}
            }
        };
    }

    private void traverseNext() {
        var node = CourseFetchSession.this._queue.pop();
        System.out.println("Traversing: " + node);
        this._visited.add(node);

        var httpClient = HttpAsyncClients
            .custom()
            .setDefaultCookieStore(this._cookieStore)
            .build();
        httpClient.start();
        httpClient.execute(
            SimpleHttpRequests.create(
                Method.GET,
                CourseFetchSession._courseUrlBase + node
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
                    var courseName = document.select("#crumb_1").text();
                    var description = "详细信息请打开网页查看。";
                    this._callback.completed(
                        document.title().startsWith("上载作业") ?
                            new Homework(
                                document.title().substring(
                                    "上载作业： ".length(),
                                    document.title().length() - courseName.length() - 3
                                ),
                                courseName,
                                description,
                                url,
                                this.parseDateTime(
                                    document.select(".metaField").first().text()
                                ),
                                false,
                                "0",
                                document.select(".metaField").last().text()
                            ) :
                            new Homework(
                                document.title().substring(
                                    "复查提交历史记录: ".length(),
                                    document.title().length() - courseName.length() - 3
                                ),
                                courseName,
                                description,
                                url,
                                this.parseDateTime(
                                    document
                                        .select("#assignmentInfo")
                                        .select("p")
                                        .last()
                                        .text()
                                ),
                                true,
                                document
                                    .select("#aggregateGrade")
                                    .attr("value"),
                                document
                                    .select("#aggregateGrade_pointsPossible")
                                    .text()
                                    .substring(1)
                            )
                    );
                    return true;
                },
                httpClient)
        );
    }

    private LocalDateTime parseDateTime(String s) {
        var dateScanner = new Scanner(s
            .replace('年', ' ')
            .replace('月', ' ')
            .replace('日', ' '));
        var year  = dateScanner.nextInt();
        var month = dateScanner.nextInt();
        var day   = dateScanner.nextInt();
        var hour  = Integer.parseInt(s.substring(
            s.length() - 5,
            s.length() - 3
        ));
        var minute = Integer.parseInt(s.substring(s.length() - 2));
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    private CookieStore _cookieStore;
    private ArrayDeque<String>   _queue;
    private ArrayList <String>   _visited;
    private final FutureCallback<Homework> _callback;

    private static final String _courseUrlBase          = "https://course.pku.edu.cn";
    private static final String _courseHomeUrlFormat    = "/webapps/blackboard/execute/announcement?method=search&context=course_entry&course_id=%s&handle=announcements_entry&mode=view";
    private static final String _courseContentUrlPrefix = "/webapps/blackboard/content/listContent.jsp";
    private static final String _homeworkUrlPrefix      = "/webapps/assignment/uploadAssignment";
}
