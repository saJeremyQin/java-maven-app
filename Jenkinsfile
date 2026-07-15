def gv

pipeline {
    agent any
    tools {
        maven "maven-3.9"
    }

    environment {
        IMAGE_REPO = "jeremyqindevops/java-maven-app"
    }

    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("version bump") {
            steps {
                script {
                    def appVersion = gv.bumpPomVersionAndPush()
                    def branchTag = env.BRANCH_NAME.replaceAll('[^A-Za-z0-9_.-]', '-').toLowerCase()

                    env.APP_VERSION = appVersion
                    env.IMAGE_NAME = "${env.IMAGE_REPO}:${branchTag}-${env.APP_VERSION}"

                    echo "Resolved version: ${env.APP_VERSION}"
                    echo "Resolved image: ${env.IMAGE_NAME}"
                }
            }
        }
        stage("test") {

            steps {
                script {
                    echo "Testing the application"
                }
            }
        }
        stage("build app") {
         
            steps {
                script {
                    // echo "building the application"
                    gv.buildJar()
                }
            }
        }
        stage("build image") {
            steps {
                script {
                    // echo "building the docker image"
                    gv.buildImage()
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    echo "deploying the application"
                    def dockerCmd = "docker run -d -p 8080:8080 ${env.IMAGE_NAME}"
                    withCredentials([sshUserPrivateKey(
                        credentialsId: 'ec2-ssh-key',
                        keyFileVariable: 'KEY_FILE',
                        usernameVariable: 'SSH_USER')]) {

                            sh """
                                chmod 400 $KEY_FILE
                                ssh -o StrictHostKeyChecking=no -i $KEY_FILE $SSH_USER@13.211.234.57 ${dockerCmd}
                            """       
                    }
                }
            }
        }
    }   
}