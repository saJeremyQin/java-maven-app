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

    stages {

        stage("build") {
            when {
                expression { 
                    BRANCH_NAME == "jenkins"
                 }
            }
            steps {
                echo "building jar"
            }
        }
        stage("test") {
            steps {
                echo "building image"
            }
        }
        stage("deploy") {
            steps {
                echo "deploying"
            }
        }
    } 
    post {
        always {
            echo "cleaning up"
        }
    }  
}