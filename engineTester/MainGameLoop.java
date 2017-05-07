package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);
		TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("person", loader),
				new ModelTexture(loader.loadTexture("playerTexture")));

		Player player = new Player(playerModel, new Vector3f(300, 5, -400), 0, 100, 0, 0.6f);
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());

		// ***************TEXT***********************

		FontType font = new FontType(loader.loadTexture("candara"), "candara");
		// GUIText text = new GUIText("In-Game Text!", 3, font, new
		// Vector2f(0.03f, 0.9f), 1f, false);
		// text.setColour(0.2f, 0.2f, 0.2f);

		// *********TERRAIN STUFF**********

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);

		// *******************Models**********************

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader), fernTextureAtlas);
		fern.getTexture().setHasTransparency(true);

		TexturedModel pineModel = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader),
				new ModelTexture(loader.loadTexture("pine")));
		pineModel.getTexture().setHasTransparency(true);

		TexturedModel willowModel = new TexturedModel(OBJFileLoader.loadOBJ("willow", loader),
				new ModelTexture(loader.loadTexture("willow")));
		willowModel.getTexture().setHasTransparency(true);
		
		TexturedModel flagModel = new TexturedModel(OBJFileLoader.loadOBJ("flag", loader),
				new ModelTexture(loader.loadTexture("flag")));
		flagModel.getTexture().setHasTransparency(true);

		TexturedModel pillar = new TexturedModel(OBJFileLoader.loadOBJ("pillar", loader),
				new ModelTexture(loader.loadTexture("sandy")));
		
		TexturedModel block = new TexturedModel(OBJFileLoader.loadOBJ("block2", loader),
				new ModelTexture(loader.loadTexture("pathway")));

		TexturedModel crate = new TexturedModel(OBJFileLoader.loadOBJ("crate", loader),
				new ModelTexture(loader.loadTexture("crate")));
		crate.getTexture().setExtraMap(loader.loadTexture("crateGlow"), true);

		TexturedModel cherryModel = new TexturedModel(OBJFileLoader.loadOBJ("cherry", loader),
				new ModelTexture(loader.loadTexture("cherry")));
		cherryModel.getTexture().setHasTransparency(true);
		cherryModel.getTexture().setShineDamper(10);
		cherryModel.getTexture().setReflectivity(0.5f);
		cherryModel.getTexture().setExtraMap(loader.loadTexture("cherryS"), false);

		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();

		// ******************NORMAL MAP MODELS************************

		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setExtraMap(loader.loadTexture("barrelS"), false);
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(1.5f);


		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));
		boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectivity(0.5f);

		// ************ENTITIES*******************

		Entity entity = new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f);
		Entity entity2 = new Entity(boulderModel, new Vector3f(85, 10, -75), 0, 0, 0, 1f);
		normalMapEntities.add(entity);
		normalMapEntities.add(entity2);

		Random random = new Random(5666778);
		for (int i = 0; i < 320; i++) {
			if (i % 3 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				float y = terrain.getHeightOfTerrain(x, z);
				if (y > 0) {
					entities.add(new Entity(fern, 3, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
				}
			}
			if (i % 1 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				if (x > 300 && x < 400 && z > -327 && z < 227) {
					continue;
				}
				float y = terrain.getHeightOfTerrain(x, z);
				if (y > 0) {
					if (random.nextFloat() > 0.3) {
						//entities.add(new Entity(willowModel, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
								//random.nextFloat() * 0.6f + 1f));
					} else {
						entities.add(new Entity(cherryModel, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
								random.nextFloat() * 0.6f + 2.5f));
					}
				}
			}
		}

//		normalMapEntities.add(new Entity(barrelModel, 1, new Vector3f(350, 20, -277), 0,
//				180, 0, 4f));
		entities.add(new Entity(crate, 1, new Vector3f(350, 20, -277), 0,
				180, 0, 0.4f));

		for (int i = 0; i < 90; i++) {
			float x = random.nextFloat() * 800;
			float z = random.nextFloat() * -800;
			float y = terrain.getHeightOfTerrain(x, z);
			normalMapEntities.add(new Entity(boulderModel, new Vector3f(x, y, z), random.nextFloat() * 360, 0, 0,
					0.5f + random.nextFloat()));
		}


		// *******************OTHER SETUP***************

		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(10000, 15000, -10000), new Vector3f(1.3f, 1.3f, 1.3f));
		lights.add(sun);

		entities.add(player);
		List<Entity> shadowEntities = new ArrayList<Entity>();
		shadowEntities.addAll(entities);
		shadowEntities.addAll(normalMapEntities);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

		// GUIs

		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f),
				new Vector2f(0.5f, 0.5f));
		//guiTextures.add(gui);
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		// **********Water Renderer Set-up************************

		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				waters.add(new WaterTile(i * 160, -j * 160, -2.5f));
			}
		}

		// ****************Particles*********************************

		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("cosmic"), 4, true);

		ParticleSystem system = new ParticleSystem(particleTexture, 155, 25, 0.2f, 2, 1f);
		system.setLifeError(0.1f);
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setSpeedError(0.25f);
		system.setScaleError(0.5f);
		system.randomizeRotation();
		
		Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
		Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo outputFbo2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		PostProcessing.init(loader);

		// ****************Game Loop Below*********************

		while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			picker.update();
			renderer.renderShadowMap(shadowEntities, sun);
			ParticleMaster.update(camera);

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			// render reflection teture
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera,
					new Vector4f(0, 1, 0, -waters.get(0).getHeight() + 1));
			camera.getPosition().y += distance;
			camera.invertPitch();

			// render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera,
					new Vector4f(0, -1, 0, waters.get(0).getHeight() + 0.2f));

			// render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();
			
			multisampleFbo.bindFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));
			waterRenderer.render(waters, camera, sun);
			ParticleMaster.renderParticles(camera);
			multisampleFbo.unbindFrameBuffer();
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
			PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());
			
			guiRenderer.render(guiTextures);
			TextMaster.render();

			DisplayManager.updateDisplay();
		}

		// *********Clean Up Below**************

		PostProcessing.cleanUp();
		outputFbo.cleanUp();
		outputFbo2.cleanUp();
		multisampleFbo.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
