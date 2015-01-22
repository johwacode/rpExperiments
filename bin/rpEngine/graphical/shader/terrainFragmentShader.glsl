#version 400 core

in vec2 pass_textureCoords;
in float pass_terrainType;
in vec3 surfaceNormal;
in vec3 toLightVector[10];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;

uniform sampler2D blendMap;

uniform vec3 lightColour[10];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 fogColour;

void main(void){

	vec4 blendMapColour = texture(blendMap, pass_textureCoords);
	
	float backTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
	vec2 tiledCoords = pass_textureCoords * 40.0;
	vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColour = texture(rTexture, tiledCoords) * blendMapColour.r;
	vec4 gTextureColour = texture(gTexture, tiledCoords) * blendMapColour.g;
	vec4 bTextureColour = texture(bTexture, tiledCoords) * blendMapColour.b;

	vec4 totalColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;
	
	if(pass_terrainType > 0.5){
		totalColour = texture(rTexture, tiledCoords);
		}
	
	

	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitVectorToCamera = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i=0; i<10; i++){
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDot1 = dot(unitNormal, unitLightVector);
		float brightness = max(nDot1, 0.0);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		
		totalDiffuse += brightness * lightColour[i];
		totalSpecular += dampedFactor * reflectivity * lightColour[i];
	}
	
	totalDiffuse = max(totalDiffuse, 0.5);
	
	
	out_Color = vec4(totalDiffuse, 1.0) * totalColour + vec4(totalSpecular, 1.0);
	out_Color = mix(vec4(fogColour, 1.0), out_Color, visibility);
}