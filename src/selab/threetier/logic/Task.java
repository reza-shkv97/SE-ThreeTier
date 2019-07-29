package selab.threetier.logic;

import selab.threetier.storage.Storage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.SimpleFormatter;

public class Task extends Entity {
    private String title;
    private Date start;
    private Date end;

    public String getTitle() { return title; }
    public void setTitle(String value) { title = value; }

    public void setStart(Date value) { start = value; }
    public String getStartDate() {
        return new SimpleDateFormat("YYYY-MM-DD").format(start);
    }
    public String getStartTime() {
        return new SimpleDateFormat("HH:mm:ss").format(start);
    }

    public void setEnd(Date value) { end = value; }
    public String getEndTime() {
        return new SimpleDateFormat("HH:mm:ss").format(end);
    }

    public void save() {
        Storage.getInstance().getTasks().addOrUpdate(this);
    }

    public void delete() {Storage.getInstance().getTasks().delete(this);}

    public static ArrayList<Task> getAll() {
        return Storage.getInstance().getTasks().getlAllSorted((o1, o2) -> {
            if (o1.start.before(o2.start))
                return -1;
            else if (o1.start.equals(o2.start))
                return 0;
            else
                return 1;
        });
    }


    public boolean isValid() throws IOException {
        try {
            return this.start.before(this.end);
        } catch (NullPointerException ex) {
            throw new IOException("Start date or end date is not set");
        }
    }

    public boolean anyOverlaps() throws IOException {
        for (Task task: Storage.getInstance().getTasks().getAll()) {
            try {
                if (this.overlaps(task))
                    return true;
            } catch (NullPointerException ex) {
                throw new IOException("Start date or end date is not set");
            }

        }
        return false;
    }

    private boolean overlaps(Task task) {
        return (start.after(task.start) && start.before(task.end)) ||
                (end.after(task.start) && end.before(task.end));
    }
}
