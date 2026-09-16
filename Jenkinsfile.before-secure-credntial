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
                sh '''
                    docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                '''
            }
        }

        stage('Deploy to Kubernetes') {
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
                    --timeout=120s
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
