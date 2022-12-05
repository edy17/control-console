node {
    withEnv(["aggregator_image_tag=${env.DOCKER_REGISTRY}/${env.DOCKER_REPOSITORY}:cc-aggregator-v${env.BUILD_NUMBER}",
             "probe_image_tag=${env.DOCKER_REGISTRY}/${env.DOCKER_REPOSITORY}:cc-probe-v${env.BUILD_NUMBER}"]) {
        stage('Checkout') {
            checkout scm
        }
        stage('Build and Push Docker images to Registry') {
            withCredentials([[$class : 'UsernamePasswordMultiBinding',
                          credentialsId   : env.DOCKER_CREDENTIALS_ID,
                          usernameVariable: 'USERNAME',
                          passwordVariable: 'PASSWORD']]) {
              sh """
                echo $PASSWORD | docker login --username $USERNAME --password-stdin
              """
              dir("aggregator") {
                  sh """
                    docker build -t ${aggregator_image_tag} -f Dockerfile.native .
                    docker push ${aggregator_image_tag}
                    docker rmi -f ${aggregator_image_tag}
                  """
              }
              dir("probe") {
                  sh """
                    docker build -t ${probe_image_tag} -f Dockerfile.native .
                    docker push ${probe_image_tag}
                    docker rmi -f ${probe_image_tag}
                  """
              }
            }
        }
        stage('Deploy on k8s') {
            withKubeConfig([credentialsId: env.K8s_CREDENTIALS_ID,
                          serverUrl    : env.K8s_SERVER_URL,
                          contextName  : env.K8s_CONTEXT_NAME,
                          clusterName  : env.K8s_CLUSTER_NAME]) {
                sh """
                    kubectl set image deployment/cc-aggregator-deployment cc-aggregator=${aggregator_image_tag} -n default
                    kubectl rollout status -w deployment/cc-aggregator-deployment -n default
                    
                    kubectl set image deployment/cc-probe-deployment cc-probe=${probe_image_tag} -n default
                    kubectl rollout status -w deployment/cc-probe-deployment -n default
                """
            }
        }
    }
}



