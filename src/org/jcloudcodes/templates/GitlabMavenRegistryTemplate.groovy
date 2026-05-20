package org.jcloudcodes.templates

class GitlabMavenRegistryTemplate implements Serializable {
    private final def steps

    GitlabMavenRegistryTemplate(def steps) {
        this.steps = steps
    }

    void publish(Map config) {
        if (!config.gitlabRegistryEnabled && !config.gitlabMavenRegistryEnabled) {
            steps.echo 'Skipping GitLab Maven registry stage because it is disabled'
            return
        }

        String command = config.gitlabMavenRegistryCommand ?: config.gitlabRegistryCommand
        if (!command) {
            steps.echo 'Skipping GitLab Maven registry stage because no publish command is set'
            return
        }

        steps.sh command
    }
}
