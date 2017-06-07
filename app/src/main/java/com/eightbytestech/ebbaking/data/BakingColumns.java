package com.eightbytestech.ebbaking.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public interface BakingColumns {

    @DataType(DataType.Type.INTEGER)
    @AutoIncrement
    @PrimaryKey
    String _ID = "id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String RECIPE_ID = "recipe_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String NAME = "name";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String SERVINGS = "servings";

    @DataType(DataType.Type.TEXT)
    String IMAGE = "image";
}
