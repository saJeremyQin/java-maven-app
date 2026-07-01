def gv

pipeline {
    agent any
    tools {
        maven "maven-3.9"
    }

    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("increment build number") {
            steps {
                script {
                    echo "incrementing build number"
                    gv.incrementBuildNumber()
                }
            }
        }
        stage("build jar") {

            steps {
                script {
                    // echo "building jar"
                    gv.buildJar()
                    echo "Executing pipeline for branch ${env.BRANCH_NAME}"
                }
            }
        }
        stage("build") {
            when {
                expression {
                    return env.BRANCH_NAME == "jenkins" || env.BRANCH_NAME == "master"
                }
            }
            steps {
                script {
                    // echo "building image"
                    gv.buildImage()
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    // echo "deploying"
                    gv.deployApp()
                }
            }
        }
    }   
}