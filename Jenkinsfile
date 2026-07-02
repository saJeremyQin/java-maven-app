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
                    env.ACTIVE_BRANCH = (env.BRANCH_NAME ?: env.GIT_BRANCH ?: env.CHANGE_BRANCH ?: "")
                        .replaceFirst(/^origin\//, "")
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
                    echo "Executing pipeline for branch ${env.ACTIVE_BRANCH}"
                }
            }
        }
        stage("build") {
            when {
                expression {
                    return env.ACTIVE_BRANCH == "jenkins" || env.ACTIVE_BRANCH == "master"
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