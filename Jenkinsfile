pipeline {
    agent any

    environment {
        registry = 'salimnahdi/docker-spring-boot'
        registryCredential = 'dockerhub'        // DockerHub credential id in Jenkins
        dockerImage = ''
        SONAR_URL = 'http://localhost:9000'    // change if your SonarQube is elsewhere
    }

    stages {
        stage('CHECKOUT GIT') {
            steps {
                git branch: 'main', url: 'https://github.com/NahdiSalim/Achat_Devops.git'
            }
        }

        stage('PREP - Make mvnw executable') {
            steps {
                // ensure the Maven wrapper is executable (required on Unix agents)
                sh 'if [ -f mvnw ]; then chmod +x mvnw; fi'
            }
        }

        stage('MVN CLEAN') {
            steps {
                sh './mvnw clean -B'
            }
        }

        stage('ARTIFACT CONSTRUCTION') {
            steps {
                echo 'ARTIFACT CONSTRUCTION...'
                // use -DskipTests to skip tests during packaging if desired
                // Removed -P test-coverage since the profile doesn't exist; JaCoCo runs automatically
                sh './mvnw package -DskipTests -B'
            }
        }

        stage('UNIT TESTS') {
            steps {
                echo 'Launching Unit Tests...'
                sh './mvnw test -B'
            }
        }

        stage('MVN SONARQUBE') {
            steps {
                // Use Jenkins credentials for Sonar token instead of hardcoding.
                // Create a "Secret text" credential in Jenkins with id 'sonar-token-jenkins'
                withCredentials([string(credentialsId: 'sonar-token-jenkins', variable: 'SONAR_TOKEN')]) {
                    sh "./mvnw sonar:sonar -Dsonar.host.url=${env.SONAR_URL} -Dsonar.login=${SONAR_TOKEN} -B"
                }
            }
        }

        stage('PUBLISH TO NEXUS (placeholder)') {
            steps {
                // Placeholder: put your 'mvn deploy' here with proper settings.xml or credentials
                sh 'java -version'
            }
        }

        stage('BUILDING OUR IMAGE') {
            steps {
                script {
                    dockerImage = docker.build("${registry}:${env.BUILD_NUMBER}")
                    sh 'java -version'
                }
            }
        }

        stage('DEPLOY OUR IMAGE') {
            steps {
                script {
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push()
                        // optionally push latest tag too
                        dockerImage.push('latest')
                        sh 'java -version'
                    }
                }
            }
        }
    }

    post {
        always {
            // keep logs and artifacts if needed
            archiveArtifacts artifacts: 'target/**/*.jar', allowEmptyArchive: true
        }
    }
}
