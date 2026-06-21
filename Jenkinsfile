// def gv

// pipeline {
//     agent any
//     tools {
//         jdk 'jdk21'
//     }
//     stages {
//         stage("init") {
//             steps {
//                 script {
//                     gv = load "script.groovy"
//                 }
//             }
//         }
//         stage("build jar") {
//             steps {
//                 script {
//                     echo "building jar"
//                     //gv.buildJar()
//                 }
//             }
//         }
//         stage("build image") {
//             steps {
//                 script {
//                     echo "building image"
//                     //gv.buildImage()
//                 }
//             }
//         }
//         stage("deploy") {
//             steps {
//                 script {
//                     echo "deploying"
//                     //gv.deployApp()
//                 }
//             }
//         }
//     }   
// }



pipeline {
    agent any
    parameters {
        string(name: 'NEW_VERSION', defaultValue: '1.2.0', description: 'Version to build and deploy')
        booleanParam(name: 'RUN_TESTS', defaultValue: false, description: 'Whether to run tests')
    }
    environment {
        NEW_VERSION = "${params.NEW_VERSION}"
        // DOCKER_CREDS = credentials("docker-hub-credentials")
    }
    stages {

        stage("build") {
            when {
                expression { 
                    return env.BRANCH_NAME == "jenkins" || env.GIT_BRANCH == "origin/jenkins" || env.GIT_BRANCH == "jenkins"
                }
            }
            steps {
                echo "building jar of version ${NEW_VERSION}"
                withCredentials([
                    usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PWD')
                ]) {
                    // sh '''
                    //     echo "$DOCKER_PWD" | docker login -u "$DOCKER_USER" --password-stdin
                    // '''
                    //sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PWD}"
                    sh "echo ${DOCKER_PWD} | docker login -u ${DOCKER_USER} --password-stdin"

                }
            }
        }
        stage("test") {
            when {
                expression { return params.RUN_TESTS }
            }
            steps {
                echo "running tests for version ${NEW_VERSION}"
            }
        }
        stage("deploy") {
            steps {
                echo "deploying version ${NEW_VERSION}  to production"
            }
        }
    } 
    post {
        always {
            echo "cleaning up"
        }
    }  
}