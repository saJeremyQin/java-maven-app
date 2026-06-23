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
        stage("build jar") {
            steps {
                script {
                    // echo "building jar"
                    gv.buildJar()
                }
            }
        }
        stage("build image") {
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

