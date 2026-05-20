package org.jcloudcodes.global

class GitOpsOps implements Serializable {
    private final def steps

    GitOpsOps(def steps) {
        this.steps = steps
    }

    void cloneRepo(Map config, Closure body) {
        steps.withCredentials([
            steps.string(credentialsId: config.gitopsRepoTokenCredentialId, variable: 'GITOPS_TOKEN')
        ]) {
            String authedUrl = config.gitopsRepoUrl.replace(
                'https://',
                'https://oauth2:$GITOPS_TOKEN@'
            )

            steps.dir('gitops-repo') {
                steps.deleteDir()
                steps.sh """
                    git clone '${authedUrl}' .
                    git config user.email '${config.get('gitUserEmail', 'ci@jcloudcodes.local')}'
                    git config user.name '${config.get('gitUserName', 'Jenkins CI')}'
                """
                body.call()
            }
        }
    }

    void updateImageValues(Map config) {
        cloneRepo(config) {
            steps.sh """
                yq -i '.image.repository = "${config.imageRepository}"' '${config.helmValuesFile}'
                yq -i '.image.tag = "${config.imageTag}"' '${config.helmValuesFile}'
                git add '${config.helmValuesFile}'
                git commit -m 'Update ${config.appName} image to ${config.imageTag}' || echo 'No GitOps changes'
                git push origin HEAD:${config.get('gitopsBranch', 'main')}
            """
        }
    }
}
