package com.eightbytestech.ebbaking.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = BakingDatabase.VERSION)
public class BakingDatabase {

    public static final int VERSION = 1;

    @Table(BakingColumns.class)
    public static final String BAKING_TABLE = "baking_table";

    @Table(IngredientsColumns.class)
    public static final String INGREDIENTS_TABLE = "ingredients_table";

    @Table(StepsColumns.class)
    public static final String STEPS_TABLE = "steps_table";
}
