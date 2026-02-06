package dev.phonis.sharedwaypoints.client;

import dev.phonis.sharedwaypoints.client.render.RenderHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;

public class InitHandler implements IInitializationHandler
{

    @Override
    public void registerModHandlers()
    {
        RenderHandler renderer = RenderHandler.getInstance();
        RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
        RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);
    }

}
