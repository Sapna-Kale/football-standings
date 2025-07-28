pipeline {
  agent any

  environment {
    API_KEY = credentials('FOOTBALL_API_KEY')
    DOCKER_IMAGE = 'football-standings'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        echo 'Building the project...'
        sh './mvnw clean install -DskipTests=false'
      }
    }

    stage('Run Tests') {
      steps {
        echo 'Running unit tests...'
        sh './mvnw test'
      }
    }

    stage('Build Docker Image') {
      steps {
        echo "Building Docker image..."
        sh 'docker build -t $DOCKER_IMAGE .'
      }
    }

    stage('Run Docker Container (Local Test)') {
      steps {
        echo "Running container for validation..."
        sh 'docker run -e FOOTBALL_API_KEY=$API_KEY -p 8080:8080 -d $DOCKER_IMAGE'
      }
    }
  }

  post {
    always {
      echo 'Cleaning up Docker containers...'
      sh 'docker ps -aq --filter ancestor=$DOCKER_IMAGE | xargs -r docker rm -f || true'
    }
  }
}
