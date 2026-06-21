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
    environment {
        NEW_VERSION = "1.2.0"
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
            steps {
                echo "building image of version ${NEW_VERSION}"
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