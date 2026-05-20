package org.jcloudcodes.templates

class SonarTemplate implements Serializable {
    private final def steps

    SonarTemplate(def steps) {
        this.steps = steps
    }

    void scan(Map config) {
        if (!config.sonarEnabled) {
            steps.echo 'Skipping Sonar stage because sonarEnabled is false'
            return
        }

        steps.sh "${config.mavenCommand ?: 'mvn'} -B sonar:sonar"
    }
}
