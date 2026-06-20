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
    }
    stages {

        stage("build") {
            when {
                expression { 
                    // env.BRANCH_NAME == "jenkins"
                    return env.GIT_BRANCH == "origin/jenkins" || env.GIT_BRANCH == "jenkins"  
                }
            }
            steps {
                echo "building jar of version ${NEW_VERSION}"
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