package org.jcloudcodes.templates

class DockerTemplate implements Serializable {
    private final def steps

    DockerTemplate(def steps) {
        this.steps = steps
    }

    void buildImage(Map config) {
        String image = "${config.imageRepository}:${config.imageTag}"
        steps.sh "docker build -t '${image}' ."
    }

    void pushDockerHub(Map config) {
        String image = "${config.imageRepository}:${config.imageTag}"
        steps.withCredentials([
            steps.usernamePassword(
                credentialsId: config.dockerCredentialId,
                usernameVariable: 'DOCKER_USERNAME',
                passwordVariable: 'DOCKER_PASSWORD'
            )
        ]) {
            steps.sh """
                printf '%s' "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                docker push '${image}'
                docker logout || true
            """
        }
    }
}
