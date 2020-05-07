package coursex;

import java.time.*;

public class Homework {
    public final String  url;
    public final String  name;
    public final String  score;
    public final String  courseName;
    public final String  scoreTotal;
    public final String  description;
    public final boolean isCompleted;
    public final LocalDateTime due;

    public Homework(
        String  name,
        String  courseName,
        String  url,
        LocalDateTime due,
        boolean isCompleted,
        String  description,
        String  score,
        String  scoreTotal
    ) {
        this.due = due;
        this.url = url;
        this.name = name;
        this.score = score;
        this.courseName = courseName;
        this.scoreTotal = scoreTotal;
        this.description = description;
        this.isCompleted = isCompleted;
    }
}
