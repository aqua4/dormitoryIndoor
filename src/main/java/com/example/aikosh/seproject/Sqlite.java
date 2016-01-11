package com.example.aikosh.seproject;

import android.provider.BaseColumns;

public class Sqlite {
    // To prevent someone from accidentally instantiating the contract class,
// give it an empty constructor.
    public Sqlite() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Scannable";
        public static final String _ID = "_ID"; //_ID
        public static final String ID = "ID";
        public static final String Relations = "Relations"; // DECK_NAME
        public static final String x_cordinate = "x_cordinate"; // TERM
        public static final String y_cordinate = "y_cordinate"; // DEFINITION
        public static final String Floor = "Floor";
        public static final String Type = "Type";
        public static final String LongText="LongText";
       // public static final String updatedAt = "updatedAt";
    }
}
