package org.jcloudcodes.global

class AksOps implements Serializable {
    private final def steps
    private final VaultOps vaultOps

    AksOps(def steps) {
        this.steps = steps
        this.vaultOps = new VaultOps(steps)
    }

    void refreshEsoVaultToken(Map config) {
        String esoNamespace = config.get('externalSecretsNamespace', 'external-secrets')
        String esoToken = ''

        vaultOps.withVaultToken(config) {
            try {
                esoToken = steps.sh(
                    script: """
                        vault token create -field=token \
                          -policy='${config.get('esoVaultPolicy', 'eso-mongo-read')}' \
                          -ttl='${config.get('esoVaultTokenTtl', '168h')}'
                    """,
                    returnStdout: true
                ).trim()
            } catch (Exception ignored) {
                steps.echo 'Falling back to the AppRole-authenticated Vault token for ESO refresh'
                esoToken = steps.sh(script: 'printf %s "$VAULT_TOKEN"', returnStdout: true).trim()
            }
        }

        vaultOps.writeKubeconfig(config)

        runKubectl(config, "kubectl get namespace '${esoNamespace}' || kubectl create namespace '${esoNamespace}'")
        runKubectl(config, "kubectl delete secret vault-token -n '${esoNamespace}' --ignore-not-found")
        runKubectl(config, "kubectl create secret generic vault-token -n '${esoNamespace}' --from-literal=token='${esoToken}'")

        if (runKubectlStatus(config, "kubectl get deployment external-secrets -n '${esoNamespace}' >/dev/null 2>&1") == 0) {
            runKubectl(config, "kubectl rollout restart deployment external-secrets -n '${esoNamespace}'")
            runKubectl(config, "kubectl rollout status deployment external-secrets -n '${esoNamespace}'")
        } else {
            steps.echo "external-secrets deployment not found in namespace '${esoNamespace}', skipping rollout restart"
        }
    }

    void verifyNamespace(Map config) {
        vaultOps.writeKubeconfig(config)
        runKubectl(config, "kubectl get namespace '${config.kubeNamespace}' || kubectl create namespace '${config.kubeNamespace}'")
        runKubectl(config, "kubectl get pods -n '${config.kubeNamespace}'")
        runKubectl(config, "kubectl get svc -n '${config.kubeNamespace}'")
        runKubectl(config, "kubectl get ingress -n '${config.kubeNamespace}' || true")
    }

    private void runKubectl(Map config, String command) {
        String escaped = command.replace("'", "'\"'\"'")
        String kubeDir = config.get('workspaceKubeDir', '.kube')
        steps.sh """
            docker run --rm \
              --network host \
              -v "\$PWD:/workdir" \
              -w /workdir \
              -v "\$PWD/${kubeDir}:/root/.kube" \
              '${config.get('helmKubectlImage', 'dtzar/helm-kubectl:3.19.1')}' \
              sh -lc '${escaped}'
        """
    }

    private int runKubectlStatus(Map config, String command) {
        String escaped = command.replace("'", "'\"'\"'")
        String kubeDir = config.get('workspaceKubeDir', '.kube')
        steps.sh(
            script: """
                docker run --rm \
                  --network host \
                  -v "\$PWD:/workdir" \
                  -w /workdir \
                  -v "\$PWD/${kubeDir}:/root/.kube" \
                  '${config.get('helmKubectlImage', 'dtzar/helm-kubectl:3.19.1')}' \
                  sh -lc '${escaped}'
            """,
            returnStatus: true
        )
    }
}
