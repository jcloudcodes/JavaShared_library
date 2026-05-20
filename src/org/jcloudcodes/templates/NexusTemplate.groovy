package org.jcloudcodes.templates

class NexusTemplate implements Serializable {
    private final def steps

    NexusTemplate(def steps) {
        this.steps = steps
    }

    void publishMaven(Map config) {
        if (!config.nexusEnabled) {
            steps.echo 'Skipping Nexus publish stage because nexusEnabled is false'
            return
        }

        steps.sh "${config.mavenCommand ?: 'mvn'} -B deploy -DskipTests"
    }
}
