pipeline{
    agent {
        docker {
            image 'maven:3.8.3-adoptopenjdk-8'
            args '-u root'
        }
    }

    stages{
        stage("Build"){
            steps{
                configFileProvider([configFile(fileId: 'hibernate-cfg-twittercitydataminer', targetLocation: 'src/main/resources/hibernate.cfg.xml')]) {}
                configFileProvider([configFile(fileId: 'oauth-cfg-twittercitydataminer', targetLocation: 'src/main/resources/oauth.xml')]) {}
                sh 'echo $JAVA_HOME'
                sh 'mvn -B -DskipTests clean package'
            }
        }
    

        stage("Deploy"){
            steps{
                withCredentials([sshUserPrivateKey(credentialsId: 'okeanos-server-ssh', keyFileVariable: 'keyfile', passphraseVariable: '', usernameVariable: 'username')]) {
                    sh "scp -i ${keyfile} -o StrictHostKeyChecking=no target/TwitterDataMiner.jar ${username}@twittercity.bravecode.gr:/home/${username}/twittercity-services/data-mine-service/TwitterDataMiner.jar"
                }
            }
        }
    }
}