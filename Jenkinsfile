#!/usr/bin/env groovy
@Library('jenkins-shared-library@master')_

pipeline {
    agent any
    tools {
        maven "maven-3.9"
    }
    environment {
        IMAGE_NAME = "jeremyqindevops/demo-app:java-maven-1.0"
        DOCKER_PLATFORMS = "linux/amd64,linux/arm64"
    }

    stages {
        // stage("init") {
        //     steps {
        //         script {
        //             gv = load "script.groovy"
        //         }
        //     }
        // }
        stage("agent-diagnostics") {
            steps {
            sh '''
                echo "NODE_NAME=$NODE_NAME"
                echo "EXECUTOR_NUMBER=$EXECUTOR_NUMBER"
                uname -m
                docker info --format '{{.Architecture}}'
                env | grep -i DOCKER_DEFAULT_PLATFORM || true
            '''
            }
        }
        stage("test") {

            steps {
                script {
                    // echo "building jar"
                    // gv.buildJar()
                    buildJar("$env.BRANCH_NAME")
                    echo "Executing pipeline for branch ${env.BRANCH_NAME}"
                }
            }
        }
        stage("build") {
            // when {
            //     expression {
            //         return env.BRANCH_NAME == "master"
            //     }
            // }
            steps {
                script {
                    sh '''
                        set -e
                        docker buildx create --name multiarch-builder --use >/dev/null 2>&1 || true
                        docker buildx inspect --bootstrap
                        docker buildx build \
                          --platform "$DOCKER_PLATFORMS" \
                          -t "$IMAGE_NAME" \
                          --push \
                          .
                    '''
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    // echo "deploying"
                    // gv.deployApp()
                    echo "deploying the application to EC2 instance..."
                    def dockerCmd = "docker run -d -p 8080:8080 ${env.IMAGE_NAME}"
                    withCredentials([sshUserPrivateKey(
                        credentialsId: 'ec2-ssh-key',
                        keyFileVariable: 'KEY_FILE',
                        usernameVariable: 'SSH_USER')]) {

                            sh """
                                chmod 400 $KEY_FILE
                                ssh -o StrictHostKeyChecking=no -i $KEY_FILE $SSH_USER@3.107.252.89 ${dockerCmd}
                            """       
                    }
                }
            }
        }
    }   
}