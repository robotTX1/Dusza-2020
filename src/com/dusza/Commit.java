package com.dusza;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Commit {
    public static final String dateFormat = "YYYY.MM.dd hh:mm:ss";

    private final String parent;
    private final String author;
    private final Date creationDate;
    private final String description;
    private final List<String> changes;

    public Commit(String parent, String author, Date creationDate, String description, List<String> changes) {
        this.parent = parent;
        this.author = author;
        this.creationDate = creationDate;
        this.description = description;
        this.changes = changes;
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }

    public static Date getDateFromString(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getParent() {
        return parent;
    }

    public String getAuthor() {
        return author;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getChanges() {
        return changes;
    }
}
