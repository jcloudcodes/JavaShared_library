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

        steps.sh """
            kubectl get namespace '${esoNamespace}' || kubectl create namespace '${esoNamespace}'
            kubectl delete secret vault-token -n '${esoNamespace}' --ignore-not-found
            kubectl create secret generic vault-token -n '${esoNamespace}' --from-literal=token='${esoToken}'
        """

        if (steps.sh(script: "kubectl get deployment external-secrets -n '${esoNamespace}' >/dev/null 2>&1", returnStatus: true) == 0) {
            steps.sh """
                kubectl rollout restart deployment external-secrets -n '${esoNamespace}'
                kubectl rollout status deployment external-secrets -n '${esoNamespace}'
            """
        } else {
            steps.echo "external-secrets deployment not found in namespace '${esoNamespace}', skipping rollout restart"
        }
    }

    void verifyNamespace(Map config) {
        vaultOps.writeKubeconfig(config)
        steps.sh """
            kubectl get namespace '${config.kubeNamespace}' || kubectl create namespace '${config.kubeNamespace}'
            kubectl get pods -n '${config.kubeNamespace}'
            kubectl get svc -n '${config.kubeNamespace}'
            kubectl get ingress -n '${config.kubeNamespace}' || true
        """
    }
}
