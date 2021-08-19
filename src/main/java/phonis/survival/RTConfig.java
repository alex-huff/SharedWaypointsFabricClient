package phonis.survival;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import phonis.survival.render.RGBAColor;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class RTConfig implements Serializable {

    public static final String configFile = RTSurvival.configDirectory + "RTConfig.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final boolean defaultHighlightClosest = true;
    public static final boolean defaultRenderWaypoints = true;
    public static final boolean defaultFullWaypointNames = true;
    public static final int defaultRenderScale = 50;
    public static final RGBAColor defaultPlateBackground = new RGBAColor(50, 50, 120, 160);
    public static final RGBAColor defaultFullBackground = new RGBAColor(120, 50, 50, 200);
    public static final RGBAColor defaultDistanceBackground = new RGBAColor(50, 50, 50, 255);
    public static final RTConfig INSTANCE = RTConfig.load();

    public boolean highlightClosest;
    public boolean renderWaypoints;
    public boolean fullWaypointNames;
    public int scale;
    public RGBAColor plateBackground;
    public RGBAColor fullBackground;
    public RGBAColor distanceBackground;

    public RTConfig() {
        this.highlightClosest = RTConfig.defaultHighlightClosest;
        this.renderWaypoints = RTConfig.defaultRenderWaypoints;
        this.fullWaypointNames = RTConfig.defaultFullWaypointNames;
        this.scale = RTConfig.defaultRenderScale;
        this.plateBackground = RTConfig.defaultPlateBackground;
        this.fullBackground = RTConfig.defaultFullBackground;
        this.distanceBackground = RTConfig.defaultDistanceBackground;
    }

    private static RTConfig load() {
        if (Files.exists(Path.of(RTConfig.configFile))) {
            try (FileReader reader = new FileReader(RTConfig.configFile)) {
                return GSON.fromJson(reader, RTConfig.class);
            } catch (IOException | JsonSyntaxException e) {
                System.out.println("Could not read config. Using defaults.");
            }
        }

        return new RTConfig();
    }

    public void saveToFile() throws IOException {
        Path path = Path.of(RTConfig.configFile);
        Path parent = path.getParent();

        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        // Atomic file replace
        Path tempPath = path.resolveSibling(path.getFileName() + ".tmp");
        Files.writeString(tempPath, GSON.toJson(this));
        Files.move(tempPath, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void trySave() {
        try {
            RTConfig.INSTANCE.saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
