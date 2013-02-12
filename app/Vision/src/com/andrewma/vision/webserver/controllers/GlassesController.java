
package com.andrewma.vision.webserver.controllers;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.andrewma.vision.database.DatabaseHelper;
import com.andrewma.vision.models.Glasses;
import com.andrewma.vision.models.ScoredGlasses;
import com.andrewma.vision.utils.PerfLogger;
import com.andrewma.vision.webserver.core.Controller;
import com.andrewma.vision.webserver.core.NanoHTTPD;
import com.andrewma.vision.webserver.core.VisionHTTPD;
import com.andrewma.vision.webserver.core.annotations.Action;

public class GlassesController extends Controller {

    final DatabaseHelper dbHelper;
    final GlassesScoring scorer = new GlassesScoring();
    private List<Glasses> cache = null;

    public GlassesController(Context context) {
        super();
        dbHelper = DatabaseHelper.getInstance(context);
    }

    private List<Glasses> getGlasses() {
        if (cache == null) {
            cache = dbHelper.getAll(Glasses.class);
        }
        return cache;
    }

    @Action
    public Result Search(Glasses query) {
        PerfLogger.log("Search", "GlassesController.Search start");
        final List<Glasses> glasses = getGlasses();
        PerfLogger.log("Search", "Get glasses");
        final List<ScoredGlasses> results = scorer.score(query, glasses, 100);
        PerfLogger.stop("Search", "--Score glasses");
        return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, results);
    }

    @Action
    public Result GetAll() {
        return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, getGlasses());
    }

    @Action
    public Result Get(int id) {
        return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, dbHelper.get(Glasses.class, id));
    }

    @Action
    public synchronized Result Add(Glasses glasses) {
        // figure out which group this pair goes to. the algorithm for this is
        // rounding the OD (right) Sph value away from 0
        boolean positive = (glasses.OD_Spherical >= 0);
        glasses.Group = (int) Math.ceil(Math.abs(glasses.OD_Spherical));
        // anything over 10 gets grouped into the 20 group
        if (glasses.Group > 10) {
            glasses.Group = 20;
        }
        if (!positive) {
            glasses.Group *= -1;
        }

        // find number
        final List<Glasses> glassesInGroup = dbHelper.executeSql(Glasses.class,
                "SELECT * FROM Glasses WHERE [Group] = " + glasses.Group);
        int max = 0;
        for (Glasses g : glassesInGroup) {
            if (g.Number > max)
                max = g.Number;
        }
        glasses.Number = max + 1;

        // number of seconds since 1/1/1970
        glasses.AddedEpochTime = (new Date().getTime()) / 1000;

        glasses.GlassesId = (int) dbHelper.insert(glasses);

        // if glasses inventory is cached, we need to add to cache
        if (cache != null) {
            getGlasses().add(glasses);
        }

        // if database couldn't insert glasses, it will return -1 as the ID
        if (glasses.GlassesId == -1) {
            return ErrorResult("Could not add glasses. Error on db.insert.");
        }
        return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, glasses);
    }

    @Action
    public Result Update(Glasses update) {
        final Glasses original = (Glasses) dbHelper.get(Glasses.class, update.GlassesId);

        update.Group = original.Group;
        update.Number = original.Number;
        update.AddedEpochTime = original.AddedEpochTime;

        // TODO Update the cache

        // if update(...) returns 0, then no rows were updated
        if (dbHelper.update(update) == 0) {
            return ErrorResult("Could not update glasses. Error on db.update.");
        }
        return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, update);
    }

    @Action
    public Result Delete(int id) {
        if (cache != null) {
            final List<Glasses> glasses = getGlasses();
            for (int i = 0; i < glasses.size(); i++) {
                if (glasses.get(i).GlassesId == id) {
                    glasses.remove(i);
                    break;
                }
            }
        }

        dbHelper.delete(Glasses.class, id);
        return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, String.format("%d deleted", id));
    }

    @Action
    public Result Import(Glasses glasses) {
        if (cache != null) {
            getGlasses().add(glasses);
        }
        return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, dbHelper.insert(glasses));
    }

    @Action
    public Result ExportCsv() {
        final StringBuilder sb = new StringBuilder();
        sb.append("#Group,Number,OD_Spherical,OD_Cylindrical,OD_Axis,OD_Add,OD_Blind,OS_Spherical,OS_Cylindrical,OS_Axis,OS_Add,OS_Blind,AddedEpochTime,RemovedEpochTime\r\n");
        for (Glasses g : getGlasses()) {
            sb.append(String.format(
                    "%d,%d,%.2f,%.2f,%03d,%.2f,%d,%.2f,%.2f,%03d,%.2f,%d,%d,%d\r\n",
                    g.Group, g.Number,
                    g.OD_Spherical, g.OD_Cylindrical, g.OD_Axis, g.OD_Add, g.OD_Blind ? 1 : 0,
                    g.OS_Spherical, g.OS_Cylindrical, g.OS_Axis, g.OS_Add, g.OS_Blind ? 1 : 0,
                    g.AddedEpochTime, g.RemovedEpochTime));
        }
        return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_PLAINTEXT, sb.toString());
    }
}
