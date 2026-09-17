pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "ashugedekar/employee-app"
        NAMESPACE = "employee"
        DEPLOYMENT = "employee-app"
        CONTAINER = "employee-app"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                    -t ${DOCKER_IMAGE}:${BUILD_NUMBER} \
                    .
                '''
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login \
                            -u "$DOCKER_USER" \
                            --password-stdin

                        docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}

                        docker logout
                    '''
                }
            }
        }

        stage('Apply Kubernetes Manifest') {
            steps {
                sh '''
                    kubectl apply \
                    -f k8s/employee-deployment.yaml \
                    -n ${NAMESPACE}
                '''
            }
        }

        stage('Deploy New Image') {
            steps {
                sh '''
                    kubectl set image deployment/${DEPLOYMENT} \
                    ${CONTAINER}=${DOCKER_IMAGE}:${BUILD_NUMBER} \
                    -n ${NAMESPACE}
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    kubectl rollout status deployment/${DEPLOYMENT} \
                    -n ${NAMESPACE} \
                    --timeout=180s
                '''
            }
        }
    }

    post {

        success {
            echo "Deployment Successful!"
        }

        failure {
            echo "Pipeline Failed!"
        }
    }
}
