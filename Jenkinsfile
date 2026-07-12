def gv

pipeline {
    agent any
    tools {
        maven "maven-3.9"
    }

    environment {
        // VERSION = "1.0"
        IMAGE_NAME = "jeremyqindevops/java-maven-1.0"
    }

    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
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