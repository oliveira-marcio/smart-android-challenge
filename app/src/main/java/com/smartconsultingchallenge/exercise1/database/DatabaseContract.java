package com.smartconsultingchallenge.exercise1.database;

import android.provider.BaseColumns;

public final class DatabaseContract {

    private DatabaseContract() {

    }

    public static final class PostalEntry implements BaseColumns {
        public final static String TABLE_NAME = "postals";

        public final static String COLUMN_DISTRICT_CODE = "cod_distrito";
        public final static String COLUMN_COUNTY_CODE = "cod_concelho";
        public final static String COLUMN_LOCAL_CODE = "cod_localidade";
        public final static String COLUMN_LOCAL_NAME = "nome_localidade";
        public final static String COLUMN_PLACE_CODE = "cod_arteria";
        public final static String COLUMN_PLACE_TYPE = "tipo_arteria";
        public final static String COLUMN_PREP1 = "prep1";
        public final static String COLUMN_PLACE_TITLE = "titulo_arteria";
        public final static String COLUMN_PREP2 = "prep2";
        public final static String COLUMN_PLACE_NAME = "nome_arteria";
        public final static String COLUMN_PLACE_LOCAL = "local_arteria";
        public final static String COLUMN_PLACE_ACCESS = "troco";
        public final static String COLUMN_PLACE_NUMBER = "porta";
        public final static String COLUMN_CLIENT = "cliente";
        public final static String COLUMN_POSTAL_CODE = "num_cod_postal";
        public final static String COLUMN_POSTAL_EXT_CODE = "ext_cod_postal";
        public final static String COLUMN_POSTAL_DESIG = "desig_postal";
    }
}
