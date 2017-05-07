package textures;

public class ModelTexture {
	
	private int textureID;
	private int normalMap;
	private int specularMap;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	private boolean usesSpecularMap = false;
	private boolean glowing = false;
	
	private int numberOfRows = 1;
	
	public ModelTexture(int texture){
		this.textureID = texture;
	}
	
	public void setExtraMap(int texture, boolean glows){
		this.specularMap = texture;
		this.usesSpecularMap = true;
		this.glowing = glows;
	}
	
	public boolean isGlowing(){
		return glowing;
	}
	
	public boolean usesSpecularMap(){
		return usesSpecularMap;
	}
	
	public int getSpecularMap(){
		return specularMap;
	}
		
	public int getNumberOfRows() {
		return numberOfRows;
	}

	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}


	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}


	public int getID(){
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

}
