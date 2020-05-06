package coursex;

import java.util.*;
import java.util.function.Function;

import javafx.event.Event;
import org.apache.hc.client5.http.cookie.*;
import org.apache.hc.core5.concurrent.FutureCallback;

public class MultipleCourseParsingSession {
    public MultipleCourseParsingSession(
        CookieStore      cookieStore,
        Iterable<String> courseIDs,
        FutureCallback<List<Homework>> callback
    ) {
        this._callback = callback;
        this._homeworks = new ArrayList<>();
        this._cookieStore = cookieStore;
        this._courseIDIterator = courseIDs.iterator();
    }

    public void start() {
        this.parseNext();
    }

    private void parseNext() {
        if (this._courseIDIterator.hasNext())
        {
            var session = new CourseParsingSession(
                this._cookieStore,
                this.buildCallback()
            );
            session.start(this._courseIDIterator.next());
        }
        else this._callback.completed(this._homeworks);
    }

    private FutureCallback<List<Homework>> buildCallback() {
        return new FutureCallback<>() {
            @Override public void completed(List<Homework> homeworks) {
                MultipleCourseParsingSession.this._homeworks.addAll(homeworks);
                MultipleCourseParsingSession.this.parseNext();
            }

            @Override public void failed(Exception e) {
                MultipleCourseParsingSession.this._callback.failed(e);
            }

            @Override public void cancelled() {
                MultipleCourseParsingSession.this._callback.cancelled();
            }
        };
    }

    private CookieStore         _cookieStore;
    private Iterator<String>    _courseIDIterator;
    private ArrayList<Homework> _homeworks;
    private FutureCallback<List<Homework>> _callback;
}
