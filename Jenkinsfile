#!/usr/bin/env groovy
@Library('jenkins-shared-library@master') 

def gv

pipeline {
    agent any
    tools {
        maven "maven-3.9"
    }
    parameters {
        choice(name: 'VERSION', choices: ['1.2.0', '1.3.0', '1.4.0'], description: 'Version to build and deploy')
        booleanParam(name: 'RUN_TESTS', defaultValue: false, description: 'Whether to run tests')
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
            when {
                expression {
                    return params.RUN_TESTS
                }
            }
            steps {
                script {
                    // echo "building jar"
                    // gv.buildJar()
                    buildJar()
                    echo "Executing pipeline for branch ${env.BRANCH_NAME} and version ${params.VERSION}"
                }
            }
        }
        stage("build") {
            when {
                expression {
                    return env.BRANCH_NAME == "master"
                }
            }
            steps {
                script {
                    // echo "building image"
                    // gv.buildImage()
                    buildImage()
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