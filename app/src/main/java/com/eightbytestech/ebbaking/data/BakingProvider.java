package com.eightbytestech.ebbaking.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = BakingProvider.AUTHORITY, database = BakingDatabase.class)
public class BakingProvider {

    static final String AUTHORITY = "com.eightbytestech.ebbaking.data.provider";

    @TableEndpoint(table = BakingDatabase.BAKING_TABLE)
    public static class BakingTable {

        @ContentUri(path = BakingDatabase.BAKING_TABLE,
                type = "vnd.android.cursor/baking_table",
                defaultSort = BakingColumns._ID + " DESC")
        public static final Uri CONTENT_URI
                = Uri.parse("content://" + AUTHORITY + "/baking_table");

        @InexactContentUri(
                path = "baking_table/#",
                name = "LIST_ID",
                type = "vnd.android.cursor.item/baking_table",
                whereColumn = BakingColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/baking_table/" + id);
        }
    }

    @TableEndpoint(table = BakingDatabase.INGREDIENTS_TABLE)
    public static class IngredientsTable {

        @ContentUri(path = "ingredients_table",
                type = "vnd.android.cursor/ingredients_table",
                defaultSort = IngredientsColumns._ID + " DESC")
        public static final Uri CONTENT_URI
                = Uri.parse("content://" + AUTHORITY + "/ingredients_table");

        @InexactContentUri(
                path = "ingredients_table/#",
                name = "LIST_ID",
                type = "vnd.android.cursor.item/ingredients_table",
                whereColumn = BakingColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/ingredients_table/" + id);
        }
    }

    @TableEndpoint(table = BakingDatabase.STEPS_TABLE)
    public static class StepsTable {

        @ContentUri(path = "steps_table",
                type = "vnd.android.cursor/steps_table",
                defaultSort = IngredientsColumns._ID + " DESC")
        public static final Uri CONTENT_URI
                = Uri.parse("content://" + AUTHORITY + "/steps_table");

        @InexactContentUri(
                path = "steps_table/#",
                name = "LIST_ID",
                type = "vnd.android.cursor.item/steps_table",
                whereColumn = BakingColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/steps_table/" + id);
        }
    }
}
