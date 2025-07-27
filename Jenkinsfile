pipeline {
  agent any

  environment {
    // Optional: If using a .env file or secret store
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

    // Optional stage
    stage('Push Docker Image') {
      when {
        expression { return env.DOCKER_USERNAME != null }
      }
      steps {
        echo 'Pushing Docker image to registry...'
        sh 'docker tag $DOCKER_IMAGE $DOCKER_USERNAME/$DOCKER_IMAGE'
        sh 'docker push $DOCKER_USERNAME/$DOCKER_IMAGE'
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
