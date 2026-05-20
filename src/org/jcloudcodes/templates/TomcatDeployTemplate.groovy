package org.jcloudcodes.templates

class TomcatDeployTemplate implements Serializable {
    private final def steps

    TomcatDeployTemplate(def steps) {
        this.steps = steps
    }

    void deployLinux(Map config) {
        if (!config.tomcatLinuxDeployCommand) {
            steps.echo 'Skipping Tomcat Linux deploy stage because tomcatLinuxDeployCommand is not set'
            return
        }

        steps.sh config.tomcatLinuxDeployCommand
    }

    void deployWindows(Map config) {
        if (!config.tomcatWindowsDeployCommand) {
            steps.echo 'Skipping Tomcat Windows deploy stage because tomcatWindowsDeployCommand is not set'
            return
        }

        steps.bat config.tomcatWindowsDeployCommand
    }
}
