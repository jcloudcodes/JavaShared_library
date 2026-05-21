package org.jcloudcodes.templates

import org.jcloudcodes.global.VaultOps

class DockerTemplate implements Serializable {
    private final def steps
    private final VaultOps vaultOps

    DockerTemplate(def steps) {
        this.steps = steps
        this.vaultOps = new VaultOps(steps)
    }

    void buildImage(Map config) {
        String image = "${config.imageRepository}:${config.imageTag}"
        String extraArgs = config.get('dockerBuildExtraArgs', '').trim()
        String extra = extraArgs ? extraArgs + ' ' : ''
        steps.sh "docker build ${extra}-t '${image}' ."
    }

    void pushDockerHub(Map config) {
        String image = "${config.imageRepository}:${config.imageTag}"
        boolean useVaultDockerCredentials = config.get('useVaultDockerCredentials', true)

        if (useVaultDockerCredentials) {
            String dockerUsername = vaultOps.readKvField(config, config.get('dockerUsernameField', 'DOCKER_USERNAME'))
            String dockerPassword = vaultOps.readKvField(config, config.get('dockerPasswordField', 'DOCKER_PASSWORD'))

            steps.withEnv([
                "DOCKER_USERNAME=${dockerUsername}",
                "DOCKER_PASSWORD=${dockerPassword}"
            ]) {
                runDockerPush(image)
            }
            return
        }

        steps.withCredentials([
            steps.usernamePassword(
                credentialsId: config.dockerCredentialId,
                usernameVariable: 'DOCKER_USERNAME',
                passwordVariable: 'DOCKER_PASSWORD'
            )
        ]) {
            runDockerPush(image)
        }
    }

    private void runDockerPush(String image) {
        steps.sh(
            script: """
                printf '%s' "\$DOCKER_PASSWORD" | docker login -u "\$DOCKER_USERNAME" --password-stdin
                docker push '${image}'
                docker logout || true
            """
        )
    }
}
