package com.longjiang.constants;

public class DiscussPostConstants {
    public static final String MAPPING_TEMPLATE="{\n" +
            "\t\t\t\"mappings\": {\n" +
            "\t\t\t\t\"properties\": {\n" +
            "\t\t\t\t\t\"id\": {\n" +
            "\t\t\t\t\t\t\"type\": \"integer\",\n" +
            "\t\t\t\t\t\t\"index\": \"false\"\n" +
            "\t\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"userId\": {\n" +
            "\t\t\t\t\t\t\"type\": \"integer\",\n" +
            "\t\t\t\t\t\t\"index\": \"false\"\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"title\": {\n" +
            "\t\t\t\t\t\t\"type\": \"text\",\n" +
            "\t\t\t\t\t\t\"analyzer\": \"ik_smart\"\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"content\": {\n" +
            "\t\t\t\t\t\t\"type\": \"text\",\n" +
            "\t\t\t\t\t\t\"analyzer\": \"ik_smart\"\n" +
            "\t\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"type\": {\n" +
            "\t\t\t\t\t\t\"type\": \"integer\",\n" +
            "\t\t\t\t\t\t\"index\": \"false\"\n" +
            "\t\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"status\": {\n" +
            "\t\t\t\t\t\t\"type\": \"integer\",\n" +
            "\t\t\t\t\t\t\"index\": \"false\"\n" +
            "\t\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"createTime\": {\n" +
            "\t\t\t\t\t\t\"type\": \"date\",\n" +
            "\t\t\t\t\t\t\"index\": \"false\"\n" +
            "\t\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"commentCount\": {\n" +
            "\t\t\t\t\t\t\"type\": \"integer\",\n" +
            "\t\t\t\t\t\t\"index\": \"false\"\n" +
            "\t\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"score\": {\n" +
            "\t\t\t\t\t\t\"type\": \"double\",\n" +
            "\t\t\t\t\t\t\"index\": \"false\"\n" +
            "\t\t\t\t\t\t}\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}\n" +
            "\t\t}\n";
}
