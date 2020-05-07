package coursex;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class Mock {
    public static final List<Homework> homeworks = Arrays.asList(
        new Homework(
            "论文：游戏测评80w字",
            "P大电竞通论",
            "http://localhost:8080/",
            LocalDateTime.of(2020, Month.JUNE, 1, 23, 59, 0),
            true,
            "自购 RTX2080",
            "Not Scored",
            "100"
        ),
        new Homework(
            "Lab: Attack Autolab",
            "ICS2020",
            "http://localhost:8080/",
            LocalDateTime.of(2020, Month.JUNE, 1, 23, 59, 0),
            true,
            "和室友合作",
            "Not Scored",
            "100"
        ),
        new Homework(
            "大作业：证明黎曼猜想",
            "退学分析 (III)",
            "http://localhost:8080/",
            LocalDateTime.of(2020, Month.JUNE, 1, 23, 59, 0),
            true,
            "严禁与外校同学交流",
            "Not Scored",
            "100"
        ),
        new Homework(
            "论文：地球猫猫教降临派大清洗事件",
            "喵星近代史纲要",
            "http://localhost:8080/",
            LocalDateTime.of(2020, Month.JUNE, 1, 23, 59, 0),
            true,
            "严禁出现煽动猫狗仇恨的语言",
            "Not Scored",
            "100"
        ),
        new Homework(
            "拍摄并上传太极足视频",
            "太极足",
            "http://localhost:8080/",
            LocalDateTime.of(2020, Month.JUNE, 1, 23, 59, 0),
            true,
            "上传视频请及时过审",
            "Not Scored",
            "100"
        ),
        new Homework(
            "论文: 地震风险评估报告",
            "理财概论",
            "http://localhost:8080/",
            LocalDateTime.of(2020, Month.JUNE, 1, 23, 59, 0),
            true,
            "理财合同一并下发",
            "Not Scored",
            "100"
        )
    );
}
