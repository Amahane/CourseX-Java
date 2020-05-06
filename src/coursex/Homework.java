package coursex;

import java.util.Date;

public class Homework {
    public final Date    due;
    public final String  url;
    public final String  name;
    public final String  score;
    public final String  courseName;
    public final String  scoreTotal;
    public final String  description;
    public final boolean isCompleted;

    public Homework(
        String  name,
        String  courseName,
        String  url,
        Date    due,
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
