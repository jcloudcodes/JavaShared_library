package org.jcloudcodes.templates

class JavaMavenTemplate implements Serializable {
    private final def steps

    JavaMavenTemplate(def steps) {
        this.steps = steps
    }

    void validate(Map config) {
        steps.sh "${config.mavenCommand ?: 'mvn'} -version"
        steps.sh 'java -version'
    }

    void build(Map config) {
        steps.sh "${config.mavenCommand ?: 'mvn'} -B clean compile"
    }

    void test(Map config) {
        steps.sh "${config.mavenCommand ?: 'mvn'} -B test"
    }

    void packageArtifact(Map config) {
        steps.sh "${config.mavenCommand ?: 'mvn'} -B package -DskipTests"
    }
}
