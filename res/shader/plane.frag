#version 330 core

in vec3 vertCol;
in vec3 screenPos;
out vec4 color;

uniform vec3 camPos;

void main() {
	color = vec4(vertCol, 1);
}
