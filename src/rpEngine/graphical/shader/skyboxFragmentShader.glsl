#version 400

in vec3 textureCoords;
out vec4 out_Color;

uniform samplerCube cubeMapDay;
uniform samplerCube cubeMapNight;
uniform float blendFactor;

uniform vec3 fogColor;

//todo: get values as uniform -> currently not nice when flying
const float lowerLimit = -10.0;
const float upperLimit = 40.0;

void main(void){
	vec4 day = texture(cubeMapDay, textureCoords);
	vec4 night = texture(cubeMapNight, textureCoords);
	vec4 texColor = mix(day, night, blendFactor);
	
	float factor = (textureCoords.y - lowerLimit) / (upperLimit - lowerLimit);
	factor = clamp(factor, 0.0, 1.0);
	out_Color = mix(vec4(fogColor, 1.0), texColor, factor);
}