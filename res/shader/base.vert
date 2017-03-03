#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 norm;
layout (location = 2) in int style;

layout (location = 3) in vec3 pointVD[3];
layout (location = 6) in vec3 orthVD[3];

out vec3 vertCol;
out vec3 normal;
out vec3 worldPos;

out vec3 points[3];
out vec3 orths[3];

uniform vec3 camPos;
uniform mat4 projMat;
uniform mat4 viewMat;
uniform mat4 modelMat;
uniform vec3 primColor;
uniform vec3 altColor;

void main() {
	normal = norm;
	worldPos = (modelMat * vec4(position, 1.0f)).xyz;
	for(int i = 0; i < 3; i++) {
		points[i] = (modelMat * vec4(pointVD[i], 1)).xyz;
		orths[i] = (modelMat * vec4(orthVD[i], 0)).xyz;
	}
	gl_Position = projMat * viewMat * modelMat * vec4(position, 1.0f);
	if(style%2 == 0) vertCol = primColor;
	else vertCol = altColor;
}
