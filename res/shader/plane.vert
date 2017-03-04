#version 330 core
layout (location = 0) in vec2 position;

out vec3 vertCol;
out vec2 screenPos;

uniform mat4 viewMat;
uniform mat4 modelMat;

void main() {
	screenPos = (modelMat * vec4(position, 0, 1)).xy;
	gl_Position = viewMat * modelMat * vec4(position, 0, 1);
	vertCol = vec3(1, 0, 0);
}
