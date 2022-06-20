pipeline{
    agent any
    tools {
        jdk "JDK 8"
        maven 'Maven 3.6.3'
    }

    stages{
        stage("Build"){
            steps{
                configFileProvider([configFile(fileId: 'hibernate-cfg-twittercitydataminer', targetLocation: 'src/main/resources/hibernate.cfg.xml')]) {}
                configFileProvider([configFile(fileId: 'oauth-cfg-twittercitydataminer', targetLocation: 'src/main/resources/oauth.xml')]) {}    
                sh("mkdir -p $HOME/.m2/repository")
                sh 'mvn clean package'
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