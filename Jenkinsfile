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
                script {

                    try {

                        echo "Checking Kubernetes rollout..."

                        sh '''
                            kubectl rollout status deployment/${DEPLOYMENT} \
                            -n ${NAMESPACE} \
                            --timeout=180s
                        '''

                        echo "Deployment rollout successful!"

                    } catch (Exception e) {

                        echo "======================================"
                        echo "DEPLOYMENT FAILED"
                        echo "Starting automatic rollback..."
                        echo "======================================"

                        sh '''
                            kubectl rollout undo deployment/${DEPLOYMENT} \
                            -n ${NAMESPACE}
                        '''

                        echo "Rollback command executed."

                        sh '''
                            kubectl rollout status deployment/${DEPLOYMENT} \
                            -n ${NAMESPACE} \
                            --timeout=180s
                        '''

                        echo "Rollback completed successfully."

                        throw e
                    }
                }
            }
        }
    }

    post {

        success {
            echo "======================================"
            echo "Deployment Successful!"
            echo "======================================"
        }

        failure {
            echo "======================================"
            echo "Pipeline Failed!"
            echo "======================================"
        }
    }
}
