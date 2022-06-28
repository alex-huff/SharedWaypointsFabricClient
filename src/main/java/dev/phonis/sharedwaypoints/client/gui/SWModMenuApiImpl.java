package dev.phonis.sharedwaypoints.client.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public
class SWModMenuApiImpl implements ModMenuApi
{

	@Override
	public
	ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return ConfigScreen::getConfigScreen;
	}

}
