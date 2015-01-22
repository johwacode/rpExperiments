#version 400 core

in vec2 position;
in vec2 coord;

uniform vec2 screenPosition;

out vec2 pass_textureCoords;

void main(void){

	gl_Position = vec4(((position.x+screenPosition.x)/5-1), ((position.y+screenPosition.y)/5-1), 0, 1);
	pass_textureCoords = coord;
}