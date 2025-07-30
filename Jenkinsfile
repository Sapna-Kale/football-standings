pipeline {
  agent any
  environment {
    DOCKER_IMAGE = "football-standings"
    IMAGE_TAG = "v1.0.${env.BUILD_NUMBER}"
    REGISTRY = "your-jfrog-host/your-project"
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://<your-repo>.git'
      }
    }
    stage('Build & Test') {
      steps {
        sh './mvnw clean test'
      }
    }
    stage('Package & Docker Build') {
      steps {
        sh './mvnw package -DskipTests'
        sh "docker build -t $DOCKER_IMAGE:$IMAGE_TAG ."
      }
    }
    stage('Push to Registry') {
      steps {
        sh "docker tag $DOCKER_IMAGE:$IMAGE_TAG $REGISTRY/$DOCKER_IMAGE:$IMAGE_TAG"
        sh "docker push $REGISTRY/$DOCKER_IMAGE:$IMAGE_TAG"
      }
    }
    stage('Deploy to Kubernetes') {
      steps {
        sh "kubectl set image deployment/football-backend football-backend=$REGISTRY/$DOCKER_IMAGE:$IMAGE_TAG --record"
      }
    }
  }
}
