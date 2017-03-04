#version 330 core

in vec3 vertCol;
in vec3 screenPos;
in vec2 texCoord;
out vec4 color;

uniform vec3 camPos;
uniform sampler2D texture;

void main() {
	color = texture2D(texture, texCoord) * vec4(vertCol, 1);
}
