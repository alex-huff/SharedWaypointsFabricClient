package dev.phonis.sharedwaypoints.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dev.phonis.sharedwaypoints.client.SharedWaypointsClient;
import dev.phonis.sharedwaypoints.client.render.RGBAColor;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public
class SWConfig implements Serializable
{

    public static final  String    configFile                       = SharedWaypointsClient.configDirectory +
                                                                      "SWConfig.json";
    private static final Gson      GSON                             = new GsonBuilder().setPrettyPrinting().create();
    public static final  boolean   defaultCrossDimensionalWaypoints = true;
    public static final  boolean   defaultHighlightClosest          = true;
    public static final  boolean   defaultRenderWaypoints           = true;
    public static final  boolean   defaultFullWaypointNames         = true;
    public static final  int       defaultRenderScale               = 50;
    public static final  RGBAColor defaultPlateBackground           = new RGBAColor(50, 50, 120, 160);
    public static final  RGBAColor defaultFullBackground            = new RGBAColor(120, 50, 50, 200);
    public static final  RGBAColor defaultDistanceBackground        = new RGBAColor(50, 50, 50, 255);
    public static final  RGBAColor defaultTextColor                 = new RGBAColor(255, 255, 255, 255);
    public static final  SWConfig  INSTANCE                         = SWConfig.load();

    public boolean   crossDimensionalWaypoints;
    public boolean   highlightClosest;
    public boolean   renderWaypoints;
    public boolean   fullWaypointNames;
    public int       scale;
    public RGBAColor plateBackground;
    public RGBAColor fullBackground;
    public RGBAColor distanceBackground;
    public RGBAColor textColor;

    public
    SWConfig()
    {
        this.crossDimensionalWaypoints = SWConfig.defaultCrossDimensionalWaypoints;
        this.highlightClosest          = SWConfig.defaultHighlightClosest;
        this.renderWaypoints           = SWConfig.defaultRenderWaypoints;
        this.fullWaypointNames         = SWConfig.defaultFullWaypointNames;
        this.scale                     = SWConfig.defaultRenderScale;
        this.plateBackground           = SWConfig.defaultPlateBackground;
        this.fullBackground            = SWConfig.defaultFullBackground;
        this.distanceBackground        = SWConfig.defaultDistanceBackground;
        this.textColor                 = SWConfig.defaultTextColor;
    }

    private static
    SWConfig load()
    {
        if (Files.exists(Path.of(SWConfig.configFile)))
        {
            try (FileReader reader = new FileReader(SWConfig.configFile))
            {
                return GSON.fromJson(reader, SWConfig.class);
            }
            catch (IOException | JsonSyntaxException e)
            {
                System.out.println("Could not read config. Using defaults.");
            }
        }

        return new SWConfig();
    }

    public
    void saveToFile() throws IOException
    {
        Path path   = Path.of(SWConfig.configFile);
        Path parent = path.getParent();

        if (!Files.exists(parent))
        {
            Files.createDirectories(parent);
        }

        // Atomic file replace
        Path tempPath = path.resolveSibling(path.getFileName() + ".tmp");

        Files.writeString(tempPath, GSON.toJson(this));
        Files.move(tempPath, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    public static
    void trySave()
    {
        try
        {
            SWConfig.INSTANCE.saveToFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
