package coursex;

import java.time.*;
import java.util.*;

public class Homework {
    public final String  url;
    public final String  name;
    public final String  score;
    public final String  courseName;
    public final String  scoreTotal;
    public final String  description;
    public final boolean isCompleted;
    public final LocalDateTime due;
    public final List<String>  tags;

    public Homework(
        String  name,
        String  courseName,
        String  description,
        String  url,
        LocalDateTime due,
        boolean isCompleted,
        String  score,
        String  scoreTotal
    ) {
        this(
            name,
            courseName,
            description,
            url,
            due,
            isCompleted,
            score,
            scoreTotal,
            new ArrayList<>()
        );
    }

    public Homework(
        String  name,
        String  courseName,
        String  description,
        String  url,
        LocalDateTime due,
        boolean isCompleted,
        String  score,
        String  scoreTotal,
        List<String> tags
    ) {
        this.due = due;
        this.url = url;
        this.name = name;
        this.tags = new ArrayList<>(tags);
        this.score = score;
        this.courseName = courseName;
        this.scoreTotal = scoreTotal;
        this.description = description;
        this.isCompleted = isCompleted;
    }
}
