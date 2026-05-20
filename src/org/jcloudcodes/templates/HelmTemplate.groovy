package org.jcloudcodes.templates

class HelmTemplate implements Serializable {
    private final def steps

    HelmTemplate(def steps) {
        this.steps = steps
    }

    void packageChart(Map config) {
        if (!config.helmChartPath) {
            steps.echo 'Skipping Helm package stage because helmChartPath is not set'
            return
        }

        steps.sh """
            mkdir -p '${config.get('helmPackageOutputDir', 'dist/helm')}'
            docker run --rm \
              --network host \
              -v "\$PWD:/workdir" \
              -w /workdir \
              '${config.get('helmKubectlImage', 'dtzar/helm-kubectl:3.19.1')}' \
              sh -lc "helm dependency update '${config.helmChartPath}' || true && helm package '${config.helmChartPath}' --destination '${config.get('helmPackageOutputDir', 'dist/helm')}'"
        """
    }

    void publishChart(Map config) {
        if (!config.helmPublishCommand) {
            steps.echo 'Skipping Helm publish stage because helmPublishCommand is not set'
            return
        }

        String escaped = config.helmPublishCommand.replace("'", "'\"'\"'")
        steps.sh """
            docker run --rm \
              --network host \
              -v "\$PWD:/workdir" \
              -w /workdir \
              '${config.get('helmKubectlImage', 'dtzar/helm-kubectl:3.19.1')}' \
              sh -lc '${escaped}'
        """
    }
}
