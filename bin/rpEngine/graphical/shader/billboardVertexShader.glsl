#version 400 core

in vec3 vertices;
in vec2 textureCoords;
in vec3 positions;

out vec2 pass_textureCoords;
out float visibility;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec2 scale;

const float density = 0.005;
const float gradient = 1.5;

void main(void){

	//rotation:
	vec3 cameraRight_worldspace2 = vec3(viewMatrix[0][0], viewMatrix[1][0], viewMatrix[2][0]); //test ob transpose+viewMatrix[i] schneller ist
	vec3 cameraUp_worldspace2 = vec3(viewMatrix[0][1], viewMatrix[1][1], viewMatrix[2][1]);

	//positionWorldSpace:
	vec3 vertexPosition_worldspace = 
		    positions
		    + cameraRight_worldspace2 * vertices.x * scale.x
		    + cameraUp_worldspace2 * vertices.y * scale.y;


	vec4 positionRelativeToCam = viewMatrix * vec4(vertexPosition_worldspace, 1.0);
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoords = textureCoords;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
	
}