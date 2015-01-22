#version 400 core

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D textureSampler;

void main(void){

	vec4 textureColour = texture(textureSampler, pass_textureCoords);
	if(textureColour.a<0.2){
		discard;
	}
	out_Color = textureColour;

}