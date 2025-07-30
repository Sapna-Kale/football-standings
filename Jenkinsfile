pipeline {
  agent any

  environment {
    DOCKER_IMAGE = "football-standings"
    IMAGE_TAG = "v1.0.${env.BUILD_NUMBER}"
    REGISTRY = "kalesapna"
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/Sapna-Kale/football-standings.git'
      }
    }

    stage('Build & Test') {
      steps {
        sh './mvnw clean test'
      }
    }

    stage('Package & Build Docker Image') {
      steps {
        sh './mvnw package -DskipTests'
        sh "docker build -t $DOCKER_IMAGE:$IMAGE_TAG ."
      }
    }

    stage('Push Docker Image') {
      steps {
        script {
          docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials-id') {
            sh "docker tag $DOCKER_IMAGE:$IMAGE_TAG $REGISTRY/$DOCKER_IMAGE:$IMAGE_TAG"
            sh "docker push $REGISTRY/$DOCKER_IMAGE:$IMAGE_TAG"
          }
        }
      }
    }
  }
}
