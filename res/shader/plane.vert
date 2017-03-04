#version 330 core
layout (location = 0) in vec2 position;

out vec3 vertCol;
out vec2 screenPos;
out vec2 texCoord;

uniform mat4 viewMat;
uniform mat4 modelMat;

void main() {
	texCoord = position;
	screenPos = (modelMat * vec4(position, 0, 1)).xy;
	gl_Position = viewMat * modelMat * vec4(position, 0, 1);
	vertCol = vec3(1, 1, 1);
}
