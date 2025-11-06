pipeline {
    agent any

    tools {
        maven 'Maven-3.8'
        jdk 'JDK-18'
    }

    environment {
        NEXUS_VERSION = 'nexus3'
        NEXUS_PROTOCOL = 'http'
        NEXUS_URL = 'localhost:8081'
        NEXUS_REPOSITORY = 'maven-releases'
        NEXUS_CREDENTIAL_ID = 'nexus-credentials'

        DOCKER_IMAGE = 'achat-app'
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_CREDENTIALS = 'dockerhub-credentials'

        SONARQUBE_URL = 'http://localhost:9000'
        SONARQUBE_TOKEN = credentials('sonarqube-token')
    }

    stages {
        stage('Checkout') {
            steps {
                echo '========== Checking out code from GitHub =========='
                git branch: 'main',
                    credentialsId: 'github-credentials',
                    url: 'https://github.com/yourusername/Achat_Devops.git'
            }
        }

        stage('Build') {
            steps {
                echo '========== Building application with Maven =========='
                script {
                    if (isUnix()) {
                        sh './mvnw clean compile'
                    } else {
                        bat '.\\mvnw.cmd clean compile'
                    }
                }
            }
        }

        stage('Unit Tests') {
            steps {
                echo '========== Running unit tests =========='
                script {
                    if (isUnix()) {
                        sh './mvnw test'
                    } else {
                        bat '.\\mvnw.cmd test'
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }

        stage('Code Quality Analysis') {
            steps {
                echo '========== Running SonarQube analysis =========='
                script {
                    withSonarQubeEnv('SonarQube') {
                        if (isUnix()) {
                            sh './mvnw sonar:sonar -Dsonar.projectKey=achat-devops'
                        } else {
                            bat '.\\mvnw.cmd sonar:sonar -Dsonar.projectKey=achat-devops'
                        }
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '========== Waiting for Quality Gate =========='
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                echo '========== Packaging application =========='
                script {
                    if (isUnix()) {
                        sh './mvnw package -DskipTests'
                    } else {
                        bat '.\\mvnw.cmd package -DskipTests'
                    }
                }
            }
        }

        stage('Publish to Nexus') {
            steps {
                echo '========== Publishing artifact to Nexus =========='
                script {
                    pom = readMavenPom file: 'pom.xml'
                    filesByGlob = findFiles(glob: 'target/*.jar')
                    artifactPath = filesByGlob[0].path
                    artifactExists = fileExists artifactPath

                    if (artifactExists) {
                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            groupId: pom.groupId,
                            version: pom.version,
                            repository: NEXUS_REPOSITORY,
                            credentialsId: NEXUS_CREDENTIAL_ID,
                            artifacts: [
                                [artifactId: pom.artifactId,
                                 classifier: '',
                                 file: artifactPath,
                                 type: 'jar']
                            ]
                        )
                        echo "✅ Artifact published successfully to Nexus"
                    } else {
                        error "❌ Artifact file not found: ${artifactPath}"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '========== Building Docker image =========='
                script {
                    dockerImage = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                echo '========== Pushing Docker image to registry =========='
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS) {
                        dockerImage.push("${DOCKER_TAG}")
                        dockerImage.push('latest')
                    }
                }
            }
        }

        stage('Clean Docker Images') {
            steps {
                echo '========== Cleaning local Docker images =========='
                script {
                    sh "docker rmi ${DOCKER_IMAGE}:${DOCKER_TAG} || true"
                    sh "docker rmi ${DOCKER_IMAGE}:latest || true"
                }
            }
        }

        stage('Deploy with Terraform') {
            steps {
                echo '========== Deploying infrastructure with Terraform =========='
                script {
                    dir('terraform') {
                        sh 'terraform init'
                        sh 'terraform plan -out=tfplan'
                        sh 'terraform apply -auto-approve tfplan'
                    }
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                echo '========== Verifying deployment =========='
                script {
                    sleep(time: 30, unit: 'SECONDS')
                    sh '''
                        curl -f http://localhost:8089/SpringMVC/actuator/health || exit 1
                        echo "✅ Application is healthy"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '========== Pipeline executed successfully! =========='
            emailext(
                subject: "✅ SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: '''
                    <p>SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'</p>
                    <p>Check console output at: <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>
                    <p><strong>Changes:</strong></p>
                    <p>${env.CHANGE_AUTHOR}: ${env.CHANGE_TITLE}</p>
                ''',
                recipientProviders: [developers(), requestor()]
            )
        }

        failure {
            echo '========== Pipeline failed! =========='
            emailext(
                subject: "❌ FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: '''
                    <p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'</p>
                    <p>Check console output at: <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>
                ''',
                recipientProviders: [developers(), requestor()]
            )
        }

        always {
            echo '========== Cleaning workspace =========='
            cleanWs()
        }
    }
}

