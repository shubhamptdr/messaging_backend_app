package com.messaging.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private int id;

    private String content;

=======
import Date;

public class Message {
    private int id;
    private String content;
>>>>>>> eb0f21ce2e30b44da21d9ddf75b011c0255afdf1
    private Date timestamp;

}
