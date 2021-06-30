package phonis.survival;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RTConfig implements Serializable {

    public static final String configFile = RTSurvival.configDirectory + "RTConfig.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public volatile boolean highlightClosest;
    public volatile boolean renderWaypoints;
    public volatile boolean fullWaypointNames;
    public volatile float scale;

    public RTConfig() {
        this.highlightClosest = true;
        this.renderWaypoints = true;
        this.fullWaypointNames = false;
        this.scale = .05f;
    }

    public static RTConfig load() {
        if (Files.exists(Path.of(RTConfig.configFile))) {
            try (FileReader reader = new FileReader(RTConfig.configFile)) {
                return GSON.fromJson(reader, RTConfig.class);
            } catch (IOException e) {
                System.out.println("Could not read config. Using defaults.");
            }
        }

        return new RTConfig();
    }

    public void saveToFile() throws IOException {
        Path path = Path.of(RTSurvival.configDirectory);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        Files.write(Path.of(RTConfig.configFile), GSON.toJson(this).getBytes(StandardCharsets.UTF_8));
    }

}
