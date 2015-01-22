#version 400 core

in vec2 pass_textureCoords;
in float visibility;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 fogColour;

void main(void){

	vec4 textureColour = texture(textureSampler, pass_textureCoords);
	if(textureColour.a<0.5){
		discard;
	}
	
	out_Color = mix(vec4(fogColour, 1.0), textureColour, visibility);

}