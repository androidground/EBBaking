package com.eightbytestech.ebbaking.utils;

import com.eightbytestech.ebbaking.models.Recipe;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by vishalsaxena on 01/06/17.
 */

public interface BakingApiInterface {
    @GET("topher/2017/May/59121517_baking/baking.json")
    Observable<ArrayList<Recipe>> getRecipes();
}
