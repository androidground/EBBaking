package com.eightbytestech.ebbaking.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface StepsColumns {

    @DataType(DataType.Type.INTEGER)
    @AutoIncrement
    @PrimaryKey
    String _ID = "id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String RECIPE_ID = "recipe_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String STEPS = "step";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String SHORT_DESCRIPTION = "short_description";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String LONG_DESCRIPTION = "long_description";

    @DataType(DataType.Type.TEXT)
    String VIDEO_URL = "video_url";

    @DataType(DataType.Type.TEXT)
    String THUMBNAIL_URL = "thumnail_url";
}
