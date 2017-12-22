package com.dailymeditation.android.utils;

import com.dailymeditation.android.model.Feedback;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created with <3 by liacob & <Pi> on 05-Sep-17.
 */

public class DatabaseUtils {

    private static final String FEEDBACK_REFERENCE = "feedback_db";

    public static void uploadFeedback(Feedback feedback) {
        String key = FirebaseDatabase.getInstance().getReference(FEEDBACK_REFERENCE).push().getKey();
        FirebaseDatabase.getInstance().getReference(FEEDBACK_REFERENCE).child(key).setValue(feedback);
    }

}
