package models.tasks;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

public interface ITask extends Serializable {
    String getStatusIcon();

    void markAsDone();

    String getDescription();

    String getInitials();

    String getDateTime();

    Date getDateTimeObject();
}
